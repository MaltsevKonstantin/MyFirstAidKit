package ru.konstantin.maltsev.myfirstaidkit.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.konstantin.maltsev.myfirstaidkit.adapters.MedicationListAdapter;
import ru.konstantin.maltsev.myfirstaidkit.databinding.ActivityMedicationListBinding;
import ru.konstantin.maltsev.myfirstaidkit.objects.Medicine;
import ru.konstantin.maltsev.myfirstaidkit.objects.MedicineHolder;
import ru.konstantin.maltsev.myfirstaidkit.utils.InfoMessage;
import ru.konstantin.maltsev.myfirstaidkit.utils.Setting;
import ru.konstantin.maltsev.myfirstaidkit.utils.StartActivityWithCleanTask;
import ru.konstantin.maltsev.myfirstaidkit.web_services.WebService;

public class MedicationListActivity extends AppCompatActivity {

    ActivityMedicationListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMedicationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle("Список лекарств");

        MedicationListAdapter adapter = new MedicationListAdapter();
        adapter.setOnMedicationListAdapterListener(medicine -> {
            Intent intent = new Intent(this, MedicineActivity.class);
            intent.putExtra("medicine", medicine);
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            binding.recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (scrollY > oldScrollY) {
                    assert layoutManager != null;
                    if (layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - 1)
                    binding.fabAddMedicine.hide();
                } else {
                    binding.fabAddMedicine.show();
                }

            });
        }

        binding.fabAddMedicine.setOnClickListener(v -> startActivity(new Intent(this, MedicineActivity.class)));

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            updateMedicationList();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMedicationList();
    }

    private void updateMedicationList() {
        binding.progressBar.setVisibility(View.VISIBLE);
        WebService.getInstance().getMedicationList(this, new Callback<MedicineHolder>() {
            @Override
            public void onResponse(@NonNull Call<MedicineHolder> call, @NonNull Response<MedicineHolder> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() == null || response.body().isError()) {
                        new Setting(MedicationListActivity.this).clearToken();
                        StartActivityWithCleanTask.start(MedicationListActivity.this, EntryActivity.class);
                    } else {
                        MedicationListAdapter adapter = (MedicationListAdapter) binding.recyclerView.getAdapter();
                        if (adapter != null) {
                            List<Medicine> medicationList = new ArrayList<>();
                            for (Medicine medicine : response.body().getMedicationList()) {
                                if (!medicine.isHide()) medicationList.add(medicine);
                            }
                            Collections.sort(medicationList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                            adapter.update(medicationList);
                            InfoMessage.createGood(binding.getRoot(), "Обновлено");
                        }
                    }
                } else if (response.code() == 404) {
                    new Setting(MedicationListActivity.this).clearServiceUrl();
                    StartActivityWithCleanTask.start(MedicationListActivity.this, GetApiUrlActivity.class);
                } else showError();
            }

            @Override
            public void onFailure(@NonNull Call<MedicineHolder> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showError();
            }
        });
    }

    private void showError() {
        InfoMessage.createBad(binding.getRoot(), "Ошибка!");
    }
}