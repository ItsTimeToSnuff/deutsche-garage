package ua.com.d_garage.deutschegarage.ui.scanner.record;

import android.app.Application;
import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

public class StartRecordingViewModel extends BaseViewModel<StartRecordingNavigator> {

    private final MediatorLiveData<String> noteTitle;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> isErrorShow;

    public StartRecordingViewModel(Application application) {
        super(application);
        noteTitle = new MediatorLiveData<>();
        errorMessage = new MutableLiveData<>();
        isErrorShow = new MutableLiveData<>();
        noteTitle.addSource(noteTitle, t-> isErrorShow.postValue(false));
    }

    public void onStartClicked() {
        String titleValue = noteTitle.getValue();
        if (isNameValid(titleValue)) {
            getNavigator().start(titleValue);
        }
    }

    public void onCancelClicked() {
        getNavigator().cancel();
    }

    public MutableLiveData<String> getNoteTitle() {
        return noteTitle;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsErrorShow() {
        return isErrorShow;
    }

    private boolean isNameValid(String noteTitle) {
        if (TextUtils.isEmpty(noteTitle)) {
            isErrorShow.postValue(true);
            errorMessage.postValue(getApplication().getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

}
