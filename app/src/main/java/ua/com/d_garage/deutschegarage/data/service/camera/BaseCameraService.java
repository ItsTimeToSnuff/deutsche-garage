package ua.com.d_garage.deutschegarage.data.service.camera;

import android.app.Application;
import android.util.Log;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.common.util.concurrent.ListenableFuture;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

public abstract class BaseCameraService<RES> implements CameraService<RES> {

    private static final String TAG = BaseCameraService.class.getSimpleName();

    protected MutableLiveData<RES> observableResult;
    protected WeakReference<Preview.SurfaceProvider> surfaceProviderWeakReference;

    private final MutableLiveData<Camera> camera;

    private ProcessCameraProvider processCameraProvider;
    private CameraSelector cameraSelector;
    private WeakReference<LifecycleOwner> lifecycleOwnerWeakReference;

    public BaseCameraService(Application application, LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider) {
        camera = new MutableLiveData<>();
        observableResult = new MutableLiveData<>();
        setCameraLance(CameraSelector.LENS_FACING_BACK);
        lifecycleOwnerWeakReference = new WeakReference<>(lifecycleOwner);
        surfaceProviderWeakReference = new WeakReference<>(surfaceProvider);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(application);
        cameraProviderFuture.addListener(
                () -> {
                    try {
                        processCameraProvider = cameraProviderFuture.get();
                        rebindUseCases(lifecycleOwnerWeakReference.get(), surfaceProviderWeakReference.get());
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Unhandled exception", e);
                    }
                },
                ContextCompat.getMainExecutor(application));
    }

    public LiveData<Camera> getCamera() {
        return camera;
    }

    @Override
    public void enableFlash(boolean isEnable) {
        Camera cam = camera.getValue();
        if (cam != null) {
            cam.getCameraControl().enableTorch(isEnable);
        }
    }

    @Override
    public LiveData<RES> getObservableResult() {
        return observableResult;
    }

    @Override
    public void rebindUseCases(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider) {
        lifecycleOwnerWeakReference = new WeakReference<>(lifecycleOwner);
        surfaceProviderWeakReference = new WeakReference<>(surfaceProvider);
        observableResult = new MutableLiveData<>();
        rebindUseCases();
    }

    @Override
    public void setCameraLance(int lanceFacing) {
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lanceFacing).build();
        rebindUseCases();
    }

    @Override
    public void stop() {

    }

    protected abstract UseCase[] getUseCasesToBind();

    private void rebindUseCases() {
        if (processCameraProvider == null) {
            return;
        }
        processCameraProvider.unbindAll();
        Camera cam = processCameraProvider.bindToLifecycle(lifecycleOwnerWeakReference.get(), cameraSelector, getUseCasesToBind());
        camera.postValue(cam);
    }

}
