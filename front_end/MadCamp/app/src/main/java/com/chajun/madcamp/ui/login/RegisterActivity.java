package com.chajun.madcamp.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.data.model.request.NativeRegisterRequest;
import com.chajun.madcamp.data.model.response.NativeRegisterResponse;
import com.chajun.madcamp.data.repository.Repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditTxt;
    private EditText userNameEditTxt;
    private EditText passwordEditTxt;
    private EditText passwordConfirmEditTxt;

    private Button registerBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setBtn();
    }

    private void initViews() {
        emailEditTxt = findViewById(R.id.register_edit_txt_email);
        userNameEditTxt = findViewById(R.id.register_edit_txt_username);
        passwordEditTxt = findViewById(R.id.register_edit_txt_password);
        passwordConfirmEditTxt = findViewById(R.id.register_edit_txt_password_confirm);
        registerBtn = findViewById(R.id.register_button_native_register);
    }

    private void setBtn() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditTxt.getText().toString();
                String userName = userNameEditTxt.getText().toString();
                String password = passwordEditTxt.getText().toString();
                String passwordConfirm = passwordConfirmEditTxt.getText().toString();

                if (email.isEmpty() || userName.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_error_2, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(RegisterActivity.this, R.string.password_not_equal, Toast.LENGTH_SHORT).show();
                    return;
                }

                Repository.getInstance().registerNative(new NativeRegisterRequest(email, userName, password), new Callback<NativeRegisterResponse>() {
                    @Override
                    public void onResponse(Call<NativeRegisterResponse> call, Response<NativeRegisterResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() == 1) {
                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(R.string.register_success);
                                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                builder.create().show();

                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<NativeRegisterResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
