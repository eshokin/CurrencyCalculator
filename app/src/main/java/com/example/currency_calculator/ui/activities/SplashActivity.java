package com.example.currency_calculator.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.currency_calculator.R;
import com.example.currency_calculator.core.RequestExecutor;
import com.example.currency_calculator.core.schemas.ValCurs;
import com.example.currency_calculator.managers.ValuteManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        }

        loadData();
    }

    private void loadData() {
        RequestExecutor.getInstance().getExchangeRates(new RequestExecutor.ResponseListener() {
            @Override
            public void onResponseListener(ValCurs result, String error) {
                if (!isFinishing()) {
                    if (TextUtils.isEmpty(error) && result != null && result.getValutes() != null && result.getValutes().size() > 0) {
                        ValuteManager.instance().save(result);
                        Toast.makeText(getApplicationContext(), R.string.activity_splash_date_successfully_update, Toast.LENGTH_SHORT).show();
                    } else {
                        ValuteManager.instance().load();
                        Toast.makeText(getApplicationContext(), R.string.activity_splash_date_not_update, Toast.LENGTH_SHORT).show();
                    }
                    startMainActivity();
                }
            }
        });

    }

    private void startMainActivity() {
        Intent startIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(startIntent);
        finish();
    }
}
