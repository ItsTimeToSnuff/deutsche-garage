package ua.com.d_garage.deutschegarage.ui.base;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ua.com.d_garage.deutschegarage.utils.NetworkUtils;

import java.lang.reflect.ParameterizedType;

public abstract class BaseActivity<DB extends ViewDataBinding, VM extends ViewModel>
        extends AppCompatActivity implements BaseFragment.Callback {

    protected DB binding;
    protected VM viewModel;

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        assert parameterizedType != null;
        Class<VM> viewModelClass = (Class<VM>)parameterizedType.getActualTypeArguments()[1];
        viewModel = new ViewModelProvider(this).get(viewModelClass);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setVariable(BR.viewModel, viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(this);
    }
}
