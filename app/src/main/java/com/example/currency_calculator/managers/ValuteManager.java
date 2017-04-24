package com.example.currency_calculator.managers;

import android.util.Log;

import com.example.currency_calculator.CurrencyCalculatorApplication;
import com.example.currency_calculator.core.schemas.ValCurs;
import com.example.currency_calculator.core.schemas.Valute;

import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ValuteManager {
    private static ValuteManager instance;
    private List<Valute> valuteList = new ArrayList<>();

    public static ValuteManager instance() {
        return instance == null ? instance = new ValuteManager() : instance;
    }

    public List<Valute> getValuteList() {
        return valuteList;
    }

    public void save(ValCurs valCurs) {
        if (valCurs != null && valCurs.getValutes() != null) {
            valuteList = valCurs.getValutes();
            File file = new File(getFileName());
            Persister serializer = new Persister();
            try {
                serializer.write(valCurs, file);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }

    public void load() {
        Persister serializer = new Persister();
        File file = new File(getFileName());
        try {
            ValCurs valCurs = serializer.read(ValCurs.class, file);
            if (valCurs != null && valCurs.getValutes() != null) {
                valuteList = valCurs.getValutes();
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private String getFileName() {
        return CurrencyCalculatorApplication.getApplication().getExternalFilesDir(null) + "/valute.xml";
    }

}
