package ua.com.d_garage.deutschegarage.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.ActivityMainBinding;
import ua.com.d_garage.deutschegarage.ui.scanner.ScannerActivity;
import ua.com.d_garage.deutschegarage.ui.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements MainNavigator {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean isDoubleClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void openBarcodeActivity() {
        startActivity(new Intent(this, ScannerActivity.class));
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isDoubleClick) {
            MainActivity.super.onBackPressed();
        } else {
            isDoubleClick = true;
            Toast.makeText(MainActivity.this, getString(R.string.back_pressed_text), Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isDoubleClick = false;
                }
            }, 1000);
        }
    }
}