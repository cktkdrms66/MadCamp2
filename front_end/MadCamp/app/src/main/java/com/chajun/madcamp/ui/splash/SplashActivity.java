package com.chajun.madcamp.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.SharedPrefKey;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.request.KakaoLoginRequest;
import com.chajun.madcamp.data.model.request.NativeLoginRequest;
import com.chajun.madcamp.data.model.response.KakaoLoginResponse;
import com.chajun.madcamp.data.model.response.NativeLoginResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.login.LoginActivity;
import com.chajun.madcamp.ui.main.MainActivity;
import com.chajun.madcamp.util.Util;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        int index = new Random().nextInt(3);

        ImageView icon = findViewById(R.id.splash_icon);
        switch (index) {
            case 0:
                icon.setImageResource(R.drawable.icon_rock);
                break;
            case 1:
                icon.setImageResource(R.drawable.icon_scissor);
                break;
            case 2:
                icon.setImageResource(R.drawable.icon_paper);
                break;
        }

        Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        icon.startAnimation(animFadeIn);

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
        } else {

            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SplashActivity.this, R.string.login_error_2, Toast.LENGTH_SHORT).show();
            return;
        }

        Repository.getInstance().loginNative(new NativeLoginRequest(email, password), new Callback<NativeLoginResponse>() {
            @Override
            public void onResponse(Call<NativeLoginResponse> call, Response<NativeLoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() ==1) {
                        Util.setString(SplashActivity.this, SharedPrefKey.EMAIL, email);
                        Util.setString(SplashActivity.this, SharedPrefKey.PASSWORD, password);
                        Util.setString(SplashActivity.this, SharedPrefKey.LOGIN_TYPE, "N");
                        AppData.userId = response.body().getId();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<NativeLoginResponse> call, Throwable t) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
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
                        Util.setString(SplashActivity.this, SharedPrefKey.EMAIL, email);
                        Util.setString(SplashActivity.this, SharedPrefKey.PASSWORD, password);
                        Util.setString(SplashActivity.this, SharedPrefKey.USER_NAME, userName);
                        Util.setString(SplashActivity.this, SharedPrefKey.LOGIN_TYPE, "K");

                        AppData.userId = res.getId();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<KakaoLoginResponse> call, Throwable t) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void moveMain() {
        new Handler().postDelayed(new Runnable()
        {
            @SuppressLint("ResourceType")
            @Override
            public void run()
            {
                autoLogin();
            }
        }, 1000); // sec초 정도 딜레이를 준 후 시작
    }

}
