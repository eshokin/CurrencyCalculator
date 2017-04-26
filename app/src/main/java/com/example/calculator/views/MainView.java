package com.example.calculator.views;


import com.example.calculator.models.Valute;

import java.util.List;

public interface MainView {

    public void showLoading(boolean show);

    public void showNoData();

    public void showValutes(List<Valute> valutes);

    public void showExchange(String exchange);
}
