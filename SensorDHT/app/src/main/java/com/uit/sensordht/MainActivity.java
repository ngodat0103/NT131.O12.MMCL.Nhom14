package com.uit.sensordht;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CreateUserCallback;
import com.uit.sensordht.Model.User;

public class MainActivity extends AppCompatActivity{
    AppCompatButton btSignUp, btSignIn;
    String username, password, email, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSignUp = findViewById(R.id.btSignUp);
        btSignIn = findViewById(R.id.btSignIn);
        btSignUp.setOnClickListener(v -> {
            showBottomSheetDialogSignUp();
        });
        btSignIn.setOnClickListener(v -> {
            showBottomSheetDialogSignIn();
        });
    }
    private void showBottomSheetDialogSignUp() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Base_Theme_SensorDHT);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_signup, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetView.setBackgroundResource(R.drawable.bg_dialog);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        AppCompatButton btBack = bottomSheetDialog.findViewById(R.id.btBack);
        AppCompatButton btSignUp = bottomSheetDialog.findViewById(R.id.btSignUp);
        TextInputEditText etUsername = bottomSheetDialog.findViewById(R.id.tietUsername);
        TextInputEditText etPassword = bottomSheetDialog.findViewById(R.id.tietPassword);
        TextInputEditText etEmail = bottomSheetDialog.findViewById(R.id.tietEmail);
        TextInputEditText etConfirmPassword = bottomSheetDialog.findViewById(R.id.tietConfirmPassword);
        btSignUp.setOnClickListener(v -> {
            username = String.valueOf(etUsername.getText());
            password = String.valueOf(etPassword.getText());
            email = String.valueOf(etEmail.getText());
            confirmPassword = String.valueOf(etConfirmPassword.getText());
            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(confirmPassword))
            {
                Toast.makeText(this, "Please fill in", Toast.LENGTH_SHORT).show();
                etEmail.setText(null);
                etConfirmPassword.setText(null);
                etPassword.setText(null);
                etUsername.setText(null);
            }
            else
            {
                APIManager.fnCreateAccount(email, password, username, new CreateUserCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btBack.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }
        });


        // Set the layout params if needed
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void showBottomSheetDialogSignIn() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Base_Theme_SensorDHT);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_signin, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetView.setBackgroundResource(R.drawable.bg_dialog);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
        AppCompatButton btBack = bottomSheetDialog.findViewById(R.id.btBack);
        btBack.setOnClickListener(v -> {
            if(bottomSheetDialog.isShowing())
            {
                bottomSheetDialog.dismiss();
            }
        });
        // Set the layout params if needed
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}