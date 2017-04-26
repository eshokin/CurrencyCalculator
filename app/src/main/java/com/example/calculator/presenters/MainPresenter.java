package com.example.calculator.presenters;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.calculator.CalculatorApplication;
import com.example.calculator.R;
import com.example.calculator.models.Valute;
import com.example.calculator.utils.DBHelper;
import com.example.calculator.views.MainView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainPresenter extends BasePresenter<List<Valute>, MainView> {

    @Override
    protected void updateView() {
        if (model.size() == 0) {
            view().showNoData();
        } else {
            view().showValutes(model);
        }
    }

    @Override
    public void bindView(@NonNull MainView view) {
        super.bindView(view);

        if (model == null) {
            view().showLoading(true);
            loadData();
        }
    }

    public void calculateExchange(Double sum, Double inputValute, Integer inputValuteNominal, Double outputValute, Integer outputValuteNominal) {
        new CalculateExchange(sum, inputValute, inputValuteNominal, outputValute, outputValuteNominal).execute();
    }

    private void loadData() {
        new LoadData().execute();
    }

    private class CalculateExchange extends AsyncTask<Void, Void, String> {

        private Double mSum, mInputValute, mOutputValute;
        private Integer mInputValuteNominal, mOutputValuteNominal;

        public CalculateExchange(Double sum, Double inputValute, Integer inputValuteNominal, Double outputValute, Integer outputValuteNominal) {
            mSum = sum;
            mInputValute = inputValute;
            mInputValuteNominal = inputValuteNominal;
            mOutputValute = outputValute;
            mOutputValuteNominal = outputValuteNominal;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (mSum != null && mInputValute != null && mInputValuteNominal != null && mInputValuteNominal > 0 && mOutputValute != null && mOutputValuteNominal != null && mOutputValuteNominal > 0) {
                Double cross_rate = ((mInputValute / mInputValuteNominal) / (mOutputValute / mOutputValuteNominal));
                mSum *= cross_rate;
                return String.format("%.02f", Math.round(mSum * 100.0) / 100.0);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String exchange) {
            view().showLoading(false);
            view().showExchange(exchange);
        }
    }

    private class LoadData extends AsyncTask<Void, Void, List<Valute>> {
        @Override
        protected List<Valute> doInBackground(Void... params) {
            List<Valute> valutes = DBHelper.instance().getValuteList();
            if (valutes.size() > 0) {
                valutes.add(addRuble());
                Collections.sort(valutes, new Comparator<Valute>() {
                    @Override
                    public int compare(Valute valute1, Valute valute2) {
                        return valute1.getName().compareToIgnoreCase(valute2.getName());
                    }
                });
            }
            return valutes;
        }

        private Valute addRuble() {
            Valute ruble = new Valute();
            ruble.setName(CalculatorApplication.getApplication().getString(R.string.russian_ruble));
            ruble.setNominal(1);
            ruble.setValue("1");
            return ruble;
        }

        @Override
        protected void onPostExecute(List<Valute> valutes) {
            view().showLoading(false);
            setModel(valutes);
        }
    }
}
