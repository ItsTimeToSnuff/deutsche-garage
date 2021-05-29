package ua.com.d_garage.deutschegarage.ui.scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.camera.core.CameraControl;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.model.barcode.BarcodeSizePair;
import ua.com.d_garage.deutschegarage.databinding.ActivityScannerBinding;
import ua.com.d_garage.deutschegarage.ui.customview.barcode.BarcodeScanBoxGraphic;
import ua.com.d_garage.deutschegarage.ui.customview.barcode.BarcodeScanningGraphic;
import ua.com.d_garage.deutschegarage.ui.customview.camera.OverlayView;
import ua.com.d_garage.deutschegarage.ui.customview.camera.ScanningAnimator;
import ua.com.d_garage.deutschegarage.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.utils.CustomTabsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ua.com.d_garage.deutschegarage.data.remote.part.PartApiConstant.ENDPOINT_BMW_PART_SEARCH;
import static ua.com.d_garage.deutschegarage.data.remote.part.PartApiConstant.HOST;
import static ua.com.d_garage.deutschegarage.data.remote.part.PartApiConstant.QUERY_PARAMETER_NAME_BMW_PART;
import static ua.com.d_garage.deutschegarage.data.remote.part.PartApiConstant.SCHEME;

public class ScannerActivity extends BaseActivity<ActivityScannerBinding, ScannerViewModel> implements ScannerNavigator {

    private static final String TAG = ScannerActivity.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;

    private ActivityScannerBinding binding;
    private PreviewView previewView;
    private OverlayView overlayView;
    private ScanningAnimator scanningAnimator;
    private CustomTabsIntent customTabsIntent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
        binding = getViewDataBinding();
        previewView = binding.previewView;
        overlayView = binding.scannerOverlay;
        scanningAnimator = new ScanningAnimator(overlayView);
        String packageNameToBind = CustomTabsUtils.getPackageNameToUse(this);
        CustomTabColorSchemeParams.Builder builder = new CustomTabColorSchemeParams.Builder();
        builder.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setNavigationBarDividerColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        CustomTabColorSchemeParams params = builder.build();
        customTabsIntent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(params)
                .build();
        if (!TextUtils.isEmpty(packageNameToBind)) {
            customTabsIntent.intent.setPackage(packageNameToBind);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted()){
            initScanner();
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopScanner();
        scanningAnimator.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (allPermissionsGranted()) {
            Log.d(TAG, "Permission granted!");
            initScanner();
        } else {
            Log.d(TAG, "User denied permissions!");
            Toast.makeText(this, getString(R.string.permissions_denied_message), Toast.LENGTH_LONG).show();
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void closeScanner() {
        finish();
    }

    @Override
    public void openDescription(Long barcode) {
        if (isNetworkConnected()) {
            viewModel.stopScanner();
            customTabsIntent.launchUrl(
                    this,
                    Uri.parse(
                            SCHEME + "://" +
                                    HOST + "/" +
                                    ENDPOINT_BMW_PART_SEARCH + "?" +
                                    QUERY_PARAMETER_NAME_BMW_PART + "=" +
                                    barcode)
            );
        } else {
            Toast.makeText(this, R.string.enable_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void initScanner() {
        previewView.post(() -> {
            Size previewSize = new Size(previewView.getWidth(), previewView.getHeight());
            BarcodeSizePair barcodeSizePair = new BarcodeSizePair(previewSize);
            initAnalyze(barcodeSizePair);
            initOverlay(barcodeSizePair);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAnalyze(BarcodeSizePair sizePair) {
        viewModel.initOrResumeScanner(this, previewView.getSurfaceProvider(), sizePair);
        viewModel.getCamera().observe(this, camera -> {
            scanningAnimator.start();
            viewModel.getBarcodeResult().observe(this, this::openDescription);
            viewModel.getIsFlashOn().observe(
                    this,
                    isEnable -> binding.barcodeActionBar.btnFlash.setSelected(isEnable)
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

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.d(TAG, "Permission NOT granted: " + permission);
        return false;
    }

}
