package com.uit.sensordht;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.uit.sensordht.Interface.DialogListener;

public class TemperatureDialogFragment extends DialogFragment {
    SecondFragment secondFragment;
    public static TemperatureDialogFragment newInstance() {
        return new TemperatureDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sheet_temperature, container, false);

        // Thêm Fragment vào Dialog
        secondFragment = new SecondFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fl_temperature, secondFragment) // R.id.fl_temperature là ID của layout trong dialog_sheet_temperature.xml để chứa Fragment
                .commit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_info);
        setCancelable(true);
    }
    @Override
    public void onResume() {
        super.onResume();

        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.windowAnimations = R.style.DialogAnimation;
                window.setAttributes(params);
            }
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (secondFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(secondFragment)
                    .commit();
            secondFragment = null; // Reset the reference to null
        }
    }

}
