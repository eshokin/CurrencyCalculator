package com.example.calculator.presenters;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.calculator.utils.DBHelper;
import com.example.calculator.R;
import com.example.calculator.utils.RequestExecutor;
import com.example.calculator.models.ValCurs;
import com.example.calculator.models.Valute;
import com.example.calculator.views.SplashView;

import java.util.List;

public class SplashPresenter extends BasePresenter<List<Valute>, SplashView> {

    private boolean isLoading = false;

    @Override
    protected void updateView() {
        view().showProcessUpdate(model.size() == 0 ? R.string.activity_splash_date_not_update : R.string.activity_splash_date_successfully_update);
        view().startMain();
    }

    @Override
    public void bindView(@NonNull SplashView view) {
        super.bindView(view);

        if (model == null && !isLoading) {
            view().showLoading(true);
            loadData();
        }
    }

    private void loadData() {
        RequestExecutor requestExecutor = new RequestExecutor();
        requestExecutor.getExchangeRates(new RequestExecutor.ResponseListener() {
            @Override
            public void onResponseListener(ValCurs result, String error) {
                if (TextUtils.isEmpty(error) && result != null && result.getValutes() != null && result.getValutes().size() > 0) {
                    new saveData(result.getValutes()).execute();
                } else {
                    isLoading = false;
                    view().showLoading(false);
                    updateView();
                }
            }
        });
    }

    private class saveData extends AsyncTask<Void, Void, Void> {
        List<Valute> mValutes;

        public saveData(List<Valute> valutes) {
            mValutes = valutes;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DBHelper.instance().updateValutes(mValutes);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isLoading = false;
            view().showLoading(false);
            setModel(mValutes);
        }
    }
}
