package ru.konstantin.maltsev.myfirstaidkit.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.konstantin.maltsev.myfirstaidkit.databinding.ActivityGetApiUrlBinding;
import ru.konstantin.maltsev.myfirstaidkit.utils.InfoMessage;
import ru.konstantin.maltsev.myfirstaidkit.utils.Setting;
import ru.konstantin.maltsev.myfirstaidkit.utils.StartActivityWithCleanTask;
import ru.konstantin.maltsev.myfirstaidkit.web_services.ServerPointerResponse;
import ru.konstantin.maltsev.myfirstaidkit.web_services.ServerPointerService;

public class GetApiUrlActivity extends AppCompatActivity {

    ActivityGetApiUrlBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetApiUrlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGetWebService.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.tiName.getText())
                    && !TextUtils.isEmpty(binding.tiPassword.getText())) {
                getUrl();
            } else {
                InfoMessage.createWarning(binding.getRoot(), "Заполните все поля!");
            }
        });
    }

    private void getUrl() {
        if (binding.tiName.getText() == null || binding.tiPassword.getText() == null) {
            showError();
            return;
        }
        ServerPointerService.Owner owner = new ServerPointerService.Owner();
        owner.setName(binding.tiName.getText().toString());
        owner.setPassword(binding.tiPassword.getText().toString());

        setProcessed(true);
        ServerPointerService.getApiUrl(owner, new Callback<ServerPointerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerPointerResponse> call, @NonNull Response<ServerPointerResponse> response) {
                setProcessed(false);
                if (response.isSuccessful()) {
                    ServerPointerResponse serverPointerResponse = response.body();
                    if (serverPointerResponse != null && serverPointerResponse.isFound()) {
                        Setting setting = new Setting(GetApiUrlActivity.this);
                        setting.setServiceUrl(serverPointerResponse.getUrl());
                        StartActivityWithCleanTask.start(GetApiUrlActivity.this, EntryActivity.class);
                    } else {
                        showError();
                    }
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerPointerResponse> call, @NonNull Throwable t) {
                showError();
                setProcessed(false);
            }
        });
    }

    private void showError() {
        InfoMessage.createBad(binding.getRoot(), "Ошибка!");
    }

    private void setProcessed(boolean inProcessed) {
        binding.progressBar.setVisibility(inProcessed? View.VISIBLE : View.GONE);
        binding.btnGetWebService.setEnabled(!inProcessed);
        binding.tiName.setEnabled(!inProcessed);
        binding.tiPassword.setEnabled(!inProcessed);
    }
}