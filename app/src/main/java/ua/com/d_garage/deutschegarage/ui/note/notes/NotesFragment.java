package ua.com.d_garage.deutschegarage.ui.note.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.FragmentNotesBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseFragment;
import ua.com.d_garage.deutschegarage.ui.note.NoteActivity;
import ua.com.d_garage.deutschegarage.ui.note.note.NoteFragment;

public class NotesFragment extends BaseFragment<FragmentNotesBinding, NotesViewModel, NoteActivity> implements NotesNavigator {

    public static final String TAG = NotesFragment.class.getSimpleName();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_notes;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setNavigator(this);
        if (baseActivity.getSupportActionBar() != null) {
            baseActivity.getSupportActionBar().setTitle(R.string.notes_fragment_title);
        }
        NotesPagingAdapter notesPagingAdapter = new NotesPagingAdapter(viewModel);
        binding.notesContainer.setAdapter(notesPagingAdapter);
        viewModel.getPagingDataLiveData().observe(getViewLifecycleOwner(), notePagingData -> notesPagingAdapter.submitData(getLifecycle(), notePagingData));
    }

    @Override
    public void openDescription(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(NoteFragment.NOTE_ID, id);
        baseActivity.openFragment(NoteFragment.class, bundle, NoteFragment.TAG);
    }

}
