package ru.konstantin.maltsev.myfirstaidkit.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.konstantin.maltsev.myfirstaidkit.databinding.ActivityEntryBinding;
import ru.konstantin.maltsev.myfirstaidkit.utils.InfoMessage;
import ru.konstantin.maltsev.myfirstaidkit.utils.Setting;
import ru.konstantin.maltsev.myfirstaidkit.utils.StartActivityWithCleanTask;
import ru.konstantin.maltsev.myfirstaidkit.web_services.WebService;

public class EntryActivity extends AppCompatActivity {

    ActivityEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnEntry.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.tiPassword.getText())) {
                InfoMessage.createWarning(binding.getRoot(), "Введите пароль!");
            } else {
                if (binding.tiPassword.getText() == null) return;
                if (binding.tiPassword.getText().toString().replace(" ", "").length() < 1) return;
                setProcessed(true);
                WebService.getInstance().getToken(binding.tiPassword.getText().toString(), new Callback<WebService.Token>() {
                    @Override
                    public void onResponse(@NonNull Call<WebService.Token> call, @NonNull Response<WebService.Token> response) {
                        setProcessed(false);
                        if (response.isSuccessful()) {
                            if (response.body() == null) showError();
                            else {
                                if (response.body().isOk()) {
                                    Setting setting = new Setting(EntryActivity.this);
                                    setting.setToken(response.body().getToken());
                                    StartActivityWithCleanTask.start(EntryActivity.this, MedicationListActivity.class);
                                } else showError();
                            }
                        } else {
                            if (response.code() == 404) {
                                new Setting(EntryActivity.this).clearServiceUrl();
                                StartActivityWithCleanTask.start(EntryActivity.this, GetApiUrlActivity.class);
                            }
                            showError();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WebService.Token> call, @NonNull Throwable t) {
                        setProcessed(false);
                        InfoMessage.createBad(binding.getRoot(), "Неправильный пароль");
                        binding.btnEntry.setEnabled(true);
                    }
                });
            }
        });
    }

    private void showError() {
        InfoMessage.createBad(binding.getRoot(), "Ошибка");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Setting setting = new Setting(this);

        if (setting.getServiceUrl() == null) {
            StartActivityWithCleanTask.start(this, GetApiUrlActivity.class);
            return;
        }

        WebService.getInstance().create(this);

        if (setting.getToken() != null) {
            StartActivityWithCleanTask.start(this, MedicationListActivity.class);
        }
    }

    private void setProcessed(boolean inProcessed) {
        binding.progressBar.setVisibility(inProcessed? View.VISIBLE : View.GONE);
        binding.btnEntry.setEnabled(!inProcessed);
        binding.tiPassword.setEnabled(!inProcessed);
    }
}