package ua.com.d_garage.deutschegarage.data.service.barcode;

import android.app.Application;
import android.util.Log;
import androidx.camera.core.AspectRatio;
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
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

    private final BarcodeSizePair sizePair;
    private final int aspectRatio;

    private ImageProcessor<Long> imageProcessor;

    public BarcodeCameraService(Application application, LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider, BarcodeSizePair sizePair) {
        super(application, lifecycleOwner, surfaceProvider);
        this.sizePair = sizePair;
        aspectRatio = getAspectRatio(sizePair.getPreviewSize().getWidth(), sizePair.getPreviewSize().getHeight());
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
        builder.setTargetAspectRatio(aspectRatio);
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
        builder.setTargetAspectRatio(aspectRatio);
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

    private int getAspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

}
