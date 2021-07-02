package ua.com.d_garage.deutschegarage.ui.scanner;

import android.app.Application;
import androidx.camera.core.Camera;
import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import ua.com.d_garage.deutschegarage.data.local.db.AppDatabase;
import ua.com.d_garage.deutschegarage.data.local.prefs.AppPreferences;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItem;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.remote.HttpClient;
import ua.com.d_garage.deutschegarage.data.remote.part.PartHtmlParser;
import ua.com.d_garage.deutschegarage.data.remote.part.PartRemoteDataSource;
import ua.com.d_garage.deutschegarage.data.repository.note.NoteRepository;
import ua.com.d_garage.deutschegarage.data.repository.part.PartRepository;
import ua.com.d_garage.deutschegarage.data.service.barcode.BarcodeCameraService;
import ua.com.d_garage.deutschegarage.data.service.camera.CameraService;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerViewModel extends BaseViewModel<ScannerNavigator> {

    private static final String TAG = ScannerViewModel.class.getSimpleName();

    private final AppPreferences prefs;
    private final PartRepository partRepository;
    private final NoteRepository noteRepository;
    private final MutableLiveData<CameraService<Long>> cameraServiceLiveData;
    private final MediatorLiveData<Camera> camera;
    private final MediatorLiveData<Boolean> isFlashOn;
    private final MediatorLiveData<Long> result;
    private final MutableLiveData<String> saveTitle;
    private final MutableLiveData<String> noteTitle;
    private final LiveData<Note> note;
    private final MutableLiveData<Boolean> isRecording;
    private final MutableLiveData<Long> vinPartLiveData;
    private final LiveData<Part> partLiveData;

    private CameraService<Long> cameraService;

    public ScannerViewModel(Application application) {
        super(application);
        prefs = AppPreferences.getInstance(application);
        AppDatabase database = AppDatabase.getInstance(application);
        ExecutorService executorService = Executors.newCachedThreadPool();
        PartHtmlParser partHtmlParser = new PartHtmlParser(executorService);
        PartRemoteDataSource partRemoteDataSource = new PartRemoteDataSource(HttpClient.getClient(), partHtmlParser);
        partRepository = new PartRepository(executorService, partRemoteDataSource, database.getPartDao());
        cameraServiceLiveData = new MutableLiveData<>();
        camera = new MediatorLiveData<>();
        isFlashOn = new MediatorLiveData<>();
        isFlashOn.setValue(false);
        isFlashOn.addSource(isFlashOn, isEnable -> cameraService.enableFlash(isEnable));
        noteRepository = new NoteRepository(database.getNoteDao(), executorService);
        noteTitle = new MutableLiveData<>(prefs.getNoteTitle());
        saveTitle = new MutableLiveData<>();
        note = Transformations.switchMap(saveTitle, title->noteRepository.save(new Note(null, title, LocalDateTime.now())));
        isRecording = new MutableLiveData<>(prefs.getIsRecording());
        result = new MediatorLiveData<>();
        vinPartLiveData = new MutableLiveData<>();
        partLiveData = Transformations.switchMap(vinPartLiveData, partRepository::getPart);
    }

    public void initOrResumeScanner(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider, BarcodeSizePair sizePair) {
        if (cameraService == null) {
            cameraService = new BarcodeCameraService(getApplication(), lifecycleOwner, surfaceProvider, sizePair);
            camera.addSource(cameraService.getCamera(), camera::setValue);
            result.addSource(cameraService.getObservableResult(), barcode -> {
                result.setValue(barcode);
                Boolean isRec = isRecording.getValue();
                long id = prefs.getNoteId();
                if (isRec != null && id != AppPreferences.NOTE_ID_DEFAULT) {
                    if (isRec) {
                        vinPartLiveData.setValue(barcode);
                    }
                }
            });
            cameraServiceLiveData.setValue(cameraService);
        } else {
            cameraService.rebindUseCases(lifecycleOwner, surfaceProvider);
            isFlashOn.setValue(isFlashOn.getValue());
        }
    }

    public void saveNoteItem(int partQuantity) {
        noteRepository.save(new NoteItem(null, prefs.getNoteId(), Objects.requireNonNull(partLiveData.getValue()).getId(), partQuantity));
    }

    public void saveNote(String title) {
        saveTitle.setValue(title);
    }

    public void startRecording(Long id) {
        setRecording(true, saveTitle.getValue(), id);
    }

    public void stopRecording() {
        setRecording(false, AppPreferences.NOTE_TITLE_DEFAULT, AppPreferences.NOTE_ID_DEFAULT);
    }

    public void stopScanner() {
        if (cameraService != null) {
            cameraService.stop();
        }
    }

    public void flashOnOff() {
        Boolean value = isFlashOn.getValue();
        if (value != null) {
            isFlashOn.setValue(!value);
        }
    }

    public void close() {
        getNavigator().closeScanner();
    }

    public LiveData<CameraService<Long>> getCameraServiceLiveData() {
        return cameraServiceLiveData;
    }

    public LiveData<Long> getBarcodeResult() {
        return result;
    }

    public LiveData<Camera> getCamera() {
        return camera;
    }

    public LiveData<Boolean> getIsFlashOn() {
        return isFlashOn;
    }

    public LiveData<String> getNoteTitle() {
        return noteTitle;
    }

    public LiveData<String> getSaveTitle() {
        return saveTitle;
    }

    public LiveData<Note> getNote() {
        return note;
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<Part> getPartLiveData() {
        return partLiveData;
    }

    private void setRecording(boolean isRec, String noteTitle, long id) {
        prefs.saveIsRecording(isRec);
        prefs.saveNoteTitle(noteTitle);
        prefs.saveNoteId(id);
        isRecording.setValue(isRec);
        this.noteTitle.setValue(noteTitle);
    }

}
