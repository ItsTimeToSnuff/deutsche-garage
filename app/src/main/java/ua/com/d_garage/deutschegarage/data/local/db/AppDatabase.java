package ua.com.d_garage.deutschegarage.data.local.db;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.data.local.db.converter.LocalDateTimeConverter;
import ua.com.d_garage.deutschegarage.data.local.db.dao.NoteDao;
import ua.com.d_garage.deutschegarage.data.local.db.dao.PartDao;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItem;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItemWithPart;
import ua.com.d_garage.deutschegarage.data.model.part.Part;

@Database(
        entities = {Note.class, Part.class, NoteItem.class},
        views = {NoteItemWithPart.class},
        version = 3
)
@TypeConverters(LocalDateTimeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `PartTemp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `partNumber` INTEGER NOT NULL, `name` TEXT)");
            database.execSQL("INSERT INTO `PartTemp` (`id`, `partNumber`, `name`) SELECT `id`, `vin`, `name` FROM `Part`");
            database.execSQL("DROP TABLE `Part`");
            database.execSQL("ALTER TABLE `PartTemp` RENAME TO `Part`");
            database.execSQL("CREATE UNIQUE INDEX `index_Part_partNumber` ON `Part` (`partNumber`)");
        }
    };

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            // Create NoteItem table
            database.execSQL(
                    "CREATE TABLE `NoteItem` " +
                            "(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " `noteId` INTEGER," +
                            " `partId` INTEGER," +
                            " `quantity` INTEGER NOT NULL," +
                            " FOREIGN KEY(`noteId`) REFERENCES `Note`(`id`) ON UPDATE CASCADE ON DELETE CASCADE ," +
                            " FOREIGN KEY(`partId`) REFERENCES `Part`(`id`) ON UPDATE CASCADE ON DELETE CASCADE)"
            );
            database.execSQL("CREATE INDEX `index_NoteItem_noteId` ON `NoteItem` (`noteId`)");
            database.execSQL("CREATE INDEX `index_NoteItem_partId` ON `NoteItem` (`partId`)");

            // Fill the NoteItem table with values from the old Part table,
            // removal of duplicate parts, counting duplicate entry as quantity of part for each noteItem,
            // and update Part table.
            database.execSQL(
                    "CREATE TABLE `NoteItemTemp` " +
                            "(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " `noteId` INTEGER," +
                            " `partId` INTEGER," +
                            " `quantity` INTEGER NOT NULL)");
            database.execSQL(
                    "INSERT INTO `NoteItemTemp` (`noteId`, `partId`, `quantity`) " +
                            "SELECT `noteId`, `id` AS `partId`, COUNT(`vin`) AS `quantity` " +
                            "FROM `Part` " +
                            "GROUP BY `noteId`, `vin`"
            );
            database.execSQL("CREATE TABLE `PartTemp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `vin` INTEGER NOT NULL, `name` TEXT)");
            database.execSQL("INSERT INTO `PartTemp` (`vin`, `name`) SELECT `vin`, `name` FROM `Part` GROUP BY `vin`");
            database.execSQL(
                    "INSERT INTO NoteItem (noteId, partId, quantity) " +
                            "SELECT NoteItemTemp.noteId AS note_id, PartTemp.id AS part_id, NoteItemTemp.quantity AS part_quantity " +
                            "FROM NoteItemTemp " +
                            "JOIN PartTemp, Part ON NoteItemTemp.partId = Part.id " +
                            "WHERE Part.vin = PartTemp.vin"
            );
            database.execSQL("DROP TABLE `NoteItemTemp`");
            database.execSQL("DROP TABLE `Part`");
            database.execSQL("ALTER TABLE `PartTemp` RENAME TO `Part`");
            database.execSQL("CREATE UNIQUE INDEX `index_Part_vin` ON `Part` (`vin`)");

            // Create db view to retrieve a NoteItem with full Part entity
            database.execSQL(
                    "CREATE VIEW `NoteItemWithPart` " +
                            "AS SELECT Note.id as noteId, Part.*, NoteItem.quantity " +
                            "FROM NoteItem " +
                            "INNER JOIN Note, Part " +
                            "WHERE noteId = Note.id AND partId = Part.id"
            );

            // Update Note table
            database.execSQL("CREATE TABLE `NoteBackup` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `title` TEXT, `date` TEXT)");
            database.execSQL("INSERT INTO `NoteBackup` (`id`, `title`, `date`) SELECT `id`, `title`, `date` FROM `Note`");
            database.execSQL("DROP TABLE `Note`");
            database.execSQL("ALTER TABLE `NoteBackup` RENAME TO `Note`");
        }
    };

    private static final Migration[] MIGRATIONS = new Migration[]{MIGRATION_1_2, MIGRATION_2_3};

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context, AppDatabase.class, "deutsche-garage-db")
                            .addMigrations(MIGRATIONS)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract NoteDao getNoteDao();

    public abstract PartDao getPartDao();

}
