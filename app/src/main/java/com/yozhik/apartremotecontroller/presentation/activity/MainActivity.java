package com.yozhik.apartremotecontroller.presentation.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import kotlin.text.Charsets;
import moe.codeest.rxsocketclient.RxSocketClient;
import moe.codeest.rxsocketclient.SocketClient;
import moe.codeest.rxsocketclient.SocketSubscriber;
import moe.codeest.rxsocketclient.meta.SocketConfig;
import moe.codeest.rxsocketclient.meta.SocketOption;
import moe.codeest.rxsocketclient.meta.ThreadStrategy;
import com.yozhik.apartremotecontroller.Global;
import com.yozhik.R;
import com.yozhik.apartremotecontroller.data.repository.SharedPreferencesRepository;
import com.yozhik.apartremotecontroller.presentation.adapter.SourceAdapter;
import com.yozhik.apartremotecontroller.presentation.dialog.ChangeNameDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    Integer currentVolume = null;
    Boolean isMute = null;
    String currentSource = null;

    SocketClient socketClient;
    Disposable socketDisposable;

    private static final int DEFAULT_RESPONSE_LENGTH = 5;
    private static final int SOCKET_TIMEOUT = 30 * 1000;    //milliseconds
    private static final int TASK_DELAY = 500;    //milliseconds

    //source spinner UI
    Spinner spinner;
    SourceAdapter adapter;
    AdapterView.OnItemSelectedListener sourceListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            currentSource = String.format(getString(R.string.source_number_format), i + 1);
            if (isSourceInitialized) {
                sendMessage(buildSourceMessage());
            } else {
                isSourceInitialized = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    boolean isSourceInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSocket();

        ((TextView) findViewById(R.id.title_tv)).setText(SharedPreferencesRepository.getZoneName(this, getCurrentZoneIndex()));

        findViewById(R.id.volume_up_bt).setOnClickListener(view -> {
            if (currentVolume != null) {
                currentVolume++;
                updateVolume();
                sendMessage(buildVolumeUpMessage());
            }
        });

        findViewById(R.id.volume_down_bt).setOnClickListener(view -> {
            if (currentVolume != null) {
                currentVolume--;
                updateVolume();
                sendMessage(buildVolumeDownMessage());
            }
        });

        findViewById(R.id.mute_bt).setOnClickListener(view -> {
            if (isMute != null) {
                isMute = !isMute;
                updateMute();
                sendMessage(buildMuteMessage());
            }
        });

        initSourceSpinner(0);
    }

    private void initSourceSpinner(Integer selectedIndex) {
        spinner = findViewById(R.id.source_sp);

        List<String> sources = new ArrayList<>();
        sources.add(SharedPreferencesRepository.getSourceName(this, 0));
        sources.add(SharedPreferencesRepository.getSourceName(this, 1));
        sources.add(SharedPreferencesRepository.getSourceName(this, 2));
        sources.add(SharedPreferencesRepository.getSourceName(this, 3));

        adapter = new SourceAdapter(this, R.layout.item_dropdown_source, R.id.title_tv, sources);
        adapter.setDropDownViewResource(R.layout.item_dropdown_source);
        adapter.setOnEditClickListener(
                index -> new ChangeNameDialog(
                        this,
                        SharedPreferencesRepository.getSourceName(this, index),
                        name -> {
                            SharedPreferencesRepository.setSourceName(MainActivity.this, index, name);
                            initSourceSpinner(index);
                        }
                ).show()
        );

        spinner.setAdapter(adapter);
        spinner.setSelected(false);
        spinner.setSelection(selectedIndex, false);
        spinner.setOnItemSelectedListener(sourceListener);
    }

    Integer getCurrentZoneIndex() {
        return this.getIntent().getIntExtra(Global.ZONE_INDEX, -1);
    }

    String getCurrentZoneNumber() {
        Integer index = getCurrentZoneIndex();
        switch (index) {
            case 1:
                return "a";
            case 2:
                return "b";
            case 3:
                return "c";
            case 4:
                return "d";
            default:
                finish();
        }
        return null;
    }

    void updateVolume() {
        ((TextView) findViewById(R.id.current_volume_tv)).setText(String.valueOf(currentVolume));
    }


    void updateMute() {
        Button muteButton = findViewById(R.id.mute_bt);
        if (isMute) {
            muteButton.setText(R.string.sound_switch_on_title);
            muteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            muteButton.setText(R.string.sound_switch_off_title);
            muteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    void updateSource() {
        spinner.setSelection(Integer.parseInt(currentSource) - 1);
        adapter.notifyDataSetChanged();
    }

    private String buildVolumeUpMessage() {
        return String.format(getString(R.string.volume_up_request_format), getCurrentZoneNumber());
    }

    private String buildVolumeDownMessage() {
        return String.format(getString(R.string.volume_down_request_format), getCurrentZoneNumber());
    }

    private String buildMuteMessage() {
        return String.format(getString(R.string.mute_request_format), getCurrentZoneNumber(), isMute ? getString(R.string.mute_enabled_value) : getString(R.string.mute_disabled_value));
    }

    private String buildSourceMessage() {
        return String.format(getString(R.string.source_request_format), getCurrentZoneNumber(), currentSource);
    }

    void initSocket() {

        socketClient = RxSocketClient
                .create(new SocketConfig.Builder()
                        .setIp(SharedPreferencesRepository.getIp(this))
                        .setPort(SharedPreferencesRepository.getPort(this))
                        .setCharset(Charsets.UTF_8)
                        .setThreadStrategy(ThreadStrategy.ASYNC)
                        .setTimeout(SOCKET_TIMEOUT)
                        .build())
                .option(new SocketOption.Builder()
                        .build());

        socketDisposable = socketClient.connect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SocketSubscriber() {
                    @Override
                    public void onConnected() {
                        sendInitCommands();
                    }

                    @Override
                    public void onDisconnected() {
                        initSocket();
                    }

                    @Override
                    public void onResponse(@NotNull byte[] data) {
                        Log.e(TAG, new String(data, StandardCharsets.UTF_8));
                        if (data.length == DEFAULT_RESPONSE_LENGTH) {
                            parseResponse(new String(data, StandardCharsets.UTF_8));
                        }
                    }
                }, throwable -> Log.e(TAG, throwable.toString()));
    }

    void parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return;
        }

        String buffer;

        //source
        String sourcePattern = String.format(getString(R.string.source_response_format), getCurrentZoneNumber());
        int sourceIndex = response.lastIndexOf(sourcePattern);
        buffer = response.substring(sourceIndex + sourcePattern.length());
        if (sourceIndex != -1) {
            currentSource = buffer;
            updateSource();
        }

        //mute
        String mutePattern = String.format(getString(R.string.mute_response_format), getCurrentZoneNumber());
        int muteIndex = response.lastIndexOf(mutePattern);
        buffer = response.substring(muteIndex + mutePattern.length());
        if (muteIndex != -1) {
            if (buffer.equals(getString(R.string.mute_enabled_value))) {
                isMute = true;
            } else if (buffer.equals(getString(R.string.mute_disabled_value))) {
                isMute = false;
            }

            updateMute();
        }

        //volume
        String volumePattern = String.format(getString(R.string.volume_response_format), getCurrentZoneNumber());
        int volumeIndex = response.lastIndexOf(volumePattern);
        buffer = response.substring(response.lastIndexOf(volumePattern) + volumePattern.length());
        if (volumeIndex != -1) {
            currentVolume = Integer.parseInt(buffer);
            updateVolume();
        }

        if (currentSource != null && currentVolume != null && isMute != null) {
            findViewById(R.id.loading_rl).setVisibility(View.GONE);
        }
    }

    private void sendInitCommands() {
        List<Runnable> tasks = new ArrayList<>();

        tasks.add(() -> sendMessage(String.format(getString(R.string.mute_status_request_format), getCurrentZoneNumber())));   //fetch mute status
        tasks.add(() -> sendMessage(String.format(getString(R.string.source_status_request_format), getCurrentZoneNumber())));   //fetch source status
        tasks.add(() -> sendMessage(String.format(getString(R.string.volume_status_request_format), getCurrentZoneNumber())));   //fetch volume

        for (int i = 0; i < tasks.size(); i++) {
            new Handler().postDelayed(tasks.get(i), i * TASK_DELAY);
        }
    }

    void sendMessage(final String message) {
        socketClient.sendData(message);
    }
}
