package ua.com.d_garage.deutschegarage.ui.scanner;

import android.app.Application;
import androidx.camera.core.Camera;
import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.data.service.barcode.BarcodeCameraService;
import ua.com.d_garage.deutschegarage.data.service.camera.CameraService;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

public class ScannerViewModel extends BaseViewModel<ScannerNavigator> {

    private static final String TAG = ScannerViewModel.class.getSimpleName();

    private final MediatorLiveData<Camera> camera;
    private final MediatorLiveData<Boolean> isFlashOn;

    private CameraService<Long> cameraService;
    private MediatorLiveData<Long> result;

    public ScannerViewModel(Application application) {
        super(application);
        camera = new MediatorLiveData<>();
        isFlashOn = new MediatorLiveData<>();
        isFlashOn.postValue(false);
        isFlashOn.addSource(isFlashOn, isEnable-> cameraService.enableFlash(isEnable));
    }

    public void initOrResumeScanner(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider, BarcodeSizePair sizePair) {
        if (cameraService == null) {
            cameraService = new BarcodeCameraService(getApplication(), lifecycleOwner, surfaceProvider, sizePair);
            camera.addSource(cameraService.getCamera(), camera::postValue);
        } else {
            cameraService.rebindUseCases(lifecycleOwner, surfaceProvider);
            isFlashOn.postValue(isFlashOn.getValue());
        }
    }

    public LiveData<Long> getBarcodeResult() {
        result = new MediatorLiveData<>();
        result.addSource(cameraService.getObservableResult(), barcode -> {
            result.postValue(barcode);
            //cameraService.stop();
        });
        return result;
    }

    public LiveData<Camera> getCamera() {
        return camera;
    }

    public void stopScanner() {
        if (cameraService != null) {
            cameraService.stop();
        }
    }

    public void close() {
        getNavigator().closeScanner();
    }

    public void flashOnOff() {
        Boolean value = isFlashOn.getValue();
        if (value != null) {
            isFlashOn.postValue(!value);
        }
    }

    public LiveData<Boolean> getIsFlashOn() {
        return isFlashOn;
    }
}
