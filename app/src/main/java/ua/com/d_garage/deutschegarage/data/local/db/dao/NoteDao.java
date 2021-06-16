package ua.com.d_garage.deutschegarage.data.local.db.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteWithParts;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Note note);

    @Delete
    void delete(Note note);

    @Transaction
    @Query("SELECT * FROM Note WHERE id = :id")
    NoteWithParts findNoteWithParts(long id);

    @Transaction
    @Query("SELECT * FROM Note ORDER BY date DESC")
    PagingSource<Integer, Note> findNotePagingSourceSortedByDate();

}
