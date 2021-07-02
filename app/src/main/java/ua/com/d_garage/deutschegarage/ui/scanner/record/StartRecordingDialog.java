package ua.com.d_garage.deutschegarage.ui.scanner.record;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.DialogNoteStartBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseDialog;
import ua.com.d_garage.deutschegarage.ui.scanner.ScannerActivity;

public class StartRecordingDialog extends BaseDialog<DialogNoteStartBinding, StartRecordingViewModel, ScannerActivity> implements StartRecordingNavigator {

    private static final String TAG = StartRecordingDialog.class.getSimpleName();

    public static StartRecordingDialog newInstance() {
        StartRecordingDialog dialog = new StartRecordingDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_note_start;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
    }

    @Override
    public void start(String noteTitle) {
        baseActivity.startRecording(noteTitle);
        dismissDialog(TAG);
    }

    @Override
    public void cancel() {
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
