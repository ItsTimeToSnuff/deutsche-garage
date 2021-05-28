package ua.com.d_garage.deutschegarage.data.service.barcode;

import android.app.Application;
import android.util.Log;
import android.util.Size;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.lifecycle.LifecycleOwner;
import com.google.mlkit.common.MlKitException;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.data.service.camera.BaseCameraService;
import ua.com.d_garage.deutschegarage.data.service.camera.ImageProcessor;
import ua.com.d_garage.deutschegarage.data.service.executor.ShutdownExecutor;

import java.util.concurrent.Executors;

public class BarcodeCameraService extends BaseCameraService<Long> {

    private static final String TAG = BarcodeCameraService.class.getSimpleName();

    private ImageProcessor<Long> imageProcessor;
    private final BarcodeSizePair sizePair;
    private final Size targetResolution;

    public BarcodeCameraService(Application application, LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider, BarcodeSizePair sizePair) {
        super(application, lifecycleOwner, surfaceProvider);
        this.sizePair = sizePair;
        targetResolution = new Size(sizePair.getPreviewSize().getHeight(), sizePair.getPreviewSize().getWidth());
    }

    @Override
    protected UseCase[] getUseCasesToBind() {
        UseCase[] useCases = new UseCase[2];
        useCases[0] = initPreviewUseCase();
        useCases[1] = initAnalysisUseCase();
        return useCases;
    }

    @Override
    public void stop() {
        super.stop();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    private Preview initPreviewUseCase() {
        Preview.Builder builder = new Preview.Builder();
        builder.setTargetResolution(targetResolution);
        Preview preview = builder.build();
        preview.setSurfaceProvider(surfaceProviderWeakReference.get());
        return preview;
    }

    private ImageAnalysis initAnalysisUseCase() {
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
        ShutdownExecutor shutdownExecutor = new ShutdownExecutor(Executors.newCachedThreadPool());
        imageProcessor = new BarcodeImageProcessor(shutdownExecutor, sizePair);
        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetResolution(targetResolution);
        ImageAnalysis imageAnalysis = builder.build();
        imageAnalysis.setAnalyzer(
                shutdownExecutor,
                imageProxy -> {
                    try {
                        imageProcessor.process(imageProxy, observableResult);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                    }
                });
        return imageAnalysis;
    }

}
