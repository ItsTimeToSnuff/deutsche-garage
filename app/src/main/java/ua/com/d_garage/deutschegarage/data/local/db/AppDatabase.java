package ua.com.d_garage.deutschegarage.data.local.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import ua.com.d_garage.deutschegarage.data.local.db.converter.LocalDateTimeConverter;
import ua.com.d_garage.deutschegarage.data.local.db.dao.NoteDao;
import ua.com.d_garage.deutschegarage.data.local.db.dao.PartDao;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

@Database(entities = {Note.class, Part.class}, version = 1)
@TypeConverters(LocalDateTimeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "deutsche-garage-db").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract NoteDao getNoteDao();

    public abstract PartDao getPartDao();

}
