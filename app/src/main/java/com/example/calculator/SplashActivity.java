package com.example.calculator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.calculator.presenters.SplashPresenter;
import com.example.calculator.utils.PresenterManager;
import com.example.calculator.views.SplashView;

public class SplashActivity extends AppCompatActivity implements SplashView {

    private SplashPresenter presenter;
    private LinearLayout mLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            presenter = new SplashPresenter();
        } else {
            presenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        }
        mLoader = (LinearLayout) findViewById(R.id.splash_activity_loader);
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.unbindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        PresenterManager.getInstance().savePresenter(presenter, outState);
    }

    @Override
    public void showLoading(boolean show) {
        mLoader.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProcessUpdate(Integer resource) {
        Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startMain() {
        Intent startIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(startIntent);
        finish();
    }
}
