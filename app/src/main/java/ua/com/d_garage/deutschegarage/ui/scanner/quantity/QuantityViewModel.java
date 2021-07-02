package ua.com.d_garage.deutschegarage.ui.scanner.quantity;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

public class QuantityViewModel extends BaseViewModel<QuantityNavigator> {

    private final MutableLiveData<Integer> partQuantityLiveData;

    public QuantityViewModel(Application application) {
        super(application);
        partQuantityLiveData = new MutableLiveData<>(QuantityDialog.PICKER_MIN_VALUE);
    }

    public void onOkClicked() {
        Integer value = partQuantityLiveData.getValue();
        if (value != null) {
            getNavigator().save(value);
        }
    }

    public MutableLiveData<Integer> getPartQuantityLiveData() {
        return partQuantityLiveData;
    }

}
