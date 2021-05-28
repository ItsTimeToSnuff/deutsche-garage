package ua.com.d_garage.deutschegarage.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;

public abstract class BaseFragment<DB extends ViewDataBinding, VM extends ViewModel, A extends BaseActivity<? extends ViewDataBinding, ? extends ViewModel>> extends Fragment {

    protected VM viewModel;

    private A baseActivity;
    private DB viewDataBinding;

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        assert parameterizedType != null;
        Class<VM> viewModelClass = (Class<VM>)parameterizedType.getActualTypeArguments()[1];
        viewModel = new ViewModelProvider(this).get(viewModelClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        baseActivity = (A) context;
        baseActivity.onFragmentAttached();
    }

    @Override
    public void onDetach() {
        baseActivity = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return viewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewDataBinding.setVariable(BR.viewModel, viewModel);
        viewDataBinding.setLifecycleOwner(this);
        viewDataBinding.executePendingBindings();
    }

    public A getBaseActivity() {
        return baseActivity;
    }

    public DB getViewDataBinding() {
        return viewDataBinding;
    }
}
