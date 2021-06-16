package ua.com.d_garage.deutschegarage.data.repository.note;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingSource;
import ua.com.d_garage.deutschegarage.data.local.db.dao.NoteDao;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithParts;

import java.util.concurrent.ExecutorService;

public class NoteRepository {

    private final NoteDao noteDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Long> noteIdLiveData;
    private final MutableLiveData<NoteWithParts> noteWithPartsLiveData;

    public NoteRepository(NoteDao noteDao, ExecutorService executorService) {
        this.noteDao = noteDao;
        this.executorService = executorService;
        noteIdLiveData = new MutableLiveData<>();
        noteWithPartsLiveData = new MutableLiveData<>();
    }

    public LiveData<Long> save(Note note) {
        executorService.execute(() -> {
            long id = noteDao.insert(note);
            noteIdLiveData.postValue(id);
        });
        return noteIdLiveData;
    }

    public void delete(Note note) {
        executorService.execute(() -> noteDao.delete(note));
    }

    public LiveData<NoteWithParts> getDescription(long id) {
        executorService.execute(()-> noteWithPartsLiveData.postValue(noteDao.findNoteWithParts(id)));
        return noteWithPartsLiveData;
    }

    public PagingSource<Integer, Note> getPagingSource() {
        return noteDao.findNotePagingSourceSortedByDate();
    }

}
