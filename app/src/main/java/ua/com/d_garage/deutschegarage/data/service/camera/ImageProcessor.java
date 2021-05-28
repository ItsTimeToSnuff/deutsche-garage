package ua.com.d_garage.deutschegarage.data.service.camera;

import androidx.camera.core.ImageProxy;
import androidx.lifecycle.MutableLiveData;
import com.google.mlkit.common.MlKitException;

public interface ImageProcessor<RES> {

    void process(ImageProxy imageProxy, MutableLiveData<RES> observableResult) throws MlKitException;

    void stop();

}
