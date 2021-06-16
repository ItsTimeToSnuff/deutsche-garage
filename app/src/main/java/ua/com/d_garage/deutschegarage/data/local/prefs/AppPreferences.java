package ua.com.d_garage.deutschegarage.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {

    public static final String NOTE_TITLE_DEFAULT = " ";
    public static final long NOTE_ID_DEFAULT = -1;

    private static final String PREFS_NAME = "deutsche-garage-prefs";
    private static final String NOTE_TITLE_KEY = "note_title";
    private static final String NOTE_ID_KEY = "note_id";
    private static final String IS_RECORDING_KEY = "is_recording";

    private static AppPreferences INSTANCE;

    private final SharedPreferences prefs;

    private AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static AppPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppPreferences(context);
                }
            }
        }
        return INSTANCE;
    }

    public void saveNoteTitle(String noteTitle) {
        prefs.edit().putString(NOTE_TITLE_KEY, noteTitle).apply();
    }

    public String getNoteTitle() {
        return prefs.getString(NOTE_TITLE_KEY, NOTE_TITLE_DEFAULT);
    }

    public void saveNoteId(long noteId) {
        prefs.edit().putLong(NOTE_ID_KEY, noteId).apply();
    }

    public long getNoteId() {
        return prefs.getLong(NOTE_ID_KEY, NOTE_ID_DEFAULT);
    }

    public void saveIsRecording(boolean isRecording) {
        prefs.edit().putBoolean(IS_RECORDING_KEY, isRecording).apply();
    }

    public boolean getIsRecording() {
        return prefs.getBoolean(IS_RECORDING_KEY, false);
    }

}
