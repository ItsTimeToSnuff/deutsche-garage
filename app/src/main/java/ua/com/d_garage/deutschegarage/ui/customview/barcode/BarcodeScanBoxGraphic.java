package ua.com.d_garage.deutschegarage.ui.customview.barcode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Size;
import androidx.core.content.ContextCompat;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.ui.customview.camera.OverlayView;

public class BarcodeScanBoxGraphic extends BaseBarcodeOverlayGraphic{

    private final Paint backgroundPaint;
    private final Paint scanBoxPaint;

    public BarcodeScanBoxGraphic(OverlayView overlayView, Size scanBoxSize) {
        super(overlayView, scanBoxSize);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(overlayView.getContext(), R.color.barcode_overlay_background));
        scanBoxPaint = new Paint();
        scanBoxPaint.setColor(ContextCompat.getColor(overlayView.getContext(), R.color.barcode_scan_box_stroke));
        scanBoxPaint.setStyle(Paint.Style.STROKE);
        scanBoxPaint.setStrokeWidth(overlayView.getContext().getResources().getDimension(R.dimen.barcode_scan_box_stroke_width));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        canvas.drawRoundRect(scanBoxRect, scanBoxCornerRadius, scanBoxCornerRadius, clearPaint);
        canvas.drawRoundRect(scanBoxRect, scanBoxCornerRadius, scanBoxCornerRadius, scanBoxPaint);
        canvas.restore();
    }

}
