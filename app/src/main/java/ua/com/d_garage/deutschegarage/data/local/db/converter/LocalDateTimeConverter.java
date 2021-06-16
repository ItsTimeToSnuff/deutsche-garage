package ua.com.d_garage.deutschegarage.data.local.db.converter;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public final class LocalDateTimeConverter {

    private LocalDateTimeConverter() {
        throw new IllegalStateException();
    }

    @TypeConverter
    public static LocalDateTime toDate(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            return LocalDateTime.parse(dateString);
        }
    }

    @TypeConverter
    public static String toDateString(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }

}
