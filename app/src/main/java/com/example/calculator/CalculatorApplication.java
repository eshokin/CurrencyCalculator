package com.example.calculator;

import android.app.Application;

public class CalculatorApplication extends Application {

    private static CalculatorApplication mApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInstance = this;

    }

    public static CalculatorApplication getApplication() {
        return mApplicationInstance;
    }
}
