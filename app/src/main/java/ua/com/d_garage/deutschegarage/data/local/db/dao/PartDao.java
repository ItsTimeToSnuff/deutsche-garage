package ua.com.d_garage.deutschegarage.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

@Dao
public interface PartDao {

    @Insert
    Long insert(Part part);

    @Transaction
    @Query("SELECT * FROM Part WHERE vin = :vin")
    Part findPartByVin(long vin);

}
