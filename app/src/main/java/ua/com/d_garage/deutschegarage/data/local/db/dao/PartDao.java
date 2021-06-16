package ua.com.d_garage.deutschegarage.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

@Dao
public interface PartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Part part);

}
