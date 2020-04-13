package com.yozhik.apartremotecontroller.presentation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.yozhik.R;

public class ChangeNameDialog extends Dialog {

    private OnSaveListener onSaveListener;
    private String oldName;

    public ChangeNameDialog(@NonNull Context context, String oldName, OnSaveListener onSaveListener) {
        super(context);
        this.oldName = oldName;
        this.onSaveListener = onSaveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_change_name);

        setCancelable(true);

        EditText nameEditText = findViewById(R.id.name_et);
        nameEditText.setText(oldName);
        findViewById(R.id.save_bt).setOnClickListener(view -> {
            if (!nameEditText.getText().toString().isEmpty()) {
                onSaveListener.onSaveClick(nameEditText.getText().toString());
                dismiss();
            }
        });
    }

    public interface OnSaveListener {
        void onSaveClick(String name);
    }
}
