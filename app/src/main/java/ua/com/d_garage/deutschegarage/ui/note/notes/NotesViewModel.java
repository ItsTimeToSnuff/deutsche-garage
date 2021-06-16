package ua.com.d_garage.deutschegarage.ui.note.notes;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import ua.com.d_garage.deutschegarage.data.local.db.AppDatabase;
import ua.com.d_garage.deutschegarage.data.model.note.Note;
import ua.com.d_garage.deutschegarage.data.repository.note.NoteRepository;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

import java.util.concurrent.Executors;

public class NotesViewModel extends BaseViewModel<NotesNavigator> {

    private final NoteRepository noteRepository;
    private final LiveData<PagingData<Note>> pagingDataLiveData;

    public NotesViewModel(Application application) {
        super(application);
        noteRepository = new NoteRepository(AppDatabase.getInstance(application).getNoteDao(), Executors.newCachedThreadPool());
        Pager<Integer, Note> pager = new Pager<>(new PagingConfig(20), noteRepository::getPagingSource);
        pagingDataLiveData = PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), this);
    }

    public LiveData<PagingData<Note>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }

    public void onItemClicked(long id) {
        getNavigator().openDescription(id);
    }
}
