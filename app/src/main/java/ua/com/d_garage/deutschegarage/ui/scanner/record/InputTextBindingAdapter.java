package ua.com.d_garage.deutschegarage.ui.scanner.record;

import androidx.databinding.BindingAdapter;
import com.google.android.material.textfield.TextInputLayout;

public final class InputTextBindingAdapter {

    private InputTextBindingAdapter() {
        throw new IllegalStateException();
    }

    @BindingAdapter({"errorText"})
    public static void setErrorText(TextInputLayout view, String message) {
        view.setError(message);
    }
}
