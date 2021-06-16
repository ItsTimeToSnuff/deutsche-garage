package ua.com.d_garage.deutschegarage.ui.note.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.databinding.ItemPartViewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.ViewHolder> {

    private final List<Part> parts;
    private final Map<Part, Long> counts;

    public PartsAdapter(List<Part> parts) {
        counts = parts
                .stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        this.parts = new ArrayList<>(counts.keySet());
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
        Part part = parts.get(position);
        holder.binding.setPartNumber(String.valueOf(position));
        holder.binding.setPart(part);
        holder.binding.setPartCount(String.valueOf(counts.get(part)));
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public Map<Part, Long> getCounts() {
        return counts;
    }

}
