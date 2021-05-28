package ua.com.d_garage.deutschegarage.data.model.barcode;

import android.util.Size;
import org.jetbrains.annotations.NotNull;

public class BarcodeSizePair {

    private static final float WIDTH_SCALE = 0.75F;
    private static final float HEIGHT_SCALE = 0.15F;

    private final Size previewSize;
    private final Size analyzeSize;

    public BarcodeSizePair(Size previewSize) {
        this.previewSize = previewSize;
        this.analyzeSize = new Size((int) (previewSize.getWidth() * WIDTH_SCALE), (int) (previewSize.getHeight() * HEIGHT_SCALE));
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public Size getAnalyzeSize() {
        return analyzeSize;
    }

    @NotNull
    @Override
    public String toString() {
        return "BarcodeSizePair{" +
                "previewSize=" + previewSize +
                ", analyzeSize=" + analyzeSize +
                '}';
    }
}
