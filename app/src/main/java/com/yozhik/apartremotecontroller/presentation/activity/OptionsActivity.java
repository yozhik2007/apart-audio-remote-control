package com.yozhik.apartremotecontroller.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.yozhik.R;
import com.yozhik.apartremotecontroller.data.repository.SharedPreferencesRepository;

public class OptionsActivity extends AppCompatActivity {

    EditText ipEditText;
    EditText portEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ipEditText = findViewById(R.id.ip_et);
        portEditText = findViewById(R.id.port_et);

        ipEditText.setText(SharedPreferencesRepository.getIp(this));
        portEditText.setText(String.valueOf(SharedPreferencesRepository.getPort(this)));

        findViewById(R.id.login_bt).setOnClickListener(view -> {
            saveCredentials();
            navigateToSelectorActivity();
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    void navigateToSelectorActivity() {
        finish();
        startActivity(new Intent(this, SelectorActivity.class));
    }

    void saveCredentials() {
        SharedPreferencesRepository.setHost(
                this,
                ipEditText.getText().toString(),
                Integer.parseInt(portEditText.getText().toString())
        );
    }
}
