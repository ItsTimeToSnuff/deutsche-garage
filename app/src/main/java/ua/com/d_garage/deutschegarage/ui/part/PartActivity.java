package ua.com.d_garage.deutschegarage.ui.part;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.ActivityPartBinding;
import ua.com.d_garage.deutschegarage.ui.part.barcode.BarcodeFragment;
import ua.com.d_garage.deutschegarage.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PartActivity extends BaseActivity<ActivityPartBinding, PartViewModel> implements PartNavigator {

    private static final String TAG = PartActivity.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;

    private ActivityPartBinding activityPartBinding;

    @Override
    public int getLayoutId() {
        return R.layout.activity_part;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
        activityPartBinding = getViewDataBinding();
        if (allPermissionsGranted()){
            openFragment(BarcodeFragment.class, null, BarcodeFragment.TAG);
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (allPermissionsGranted()) {
            Log.d(TAG, "Permission granted!");
            getSupportFragmentManager().beginTransaction().replace(activityPartBinding.activityBarcodeContainer.getId(), new BarcodeFragment()).commit();
        } else {
            Log.d(TAG, "User denied permissions!");
            Toast.makeText(this, getString(R.string.permissions_denied_message), Toast.LENGTH_LONG).show();
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFragmentDetached(String tag) {
        super.onFragmentDetached(tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .remove(fragment)
                    .commit();
        }
    }

    public void openFragment(Class<? extends Fragment> fragment, Bundle bundle, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(tag)
                .replace(activityPartBinding.activityBarcodeContainer.getId(), fragment, bundle, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
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
