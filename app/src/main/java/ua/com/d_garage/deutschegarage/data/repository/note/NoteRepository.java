package ua.com.d_garage.deutschegarage.data.repository.note;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingSource;
import ua.com.d_garage.deutschegarage.data.local.db.dao.NoteDao;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItem;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithNoteItems;

import java.util.concurrent.ExecutorService;

public class NoteRepository {

    private final NoteDao noteDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Note> saveNoteLiveData;
    private final MutableLiveData<NoteWithNoteItems> noteWithNoteItemsLiveData;
    private final MutableLiveData<NoteItem> saveNoteItemLiveData;

    public NoteRepository(NoteDao noteDao, ExecutorService executorService) {
        this.noteDao = noteDao;
        this.executorService = executorService;
        saveNoteLiveData = new MutableLiveData<>();
        noteWithNoteItemsLiveData = new MutableLiveData<>();
        saveNoteItemLiveData = new MutableLiveData<>();
    }

    public LiveData<Note> save(Note note) {
        executorService.execute(() -> {
            if (note == null) {
                return;
            }
            long id = noteDao.insert(note);
            Note saveNote = new Note(id, note.getTitle(), note.getDate());
            saveNoteLiveData.postValue(saveNote);
        });
        return saveNoteLiveData;
    }

    public LiveData<NoteItem> save(NoteItem noteItem) {
        executorService.execute(() -> {
            if (noteItem == null) {
                return;
            }
            NoteItem exist = noteDao.findNoteItemWithNoteIdAndPartId(noteItem.getNoteId(), noteItem.getPartId());
            if (exist != null) {
                int newQuantity = exist.getQuantity() + noteItem.getQuantity();
                NoteItem update = new NoteItem(exist.getId(), exist.getNoteId(), exist.getPartId(), newQuantity);
                noteDao.update(update);
                saveNoteItemLiveData.postValue(update);
            } else {
                long id = noteDao.insert(noteItem);
                NoteItem saveNote = new NoteItem(id, noteItem.getNoteId(), noteItem.getPartId(), noteItem.getQuantity());
                saveNoteItemLiveData.postValue(saveNote);
            }
        });
        return saveNoteItemLiveData;
    }

    public void delete(Note note) {
        executorService.execute(() -> noteDao.delete(note));
    }

    public LiveData<NoteWithNoteItems> getDescription(Long id) {
        executorService.execute(() -> noteWithNoteItemsLiveData.postValue(noteDao.findNoteWithNoteItems(id)));
        return noteWithNoteItemsLiveData;
    }

    public PagingSource<Integer, Note> getPagingSource() {
        return noteDao.findNotePagingSourceSortedByDate();
    }

}
