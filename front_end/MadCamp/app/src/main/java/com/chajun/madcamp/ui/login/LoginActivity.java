package com.chajun.madcamp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.SharedPrefKey;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.request.KakaoLoginRequest;
import com.chajun.madcamp.data.model.request.NativeLoginRequest;
import com.chajun.madcamp.data.model.response.KakaoLoginResponse;
import com.chajun.madcamp.data.model.response.NativeLoginResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.main.MainActivity;
import com.chajun.madcamp.util.Util;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText idEditTxt;
    private EditText passwordEditTxt;

    private Button loginBtn;
    private Button registerBtn;

    private Button kakaoLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autoLogin();


        setContentView(R.layout.activity_login);

        System.out.println("qwejpqjweiop");
        KakaoSdk.init(this, Constant.kakaoNativeKey);

        initViews();

        setBtns();
    }

    private void autoLogin() {
        String email = Util.getString(this, SharedPrefKey.EMAIL);
        String password = Util.getString(this, SharedPrefKey.PASSWORD);
        String userName = Util.getString(this, SharedPrefKey.USER_NAME);
        String loginType = Util.getString(this, SharedPrefKey.LOGIN_TYPE);
        if (!email.isEmpty() && !password.isEmpty()) {
            if (loginType.equals("N")) {
                login(email, password);
            } else {
                kakaoLogin(email, userName, password);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (idEditTxt != null && passwordEditTxt != null) {
            idEditTxt.setText("");
            passwordEditTxt.setText("");
        }
    }

    private void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.login_error_2, Toast.LENGTH_SHORT).show();
            return;
        }

        Repository.getInstance().loginNative(new NativeLoginRequest(email, password), new Callback<NativeLoginResponse>() {
            @Override
            public void onResponse(Call<NativeLoginResponse> call, Response<NativeLoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() ==1) {
                        Util.setString(LoginActivity.this, SharedPrefKey.EMAIL, email);
                        Util.setString(LoginActivity.this, SharedPrefKey.PASSWORD, password);
                        Util.setString(LoginActivity.this, SharedPrefKey.LOGIN_TYPE, "N");
                        AppData.userId = response.body().getId();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NativeLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void kakaoLogin(String email, String userName, String password) {
        Repository.getInstance().loginKakao(new KakaoLoginRequest(email,
                userName, password), new Callback<KakaoLoginResponse>() {
            @Override
            public void onResponse(Call<KakaoLoginResponse> call, Response<KakaoLoginResponse> response) {
                if (response.isSuccessful()) {
                    KakaoLoginResponse res = response.body();
                    if (res.getSuccess() == 1) {
                        Util.setString(LoginActivity.this, SharedPrefKey.EMAIL, email);
                        Util.setString(LoginActivity.this, SharedPrefKey.PASSWORD, password);
                        Util.setString(LoginActivity.this, SharedPrefKey.USER_NAME, userName);
                        Util.setString(LoginActivity.this, SharedPrefKey.LOGIN_TYPE, "K");

                        AppData.userId = res.getId();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KakaoLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews() {
        idEditTxt = findViewById(R.id.login_edit_txt_username);
        passwordEditTxt = findViewById(R.id.login_edit_txt_password);
        loginBtn = findViewById(R.id.login_button_native_login);
        registerBtn = findViewById(R.id.login_button_native_register);
        kakaoLoginBtn = findViewById(R.id.login_button_kakao_login);
    }

    private void setBtns() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = idEditTxt.getText().toString();
                String password = passwordEditTxt.getText().toString();

                login(email, password);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    // 카카오톡이 있을 경우?
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }


            }


            Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                    System.out.println(oAuthToken.getAccessToken());

                    if (oAuthToken != null) {
                        System.out.println(oAuthToken.getAccessToken() + " " + oAuthToken.getRefreshToken());
                    }

                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {
                            if (user != null) {
                                String email = user.getKakaoAccount().getEmail();
                                String password = user.getId() + "";
                                String userName = user.getKakaoAccount().getProfile().getNickname();
                                kakaoLogin(email, userName, password);

                            }
                            if (throwable != null) {
                                // 로그인 시 오류 났을 때
                                // 키해시가 등록 안 되어 있으면 오류 납니다.
                                System.out.println("invoke: " + throwable.getLocalizedMessage());
                            }
                            return null;
                        }


                    });

                    return null;
                }
            };
        });
    }

}
