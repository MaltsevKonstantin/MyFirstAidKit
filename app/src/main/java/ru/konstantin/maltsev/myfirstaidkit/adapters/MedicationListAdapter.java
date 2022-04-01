package ru.konstantin.maltsev.myfirstaidkit.adapters;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.konstantin.maltsev.myfirstaidkit.R;
import ru.konstantin.maltsev.myfirstaidkit.objects.Medicine;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.Holder> {
    List<Medicine> medicationList;
    List<Medicine> oldList;
    OnMedicationListAdapterListener listener;

    public interface OnMedicationListAdapterListener {
        void onClickMedicine(Medicine medicine);
    }

    public MedicationListAdapter() {
        this.medicationList = new ArrayList<>();
        this.oldList = new ArrayList<>();
    }

    public void setOnMedicationListAdapterListener(OnMedicationListAdapterListener listener) {
        this.listener = listener;
    }

    public void update(List<Medicine> medicationList) {
        this.medicationList = medicationList;

        DiffUtilCallback callback = new DiffUtilCallback(oldList, medicationList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        result.dispatchUpdatesTo(this);

        oldList.clear();
        oldList.addAll(medicationList);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Medicine medicine = medicationList.get(position);
        medicine.updateCalendars();
        holder.name.setText(medicine.getName());
        String manufactureDate = "Дата изг. " + medicine.getManufactureDate().substring(3);
        String expirationDate = "Дата оконч. " + medicine.getExpirationDate().substring(3);
        holder.manufactureDate.setText(manufactureDate);
        holder.expirationDate.setText(expirationDate);


        String remainingShelfLife;
        boolean isExpired = false;
        if (medicine.isExpirationDateHasExpired()) {
            remainingShelfLife = holder.name.getContext().getResources().getString(R.string.expired);
            holder.progressBar.setVisibility(View.GONE);
            isExpired = true;
        } else {
            int deferenceDay = medicine.getDeferenceDay();
            holder.progressBar.setMax(deferenceDay);
            holder.progressBar.setProgress(deferenceDay - medicine.getRemainingDay());
            holder.progressBar.setVisibility(View.VISIBLE);
            remainingShelfLife = medicine.getStringRemainingShelfLife();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (medicine.isDanger()) {
                    holder.progressBar.setProgressTintList(ColorStateList.valueOf(holder.progressBar.getContext().getColor(R.color.danger)));
                } else if (medicine.isWarning()) {
                    holder.progressBar.setProgressTintList(ColorStateList.valueOf(holder.progressBar.getContext().getColor(R.color.warning)));
                } else {
                    holder.progressBar.setProgressTintList(ColorStateList.valueOf(holder.progressBar.getContext().getColor(R.color.good)));
                }
            }
        }
        holder.remainingShelfLife.setText(remainingShelfLife);
        if (isExpired) {
            holder.remainingShelfLife.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, 0, 0);
            holder.remainingShelfLife.setCompoundDrawablePadding(4);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            holder.remainingShelfLife.setCompoundDrawableTintList(
                    ColorStateList.valueOf(holder.remainingShelfLife.getContext().getColor(R.color.danger)));
        } else {
            holder.remainingShelfLife.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView manufactureDate;
        TextView expirationDate;
        ProgressBar progressBar;
        TextView remainingShelfLife;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            manufactureDate = itemView.findViewById(R.id.manufactureDate);
            expirationDate = itemView.findViewById(R.id.expirationDate);
            progressBar = itemView.findViewById(R.id.progressBar);
            remainingShelfLife = itemView.findViewById(R.id.remainingShelfLife);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickMedicine(medicationList.get(getAdapterPosition()));
                }
            });
        }
    }

    static class DiffUtilCallback extends DiffUtil.Callback {

        List<Medicine> oldList;
        List<Medicine> newList;

        public DiffUtilCallback(List<Medicine> oldList, List<Medicine> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Medicine oldItem = oldList.get(oldItemPosition);
            Medicine newItem = newList.get(newItemPosition);
            return oldItem.getExpirationDate().equals(newItem.getExpirationDate()) && oldItem.getManufactureDate().equals(newItem.getManufactureDate())
                    && oldItem.getManufactureName().equals(newItem.getManufactureName()) && oldItem.getName().equals(newItem.getName())
                    && oldItem.getQuantity() == newItem.getQuantity();
        }
    }
}
