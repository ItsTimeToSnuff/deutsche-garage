package ua.com.d_garage.deutschegarage.ui.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OverlayView extends View {

    private final Lock lock;
    private final List<OverlayGraphic> graphics;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        lock = new ReentrantLock();
        graphics = new ArrayList<>();
    }

    public abstract static class OverlayGraphic {

        protected final OverlayView overlayView;

        protected OverlayGraphic(OverlayView overlayView) {
            this.overlayView = overlayView;
        }

        public abstract void draw(Canvas canvas);

    }

    public void addGraphic(OverlayGraphic overlayGraphic) {
        lock.lock();
        try {
            graphics.add(overlayGraphic);
        } finally {
            lock.unlock();
        }
        postInvalidate();
    }

    public void removeGraphic(OverlayGraphic overlayGraphic) {
        lock.lock();
        try {
            graphics.remove(overlayGraphic);
        } finally {
            lock.unlock();
        }
        postInvalidate();
    }

    public void clear() {
        lock.lock();
        try {
            graphics.clear();
        } finally {
            lock.unlock();
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lock.lock();
        try {
            for (OverlayGraphic graphic : graphics) {
                graphic.draw(canvas);
            }
        } finally {
            lock.unlock();
        }
    }

}
