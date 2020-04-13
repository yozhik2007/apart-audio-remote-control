package com.yozhik.apartremotecontroller.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yozhik.apartremotecontroller.Global;
import com.yozhik.R;
import com.yozhik.apartremotecontroller.data.repository.SharedPreferencesRepository;
import com.yozhik.apartremotecontroller.presentation.dialog.ChangeNameDialog;

public class SelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selector);

        ((TextView) findViewById(R.id.zone1_tv)).setText(SharedPreferencesRepository.getZoneName(this, 1));
        ((TextView) findViewById(R.id.zone2_tv)).setText(SharedPreferencesRepository.getZoneName(this, 2));
        ((TextView) findViewById(R.id.zone3_tv)).setText(SharedPreferencesRepository.getZoneName(this, 3));
        ((TextView) findViewById(R.id.zone4_tv)).setText(SharedPreferencesRepository.getZoneName(this, 4));

        findViewById(R.id.zone1_rl).setOnClickListener(view -> navigateToMainActivity(1));
        findViewById(R.id.zone2_rl).setOnClickListener(view -> navigateToMainActivity(2));
        findViewById(R.id.zone3_rl).setOnClickListener(view -> navigateToMainActivity(3));
        findViewById(R.id.zone4_rl).setOnClickListener(view -> navigateToMainActivity(4));

        findViewById(R.id.edit_zone1_iv).setOnClickListener(
                view -> new ChangeNameDialog(
                        this,
                        SharedPreferencesRepository.getZoneName(this, 1),
                        name -> {
                            SharedPreferencesRepository.setZoneName(SelectorActivity.this, 1, name);
                            ((TextView) findViewById(R.id.zone1_tv)).setText(name);
                        }
                ).show());

        findViewById(R.id.edit_zone2_iv).setOnClickListener(
                view -> new ChangeNameDialog(
                        this,
                        SharedPreferencesRepository.getZoneName(this, 2),
                        name -> {
                            SharedPreferencesRepository.setZoneName(SelectorActivity.this, 2, name);
                            ((TextView) findViewById(R.id.zone2_tv)).setText(name);
                        }
                ).show());

        findViewById(R.id.edit_zone3_iv).setOnClickListener(
                view -> new ChangeNameDialog(
                        this,
                        SharedPreferencesRepository.getZoneName(this, 3),
                        name -> {
                            SharedPreferencesRepository.setZoneName(SelectorActivity.this, 3, name);
                            ((TextView) findViewById(R.id.zone3_tv)).setText(name);
                        }
                ).show());

        findViewById(R.id.edit_zone4_iv).setOnClickListener(
                view -> new ChangeNameDialog(
                        this,
                        SharedPreferencesRepository.getZoneName(this, 4),
                        name -> {
                            SharedPreferencesRepository.setZoneName(SelectorActivity.this, 4, name);
                            ((TextView) findViewById(R.id.zone4_tv)).setText(name);
                        }
                ).show());

        findViewById(R.id.settings_bt).setOnClickListener(view -> navigateToOptionsActivity());
    }

    void navigateToMainActivity(Integer zoneIndex) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Global.ZONE_INDEX, zoneIndex);
        startActivity(intent);
    }

    void navigateToOptionsActivity() {
        startActivity(new Intent(this, OptionsActivity.class));
    }
}
