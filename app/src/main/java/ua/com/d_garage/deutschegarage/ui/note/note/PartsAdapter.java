package ua.com.d_garage.deutschegarage.ui.note.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.model.note.NoteItemWithPart;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.databinding.ItemPartViewBinding;

import java.util.List;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.ViewHolder> {

    private final List<NoteItemWithPart> noteItemWithParts;

    public PartsAdapter(List<NoteItemWithPart> noteItemWithParts) {
        this.noteItemWithParts = noteItemWithParts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPartViewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_part_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteItemWithPart noteItemWithPart = noteItemWithParts.get(position);
        Part part = noteItemWithPart.getPart();
        if (holder.binding != null) {
            holder.binding.setPartNumberValue(position);
            holder.binding.setPart(part);
            holder.binding.setPartCountValue(noteItemWithPart.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return noteItemWithParts.size();
    }

}
