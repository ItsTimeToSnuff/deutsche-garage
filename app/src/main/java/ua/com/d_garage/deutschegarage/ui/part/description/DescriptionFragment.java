package ua.com.d_garage.deutschegarage.ui.part.description;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.FragmentDescriptionBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseFragment;
import ua.com.d_garage.deutschegarage.ui.part.PartActivity;
import org.jetbrains.annotations.NotNull;

public class DescriptionFragment extends BaseFragment<FragmentDescriptionBinding, DescriptionViewModel, PartActivity> implements DescriptionNavigator {

    public static final String TAG = DescriptionFragment.class.getSimpleName();
    public static final String PART_NUMBER_KEY = "pn_key";

    private FragmentDescriptionBinding fragmentBinding;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_description;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentBinding = getViewDataBinding();
        Bundle args = getArguments();
        if (args != null) {
            viewModel.getDescriptionFields(args.getLong(PART_NUMBER_KEY)).observe(getViewLifecycleOwner(), fields -> {
                if (fields == null) {
                    Toast.makeText(getContext(), getString(R.string.internal_server_error), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Internal server error or bad connection!");
                    return;
                }
                if (fields.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.part_not_found), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Required part not found");
                    return;
                }
                fragmentBinding.partDescriptionContainer.setAdapter(new DescriptionAdapter(fields));
                Log.d(TAG, "Required part found: " + fields);
            });
        } else {
            Log.e(TAG, "onViewCreated: bundle arguments must not be empty!");
        }
    }

    @Override
    public void navigateBackward() {
        getBaseActivity().onFragmentDetached(TAG);
        getBaseActivity().getSupportFragmentManager().popBackStack();
    }

}
