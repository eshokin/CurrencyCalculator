package com.example.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.calculator.adapter.ValuteAdapter;
import com.example.calculator.models.Valute;
import com.example.calculator.presenters.MainPresenter;
import com.example.calculator.utils.MoneyValueFilter;
import com.example.calculator.utils.PresenterManager;
import com.example.calculator.views.MainView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Spinner mInputCurrency, mOutputCurrency;
    private EditText mInputSum, mOutputSum;
    private Button mCalculateButton;
    private ValuteAdapter mInputCurrencyAdapter, mOutputCurrencyAdapter;
    private MainPresenter presenter;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calculate();
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().startsWith("0")) {
                mInputSum.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            presenter = new MainPresenter();
        } else {
            presenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        }

        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
        mInputCurrency = (Spinner) findViewById(R.id.activity_main_input_currency_spinner);
        mOutputCurrency = (Spinner) findViewById(R.id.activity_main_output_currency_spinner);
        mInputSum = (EditText) findViewById(R.id.activity_main_input_edit_text);
        mInputSum.setFilters(new InputFilter[]{new MoneyValueFilter(), new InputFilter.LengthFilter(10)});
        mOutputSum = (EditText) findViewById(R.id.activity_main_output_edit_text);
        mCalculateButton = (Button) findViewById(R.id.activity_main_calculate_button);
        mCalculateButton.setOnClickListener(onClickListener);
        mInputSum.addTextChangedListener(mTextWatcher);
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


    private void calculate() {

        hideKeyboard();

        Double sum = 0d;

        String inputSum = mInputSum.getText().toString();
        if (inputSum.trim().length() == 0) {
            mInputSum.setError(getString(R.string.activity_main_error_sum_null));
            mInputSum.requestFocus();
            return;
        }

        try {
            sum = Double.parseDouble(inputSum);
        } catch (NumberFormatException e) {
            mInputSum.setError(getString(R.string.activity_main_error_invalid_input_format));
            mInputSum.requestFocus();
            return;
        }

        Valute inputValute = mInputCurrencyAdapter.getItem(mInputCurrency.getSelectedItemPosition());
        Valute outputValute = mOutputCurrencyAdapter.getItem(mOutputCurrency.getSelectedItemPosition());

        Double inputValuteValue = 0d;
        Integer inputValuteNominal = 0;

        if (inputValute == null || inputValute.getNominal() <= 0) {
            showErrorDialog(getString(R.string.activity_main_error_empty_input_currency));
            return;
        }

        inputValuteNominal = inputValute.getNominal();

        try {
            inputValuteValue = Double.parseDouble(inputValute.getValue().replace(",", "."));
        } catch (NumberFormatException e) {
            showErrorDialog(getString(R.string.activity_main_error_empty_input_currency));
            return;
        }

        Double outputValuteValue = 0d;
        Integer outputValuteNominal = 0;

        if (outputValute == null || outputValute.getNominal() <= 0) {
            showErrorDialog(getString(R.string.activity_main_error_empty_output_currency));
            return;
        }

        outputValuteNominal = outputValute.getNominal();

        try {
            outputValuteValue = Double.parseDouble(outputValute.getValue().replace(",", "."));
        } catch (NumberFormatException e) {
            showErrorDialog(getString(R.string.activity_main_error_empty_input_currency));
            return;
        }

        presenter.calculateExchange(sum, inputValuteValue, inputValuteNominal, outputValuteValue, outputValuteNominal);
    }


    @Override
    public void showLoading(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mCalculateButton.setEnabled(!show);
    }

    @Override
    public void showNoData() {
        showErrorDialog(getString(R.string.activity_main_error_no_data));
    }

    @Override
    public void showValutes(List<Valute> valutes) {


        mInputCurrencyAdapter = new ValuteAdapter(this, valutes);
        mOutputCurrencyAdapter = new ValuteAdapter(this, valutes);

        mInputCurrency.setAdapter(mInputCurrencyAdapter);
        mOutputCurrency.setAdapter(mOutputCurrencyAdapter);
    }

    @Override
    public void showExchange(String exchange) {
        mOutputSum.setText(exchange);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_button, null)
                .show();
    }
}
