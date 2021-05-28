package ua.com.d_garage.deutschegarage.data.service.camera;

import androidx.camera.core.Camera;
import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

public interface CameraService<RES> {

    LiveData<RES> getObservableResult();

    void rebindUseCases(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider);

    void setCameraLance(int lanceFacing);

    LiveData<Camera> getCamera();

    void enableFlash(boolean isEnable);

    void stop();

}
