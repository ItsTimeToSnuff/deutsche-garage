package ua.com.d_garage.deutschegarage.ui.part.barcode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraControl;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.view.PreviewView;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.databinding.FragmentBarcodeBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseFragment;
import ua.com.d_garage.deutschegarage.ui.customview.barcode.BarcodeScanBoxGraphic;
import ua.com.d_garage.deutschegarage.ui.customview.barcode.BarcodeScanningGraphic;
import ua.com.d_garage.deutschegarage.ui.customview.camera.OverlayView;
import ua.com.d_garage.deutschegarage.ui.customview.camera.ScanningAnimator;
import ua.com.d_garage.deutschegarage.ui.part.PartActivity;
import ua.com.d_garage.deutschegarage.ui.part.description.DescriptionFragment;

import java.util.concurrent.TimeUnit;

public class BarcodeFragment extends BaseFragment<FragmentBarcodeBinding, BarcodeViewModel, PartActivity> implements BarcodeNavigator {

    public static final String TAG = BarcodeFragment.class.getSimpleName();

    private FragmentBarcodeBinding fragmentBarcodeBinding;
    private PreviewView previewView;
    private OverlayView overlayView;
    private ScanningAnimator scanningAnimator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentBarcodeBinding = getViewDataBinding();
        previewView = fragmentBarcodeBinding.previewView;
        overlayView = fragmentBarcodeBinding.scannerOverlay;
        scanningAnimator = new ScanningAnimator(overlayView);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.stopScanner();
        scanningAnimator.stop();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_barcode;
    }

    @Override
    public void closeScanner() {
        getBaseActivity().finish();
    }

    @Override
    public void openDescription(long barcode) {
        if (getBaseActivity().isNetworkConnected()) {
            viewModel.stopScanner();
            Bundle bundle = new Bundle();
            bundle.putLong(DescriptionFragment.PART_NUMBER_KEY, barcode);
            getBaseActivity().openFragment(DescriptionFragment.class, bundle, DescriptionFragment.TAG);
        } else {
            Toast.makeText(getContext(), R.string.enable_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        previewView.post(() -> {
            Size previewSize = new Size(previewView.getWidth(), previewView.getHeight());
            BarcodeSizePair barcodeSizePair = new BarcodeSizePair(previewSize);
            initAnalyze(barcodeSizePair);
            initOverlay(barcodeSizePair);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAnalyze(BarcodeSizePair sizePair) {
        viewModel.initOrResumeScanner(getViewLifecycleOwner(), previewView.getSurfaceProvider(), sizePair);
        viewModel.getCamera().observe(getViewLifecycleOwner(), camera -> {
            scanningAnimator.start();
            viewModel.getBarcodeResult().observe(getViewLifecycleOwner(), this::openDescription);
            viewModel.getIsFlashOn().observe(
                    getViewLifecycleOwner(),
                    isEnable -> fragmentBarcodeBinding.barcodeActionBar.btnFlash.setSelected(isEnable)
            );
            previewView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP: {
                        MeteringPointFactory factory = previewView.getMeteringPointFactory();
                        MeteringPoint point = factory.createPoint(event.getX(), event.getY());
                        FocusMeteringAction action = new FocusMeteringAction.Builder(point)
                                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                .build();
                        CameraControl cameraControl = camera.getCameraControl();
                        cameraControl.startFocusAndMetering(action);
                        return true;
                    }
                }
                return false;
            });
        });
    }

    private void initOverlay(BarcodeSizePair sizePair) {
        overlayView.clear();
        overlayView.addGraphic(new BarcodeScanBoxGraphic(overlayView, sizePair.getAnalyzeSize()));
        scanningAnimator.start();
        overlayView.addGraphic(new BarcodeScanningGraphic(overlayView, sizePair.getAnalyzeSize(), scanningAnimator));
    }

}
