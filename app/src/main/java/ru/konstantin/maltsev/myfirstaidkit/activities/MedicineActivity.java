package ru.konstantin.maltsev.myfirstaidkit.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.konstantin.maltsev.myfirstaidkit.databinding.ActivityMedicineBinding;
import ru.konstantin.maltsev.myfirstaidkit.objects.Medicine;
import ru.konstantin.maltsev.myfirstaidkit.objects.MedicineHolder;
import ru.konstantin.maltsev.myfirstaidkit.utils.InfoMessage;
import ru.konstantin.maltsev.myfirstaidkit.utils.Setting;
import ru.konstantin.maltsev.myfirstaidkit.utils.StartActivityWithCleanTask;
import ru.konstantin.maltsev.myfirstaidkit.web_services.WebService;

public class MedicineActivity extends AppCompatActivity {

    ActivityMedicineBinding binding;
    Medicine medicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle("Лекарство");

        medicine = (Medicine) getIntent().getSerializableExtra("medicine");
        if (medicine == null) medicine = new Medicine();

        medicine.updateCalendars();

        initViews();

        binding.btnDelete.setOnClickListener(v -> {
            medicine.setHide(true);
            updateMedicine();
        });

        binding.btnSave.setOnClickListener(v -> {
            medicine.setManufactureName(Objects.requireNonNull(binding.tiManufacture.getText()).toString());
            medicine.setName(Objects.requireNonNull(binding.tiName.getText()).toString());
            updateMedicine();
        });

        binding.manufactureDate.setOnClickListener(v -> new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            medicine.setManufactureCalendar(year, month, 1);
            updateDateViews();
        }, medicine.getManufactureCalendar().get(Calendar.YEAR), medicine.getManufactureCalendar().get(Calendar.MONTH), 1).show());

        binding.expirationDate.setOnClickListener(v -> new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            medicine.setExpirationCalendar(year, month, 1);
            updateDateViews();
        }, medicine.getExpirationCalendar().get(Calendar.YEAR), medicine.getExpirationCalendar().get(Calendar.MONTH), 1).show());
    }

    private void initViews() {
        binding.tiManufacture.setText(medicine.getManufactureName());
        binding.tiName.setText(medicine.getName());
        updateDateViews();
    }

    private void updateDateViews() {
        binding.manufactureDate.setText(medicine.getManufactureDate().substring(3));
        binding.expirationDate.setText(medicine.getExpirationDate().substring(3));
    }

    private void updateMedicine() {
        setProcess(true);
        WebService.getInstance().updateMedicine(this, medicine, new Callback<MedicineHolder>() {
            @Override
            public void onResponse(@NonNull Call<MedicineHolder> call, @NonNull Response<MedicineHolder> response) {
                setProcess(false);
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isError()) {
                        finish();
                        return;
                    } else {
                        new Setting(MedicineActivity.this).clearToken();
                        StartActivityWithCleanTask.start(MedicineActivity.this, EntryActivity.class);
                    }
                } else if (response.code() == 404) {
                    new Setting(MedicineActivity.this).clearServiceUrl();
                    StartActivityWithCleanTask.start(MedicineActivity.this, GetApiUrlActivity.class);
                }
                InfoMessage.createBad(binding.getRoot(), "Произошла ошибка!");
            }

            @Override
            public void onFailure(@NonNull Call<MedicineHolder> call, @NonNull Throwable t) {
                setProcess(false);
                InfoMessage.createBad(binding.getRoot(), "Произошла ошибка!");
            }
        });
    }

    private void setProcess(boolean inProcessed) {
        binding.progressBar.setVisibility(inProcessed? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!inProcessed);
        binding.btnDelete.setEnabled(!inProcessed);
        binding.tiName.setEnabled(!inProcessed);
        binding.tiManufacture.setEnabled(!inProcessed);
        binding.expirationDate.setEnabled(!inProcessed);
        binding.manufactureDate.setEnabled(!inProcessed);
    }
}