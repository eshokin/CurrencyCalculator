package com.example.calculator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.calculator.R;
import com.example.calculator.models.Valute;

import java.util.List;

public class ValuteAdapter extends BaseAdapter {

    private Context mContext;
    private List<Valute> mValuteList;

    public ValuteAdapter(Context context, List<Valute> valuteList) {
        mContext = context;
        mValuteList = valuteList;
    }

    @Override
    public int getCount() {
        return mValuteList == null ? 0 : mValuteList.size();
    }

    @Override
    public Valute getItem(int i) {
        return mValuteList == null ? null : mValuteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.spinner_item_name);
        if (mValuteList.size() > i) {
            names.setText(mValuteList.get(i).getName());
        }
        return view;
    }
}
