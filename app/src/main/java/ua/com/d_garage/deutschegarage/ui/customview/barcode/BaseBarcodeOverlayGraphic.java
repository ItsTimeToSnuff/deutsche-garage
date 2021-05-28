package ua.com.d_garage.deutschegarage.ui.customview.barcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Size;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.ui.customview.camera.OverlayView;

public abstract class BaseBarcodeOverlayGraphic extends OverlayView.OverlayGraphic {

    protected final float scanBoxCornerRadius;
    protected final float scanBoxStrokeWidth;
    protected final Paint pathPaint;
    protected final Paint clearPaint;
    protected final RectF scanBoxRect;

    protected BaseBarcodeOverlayGraphic(OverlayView overlayView, Size scanBoxSize) {
        super(overlayView);
        scanBoxCornerRadius = overlayView.getResources().getDimension(R.dimen.barcode_scan_box_corner_radius);
        scanBoxStrokeWidth = overlayView.getResources().getDimension(R.dimen.barcode_scan_box_stroke_width);
        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(scanBoxStrokeWidth);
        pathPaint.setPathEffect(new CornerPathEffect(scanBoxCornerRadius));
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clearPaint.setStrokeWidth(overlayView.getContext().getResources().getDimension(R.dimen.barcode_scan_box_stroke_width));
        float cX = overlayView.getWidth() / 2f;
        float cY = overlayView.getHeight() / 2f;
        float halfWidth = scanBoxSize.getWidth() / 2f;
        float halfHeight = scanBoxSize.getHeight() / 2f;
        scanBoxRect = new RectF(cX - halfWidth, cY - halfHeight, cX + halfWidth, cY + halfHeight);
    }

    @Override
    public abstract void draw(Canvas canvas);
}
