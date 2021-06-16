package ua.com.d_garage.deutschegarage.ui.view.barcode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Size;
import androidx.core.content.ContextCompat;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.ui.view.camera.ScanningAnimator;
import ua.com.d_garage.deutschegarage.ui.view.camera.OverlayView;

public class BarcodeScanningGraphic extends BaseBarcodeOverlayGraphic{

    private final ScanningAnimator animator;
    private final Paint scanningPaint;
    private final float alpha;
    private final float sizeOffset;

    public BarcodeScanningGraphic(OverlayView overlayView, Size scanBoxSize, ScanningAnimator animator) {
        super(overlayView, scanBoxSize);
        this.animator = animator;
        scanningPaint = new Paint();
        scanningPaint.setColor(ContextCompat.getColor(overlayView.getContext(), R.color.barcode_scanning));
        scanningPaint.setStyle(Paint.Style.STROKE);
        alpha = scanningPaint.getAlpha();
        sizeOffset = overlayView.getResources().getDimension(R.dimen.barcode_scanning_size_offset);
    }

    @Override
    public void draw(Canvas canvas) {
        scanningPaint.setAlpha((int) (alpha * animator.getAlpha()));
        scanningPaint.setStrokeWidth(scanBoxStrokeWidth * animator.getStrokeScale());
        float offset = sizeOffset * animator.getScale();
        RectF scanRect = new RectF(
                scanBoxRect.left - offset,
                scanBoxRect.top - offset,
                scanBoxRect.right + offset,
                scanBoxRect.bottom + offset
        );
        canvas.drawRoundRect(scanRect, scanBoxCornerRadius, scanBoxCornerRadius, scanningPaint);
    }

}
