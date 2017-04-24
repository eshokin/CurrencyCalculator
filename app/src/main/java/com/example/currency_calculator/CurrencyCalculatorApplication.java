package com.example.currency_calculator;

import android.app.Application;

public class CurrencyCalculatorApplication extends Application {

    private static CurrencyCalculatorApplication mApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInstance = this;

    }

    public static CurrencyCalculatorApplication getApplication() {
        return mApplicationInstance;
    }
}
