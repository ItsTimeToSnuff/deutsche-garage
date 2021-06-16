package ua.com.d_garage.deutschegarage.ui.note.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.FragmentNoteDescriptionBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseFragment;
import ua.com.d_garage.deutschegarage.ui.note.NoteActivity;

public class NoteFragment extends BaseFragment<FragmentNoteDescriptionBinding, NoteViewModel, NoteActivity> implements NoteNavigator {

    public static final String TAG = NoteFragment.class.getSimpleName();
    public static final String NOTE_ID = "NOTE_ID";

    private PartsAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_note_description;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getNoteWithPartsLiveData().observe(getViewLifecycleOwner(), noteWithParts -> {
            adapter = new PartsAdapter(noteWithParts.getParts());
            binding.partsContainer.setAdapter(adapter);
            if (baseActivity.getSupportActionBar() != null) {
                baseActivity.getSupportActionBar().setTitle(noteWithParts.getNote().getTitle());
            }
        });
        Bundle arguments = getArguments();
        if (arguments != null) {
            viewModel.loadDescription(arguments.getLong(NOTE_ID));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_delete_note: {
                viewModel.deleteNote();
                baseActivity.onBackPressed();
                break;
            }
            case R.id.btn_share_note: {
                String shareableNote = viewModel.getShareableNote(adapter.getCounts());
                Intent intent = new Intent();
                intent.setType("text/plain");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareableNote);
                startActivity(Intent.createChooser(intent, getString(R.string.btn_share_note)));
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
