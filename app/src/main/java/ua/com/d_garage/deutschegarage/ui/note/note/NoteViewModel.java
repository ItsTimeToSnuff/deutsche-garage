package ua.com.d_garage.deutschegarage.ui.note.note;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.local.db.AppDatabase;
import ua.com.d_garage.deutschegarage.data.local.prefs.AppPreferences;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithNoteItems;
import ua.com.d_garage.deutschegarage.data.repository.note.NoteRepository;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

import java.util.concurrent.Executors;

public class NoteViewModel extends BaseViewModel<NoteNavigator> {

    private final AppPreferences prefs;
    private final NoteRepository noteRepository;
    private final MutableLiveData<Long> noteIdLiveData;
    private final LiveData<NoteWithNoteItems> noteWithPartsLiveData;

    public NoteViewModel(Application application) {
        super(application);
        prefs = AppPreferences.getInstance(application);
        noteRepository = new NoteRepository(AppDatabase.getInstance(application).getNoteDao(), Executors.newCachedThreadPool());
        noteIdLiveData = new MutableLiveData<>();
        noteWithPartsLiveData = Transformations.switchMap(noteIdLiveData, noteRepository::getDescription);
    }

    public LiveData<NoteWithNoteItems> getNoteWithNoteItemsLiveData() {
        return noteWithPartsLiveData;
    }

    public void loadDescription(long id) {
        noteIdLiveData.postValue(id);
    }

    public void deleteNote() {
        NoteWithNoteItems noteWithNoteItems = noteWithPartsLiveData.getValue();
        if (noteWithNoteItems != null) {
            Note note = noteWithNoteItems.getNote();
            noteRepository.delete(note);
            if (prefs.getNoteTitle().equals(note.getTitle())) {
                prefs.saveIsRecording(false);
                prefs.saveNoteTitle(AppPreferences.NOTE_TITLE_DEFAULT);
                prefs.saveNoteId(AppPreferences.NOTE_ID_DEFAULT);
            }
        }
    }

    public String getShareableNote() {
        final StringBuilder sb = new StringBuilder();
        NoteWithNoteItems noteWithNoteItems = noteWithPartsLiveData.getValue();
        if (noteWithNoteItems != null) {
            sb.append("#########################");
            sb.append("\r\n");
            sb.append("## ");
            sb.append(noteWithNoteItems.getNote().getTitle());
            sb.append("\r\n");
            sb.append("## ");
            sb.append(noteWithNoteItems.getNote().getDate().toLocalDate());
            sb.append("\r\n");
            sb.append("#########################");
            sb.append("\r\n");
            noteWithNoteItems.getNoteItems().forEach((noteItemWithPart) -> {
                sb.append("-------------------------------------------------------");
                sb.append("\r\n");
                sb.append(getApplication().getString(R.string.part_number_name));
                sb.append(" ");
                sb.append(noteItemWithPart.getPart().getPartNumber());
                sb.append("\r\n");
                sb.append(getApplication().getString(R.string.part_name));
                sb.append(" ");
                sb.append(noteItemWithPart.getPart().getName());
                sb.append("\r\n");
                sb.append(getApplication().getString(R.string.part_count));
                sb.append(" ");
                sb.append(noteItemWithPart.getQuantity());
                sb.append("\r\n");
            });
        }
        return sb.toString();
    }
}
