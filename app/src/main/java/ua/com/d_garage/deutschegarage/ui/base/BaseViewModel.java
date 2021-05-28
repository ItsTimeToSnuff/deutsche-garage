package ua.com.d_garage.deutschegarage.ui.base;

import android.app.Application;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;

import java.lang.ref.WeakReference;

public abstract class BaseViewModel<N> extends AndroidViewModel {

    private final ObservableBoolean isLoading;

    private WeakReference<N> navigator;

    public BaseViewModel(Application application) {
        super(application);
        isLoading = new ObservableBoolean();
    }

    public ObservableBoolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }

    public N getNavigator() {
        return navigator.get();
    }

    public void setNavigator(N navigator) {
        this.navigator = new WeakReference<>(navigator);
    }
}
