package ua.com.d_garage.deutschegarage.ui.note;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.databinding.ActivityNotesBinding;
import ua.com.d_garage.deutschegarage.ui.base.BaseActivity;
import ua.com.d_garage.deutschegarage.ui.note.notes.NotesFragment;

public class NoteActivity extends BaseActivity<ActivityNotesBinding, NoteActivityViewModel> implements NoteActivityNavigator {

    @Override
    public int getLayoutId() {
        return R.layout.activity_notes;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
        setSupportActionBar(binding.notesAppBar.notesToolbar);
        openFragment(NotesFragment.class, null, NotesFragment.TAG);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
                .addToBackStack(tag)
                .replace(binding.activityNotesContainer.getId(), fragment, bundle, tag)
                .commit();
    }

}
