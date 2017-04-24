package com.example.currency_calculator.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.currency_calculator.R;
import com.example.currency_calculator.core.schemas.Valute;
import com.example.currency_calculator.managers.ValuteManager;
import com.example.currency_calculator.utils.MoneyValueFilter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Spinner mInputCurrency, mOutputCurrency;
    private EditText mInputSum, mOutputSum;
    private Button mCalculateButton;
    private ValuteAdapter mInputCurrencyAdapter, mOutputCurrencyAdapter;
    private List<Valute> valuteList = ValuteManager.instance().getValuteList();
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
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        }

        mInputCurrency = (Spinner) findViewById(R.id.activity_main_input_currency_spinner);
        mOutputCurrency = (Spinner) findViewById(R.id.activity_main_output_currency_spinner);
        mInputSum = (EditText) findViewById(R.id.activity_main_input_edit_text);
        mInputSum.setFilters(new InputFilter[]{new MoneyValueFilter(), new InputFilter.LengthFilter(10)});
        mOutputSum = (EditText) findViewById(R.id.activity_main_output_edit_text);
        mCalculateButton = (Button) findViewById(R.id.activity_main_calculate_button);
        mCalculateButton.setOnClickListener(onClickListener);
        mInputSum.addTextChangedListener(mTextWatcher);

        addRuble();
        mInputCurrencyAdapter = new ValuteAdapter();
        mOutputCurrencyAdapter = new ValuteAdapter();

        mInputCurrency.setAdapter(mInputCurrencyAdapter);
        mOutputCurrency.setAdapter(mOutputCurrencyAdapter);
    }

    private void calculate() {

        hideKeyboard();

        String inputSum = mInputSum.getText().toString();
        if (inputSum.trim().length() == 0) {
            mInputSum.setError(getString(R.string.activity_main_error_sum_null));
            mInputSum.requestFocus();
            return;
        }

        Double sum = 0d;
        try {
            sum = Double.parseDouble(inputSum);
        } catch (NumberFormatException e) {
            mInputSum.setError(getString(R.string.activity_main_error_invalid_input_format));
            mInputSum.requestFocus();
            return;
        }

        Valute inputValute = mInputCurrencyAdapter.getItem(mInputCurrency.getSelectedItemPosition());
        Valute outputValute = mOutputCurrencyAdapter.getItem(mOutputCurrency.getSelectedItemPosition());

        if (inputValute == null || inputValute.getValue() <= 0 || inputValute.getNominal() <= 0) {
            showErrorDialog(getString(R.string.activity_main_error_empty_input_currency));
            return;
        }

        if (outputValute == null || outputValute.getValue() <= 0 || outputValute.getNominal() <= 0) {
            showErrorDialog(getString(R.string.activity_main_error_empty_output_currency));
            return;
        }

        Double cross_rate = ((inputValute.getValue() / inputValute.getNominal()) / (outputValute.getValue() / outputValute.getNominal()));
        sum *= cross_rate;
        mOutputSum.setText(String.format("%.02f", Math.round(sum * 100.0) / 100.0));
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    private void addRuble() {
        Valute ruble = new Valute();
        ruble.setName(getString(R.string.russian_ruble));
        ruble.setNominal(1);
        ruble.setValue("1");
        valuteList.add(ruble);
        sort();
    }

    private void sort() {
        Collections.sort(valuteList, new Comparator<Valute>() {
            @Override
            public int compare(Valute valute1, Valute valute2) {
                return valute1.getName().compareToIgnoreCase(valute2.getName());
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private final class ValuteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return valuteList == null ? 0 : valuteList.size();
        }

        @Override
        public Valute getItem(int i) {
            return valuteList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.spinner_item, null);
            TextView names = (TextView) view.findViewById(R.id.spinner_item_name);
            if (valuteList.size() > i) {
                names.setText(valuteList.get(i).getName());
            }
            return view;
        }
    }
}
