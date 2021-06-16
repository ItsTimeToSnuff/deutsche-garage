package ua.com.d_garage.deutschegarage.ui.note.note;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.local.db.AppDatabase;
import ua.com.d_garage.deutschegarage.data.local.prefs.AppPreferences;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithParts;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.repository.note.NoteRepository;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

import java.util.Map;
import java.util.concurrent.Executors;

public class NoteViewModel extends BaseViewModel<NoteNavigator> {

    private final AppPreferences prefs;
    private final NoteRepository noteRepository;
    private final MutableLiveData<Long> noteIdLiveData;
    private final LiveData<NoteWithParts> noteWithPartsLiveData;

    public NoteViewModel(Application application) {
        super(application);
        prefs = AppPreferences.getInstance(application);
        noteRepository = new NoteRepository(AppDatabase.getInstance(application).getNoteDao(), Executors.newCachedThreadPool());
        noteIdLiveData = new MutableLiveData<>();
        noteWithPartsLiveData = Transformations.switchMap(noteIdLiveData, noteRepository::getDescription);
    }

    public LiveData<NoteWithParts> getNoteWithPartsLiveData() {
        return noteWithPartsLiveData;
    }

    public void loadDescription(long id) {
        noteIdLiveData.postValue(id);
    }

    public void deleteNote() {
        NoteWithParts noteWithParts = noteWithPartsLiveData.getValue();
        if (noteWithParts != null) {
            Note note = noteWithParts.getNote();
            noteRepository.delete(note);
            if (prefs.getNoteTitle().equals(note.getTitle())) {
                prefs.saveIsRecording(false);
                prefs.saveNoteTitle(AppPreferences.NOTE_TITLE_DEFAULT);
                prefs.saveNoteId(AppPreferences.NOTE_ID_DEFAULT);
            }
        }
    }

    public String getShareableNote(Map<Part, Long> counts) {
        final StringBuilder sb = new StringBuilder();
        NoteWithParts noteWithParts = noteWithPartsLiveData.getValue();
        if (noteWithParts != null) {
            sb.append("#########################");
            sb.append("\n");
            sb.append("## ");
            sb.append(noteWithParts.getNote().getTitle());
            sb.append("\n");
            sb.append("## ");
            sb.append(noteWithParts.getNote().getDate().toLocalDate());
            sb.append("\n");
            sb.append("#########################");
            sb.append("\n");
            counts.forEach((part, count) -> {
                sb.append("-------------------------------------------------------");
                sb.append("\n");
                sb.append(getApplication().getString(R.string.part_vin_name));
                sb.append(" ");
                sb.append(part.getVin());
                sb.append("\n");
                sb.append(getApplication().getString(R.string.part_name));
                sb.append(" ");
                sb.append(part.getName());
                sb.append("\n");
                sb.append(getApplication().getString(R.string.part_count));
                sb.append(" ");
                sb.append(count);
                sb.append("\n");
            });
        }
        return sb.toString();
    }
}
