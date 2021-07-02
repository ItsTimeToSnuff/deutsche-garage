package ua.com.d_garage.deutschegarage.data.local.db.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItem;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithNoteItems;

@Dao
public interface NoteDao {

    @Insert
    Long insert(Note note);

    @Insert
    Long insert(NoteItem noteItem);

    @Update
    void update(NoteItem noteItem);

    @Delete
    void delete(Note note);

    @Transaction
    @Query("SELECT * FROM Note WHERE id = :id")
    NoteWithNoteItems findNoteWithNoteItems(Long id);

    @Transaction
    @Query("SELECT * FROM NoteItem WHERE noteId = :noteId AND partId = :partId")
    NoteItem findNoteItemWithNoteIdAndPartId(Long noteId, Long partId);

    @Transaction
    @Query("SELECT * FROM Note ORDER BY date DESC")
    PagingSource<Integer, Note> findNotePagingSourceSortedByDate();

}
