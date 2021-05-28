package ua.com.d_garage.deutschegarage.ui.main;

import android.app.Application;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

public class MainViewModel extends BaseViewModel<MainNavigator> {

    public static final String TAG = MainViewModel.class.getSimpleName();

    public MainViewModel(Application application) {
        super(application);
    }

    public void onBarcodeActivityPressed() {
        getNavigator().openBarcodeActivity();
    }

    public void onExitPressed() {
        getNavigator().exit();
    }
}
