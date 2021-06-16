package ua.com.d_garage.deutschegarage.ui.view.camera;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

public class ScanningAnimator implements Animator.AnimatorListener {

    private static final int FADE_IN_DURATION = 330;
    private static final int FADE_OUT_DURATION = 500;
    private static final int FADE_OUT_DELAY = 667;
    private static final int SCALE_DURATION = 833;
    private static final int SCALE_DELAY = 333;
    private static final int STROKE_SCALE_DURATION = 833;
    private static final int STROKE_SCALE_DELAY = 333;
    private static final int RESTART_DELAY_DURATION = 1333;
    private static final int RESTART_DELAY_DELAY = 1167;

    private final OverlayView overlayView;
    private final AnimatorSet animatorSet;
    private final ValueAnimator fadeInAnimator;
    private final ValueAnimator fadeOutAnimator;
    private final ValueAnimator scaleAnimator;
    private final ValueAnimator strokeScaleAnimator;
    private final ValueAnimator restartDelayAnimator;

    private float alpha;
    private float scale;
    private float strokeScale;

    public ScanningAnimator(OverlayView overlayView) {
        this.overlayView = overlayView;
        animatorSet = new AnimatorSet();
        fadeInAnimator = ValueAnimator.ofFloat(0f, 1f);
        fadeOutAnimator = ValueAnimator.ofFloat(1f, 0f);
        scaleAnimator = ValueAnimator.ofFloat(0f, 1f);
        strokeScaleAnimator = ValueAnimator.ofFloat(1f, .5f);
        restartDelayAnimator = ValueAnimator.ofFloat(0f, 0f);
        setUp();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        resetValues();
        animation.start();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void start() {
        if (!animatorSet.isRunning()) {
            animatorSet.addListener(this);
            animatorSet.start();
        }
    }

    public void stop() {
        animatorSet.removeAllListeners();
        animatorSet.cancel();
        resetValues();
    }

    public float getAlpha() {
        return alpha;
    }

    public float getScale() {
        return scale;
    }

    public float getStrokeScale() {
        return strokeScale;
    }

    private void setUp() {
        fadeInAnimator.setDuration(FADE_IN_DURATION);
        fadeInAnimator.addUpdateListener(animation -> {
            alpha = (float) fadeInAnimator.getAnimatedValue();
            overlayView.postInvalidate();
        });

        fadeOutAnimator.setDuration(FADE_OUT_DURATION);
        fadeOutAnimator.setStartDelay(FADE_OUT_DELAY);
        fadeOutAnimator.addUpdateListener(animation -> {
            alpha = (float) fadeOutAnimator.getAnimatedValue();
            overlayView.postInvalidate();
        });

        scaleAnimator.setDuration(SCALE_DURATION);
        scaleAnimator.setStartDelay(SCALE_DELAY);
        scaleAnimator.addUpdateListener(animation -> {
            scale = (float) scaleAnimator.getAnimatedValue();
            overlayView.postInvalidate();
        });

        strokeScaleAnimator.setDuration(STROKE_SCALE_DURATION);
        strokeScaleAnimator.setStartDelay(STROKE_SCALE_DELAY);
        strokeScaleAnimator.addUpdateListener(animation -> {
            strokeScale = (float) strokeScaleAnimator.getAnimatedValue();
            overlayView.postInvalidate();
        });

        restartDelayAnimator.setDuration(RESTART_DELAY_DURATION);
        restartDelayAnimator.setStartDelay(RESTART_DELAY_DELAY);

        animatorSet.addListener(this);
        animatorSet.playTogether(
                fadeInAnimator,
                fadeOutAnimator,
                scaleAnimator,
                strokeScaleAnimator,
                restartDelayAnimator
        );
    }

    private void resetValues() {
        alpha = 0f;
        scale = 0f;
        strokeScale = 1f;
    }
}
