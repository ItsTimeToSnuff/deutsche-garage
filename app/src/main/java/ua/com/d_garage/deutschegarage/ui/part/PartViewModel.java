package ua.com.d_garage.deutschegarage.ui.part;

import android.app.Application;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

public class PartViewModel extends BaseViewModel<PartNavigator> {

    public static final String TAG = PartViewModel.class.getSimpleName();

    public PartViewModel(Application application) {
        super(application);
    }
}
