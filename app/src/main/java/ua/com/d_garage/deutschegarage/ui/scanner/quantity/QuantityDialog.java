package ua.com.d_garage.deutschegarage.ui.scanner.quantity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.DialogPartQuantityBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseDialog;
import ua.com.d_garage.deutschegarage.ui.scanner.ScannerActivity;

public class QuantityDialog extends BaseDialog<DialogPartQuantityBinding, QuantityViewModel, ScannerActivity> implements QuantityNavigator {

    public static final int PICKER_MIN_VALUE = 1;
    public static final int PICKER_MAX_VALUE = 100;

    private static final String TAG = QuantityDialog.class.getSimpleName();

    public static QuantityDialog newInstance() {
        QuantityDialog dialog = new QuantityDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
        setCancelable(false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.quantityPicker.setMinValue(PICKER_MIN_VALUE);
        binding.quantityPicker.setMaxValue(PICKER_MAX_VALUE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_part_quantity;
    }

    @Override
    public void save(int partQuantity) {
        baseActivity.saveNoteItem(partQuantity);
        dismissDialog(TAG);
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        baseActivity.onDismiss(dialog);
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

}
