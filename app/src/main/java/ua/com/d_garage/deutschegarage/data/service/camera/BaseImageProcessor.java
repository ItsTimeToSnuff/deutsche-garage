package ua.com.d_garage.deutschegarage.data.service.camera;

import android.util.Log;
import androidx.camera.core.ImageProxy;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import ua.com.d_garage.deutschegarage.data.service.executor.ShutdownExecutor;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseImageProcessor<T, RES> implements ImageProcessor<RES> {

    public static final String TAG = BaseImageProcessor.class.getSimpleName();

    protected final ShutdownExecutor executor;
    protected final AtomicBoolean isShutdown;

    public BaseImageProcessor(ShutdownExecutor executor) {
        this.executor = executor;
        isShutdown = new AtomicBoolean();
    }

    @Override
    public void process(ImageProxy imageProxy, MutableLiveData<RES> observableResult) {
        if (isShutdown.get()) {
            imageProxy.close();
            return;
        }
        requestDetectInImage(imageProxy, observableResult).addOnCompleteListener(results -> imageProxy.close());
    }

    private Task<T> requestDetectInImage(ImageProxy imageProxy, MutableLiveData<RES> observableResult) {
        return detectInImage(imageProxy)
                .addOnSuccessListener(
                        executor,
                        results -> onSuccess(results, observableResult))
                .addOnFailureListener(
                        executor,
                        e -> {
                            String error = "Failed to process. Error: " + e.getLocalizedMessage();
                            Log.d(TAG, error);
                            e.printStackTrace();
                            onFailure(e);
                        });
    }

    @Override
    public void stop() {
        executor.shutdown();
        isShutdown.set(true);
    }

    protected abstract Task<T> detectInImage(ImageProxy imageProxy);

    protected abstract void onSuccess(T results, MutableLiveData<RES> observableResult);

    protected abstract void onFailure(Exception e);

}
