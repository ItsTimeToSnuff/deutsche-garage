package ua.com.d_garage.deutschegarage.ui.part.description;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ua.com.d_garage.deutschegarage.R;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder> {

    private final List<PartDescriptionField> partDescriptionFields;

    public DescriptionAdapter(List<PartDescriptionField> partDescriptionFields) {
        this.partDescriptionFields = partDescriptionFields;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView valueView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.field_name);
            valueView = itemView.findViewById(R.id.field_value);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.description_field_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DescriptionAdapter.ViewHolder holder, int position) {
        PartDescriptionField field = partDescriptionFields.get(position);
        holder.nameView.setText(field.getName());
        holder.valueView.setText(field.getValue());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return partDescriptionFields.size();
    }

}
