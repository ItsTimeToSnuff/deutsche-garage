package ua.com.d_garage.deutschegarage.data.service.barcode;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;
import androidx.camera.core.ImageProxy;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.data.service.camera.BaseImageProcessor;
import ua.com.d_garage.deutschegarage.data.service.executor.ShutdownExecutor;
import ua.com.d_garage.deutschegarage.utils.BitmapUtils;
import org.jsoup.internal.StringUtil;

import java.util.List;

public class BarcodeImageProcessor extends BaseImageProcessor<List<Barcode>, Long> {

    private static final String TAG = BarcodeImageProcessor.class.getSimpleName();
    private final BarcodeScanner barcodeScanner;
    private final BarcodeSizePair sizePair;

    public BarcodeImageProcessor(ShutdownExecutor executor, BarcodeSizePair sizePair) {
        super(executor);
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_CODABAR,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_ITF,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        this.sizePair = sizePair;
    }

    @Override
    protected Task<List<Barcode>> detectInImage(ImageProxy imageProxy) {
        Size preview = sizePair.getPreviewSize();
        Size analyze = sizePair.getAnalyzeSize();
        Bitmap tmpBitmap = BitmapUtils.yuv420ToBitmap(imageProxy, sizePair.getPreviewSize());
        if (tmpBitmap == null) {
            tmpBitmap = Bitmap.createBitmap(preview.getWidth(), preview.getHeight(), Bitmap.Config.RGB_565);
        }
        Bitmap bitmap = Bitmap.createBitmap(
                tmpBitmap,
                tmpBitmap.getWidth() / 2 - analyze.getWidth() / 2,
                tmpBitmap.getHeight() / 2 - analyze.getHeight()/ 2,
                analyze.getWidth(),
                analyze.getHeight()
        );
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        return barcodeScanner.process(inputImage);
    }

    @Override
    protected void onSuccess(List<Barcode> results, MutableLiveData<Long> observableResult) {
        if (results.isEmpty() || isShutdown.get()) {
            return;
        }
        String barcode = results.get(results.size() - 1).getRawValue();
        if (!StringUtil.isNumeric(barcode)) {
            return;
        }
        observableResult.postValue(Long.valueOf(barcode));
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }

    @Override
    public void stop() {
        super.stop();
        barcodeScanner.close();
    }

}
