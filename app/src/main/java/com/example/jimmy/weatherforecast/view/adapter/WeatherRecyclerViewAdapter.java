package com.example.jimmy.weatherforecast.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jimmy.weatherforecast.BR;
import com.example.jimmy.weatherforecast.R;
import com.example.jimmy.weatherforecast.model.WeatherData;

import java.util.List;


public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder> {


    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_REG = 1;
    private static final int ITEM_TODAY_RESOURCE = R.layout.list_item_today;
    private static final int ITEM_REG_RESOURCE = R.layout.list_item_reg;
    private List<WeatherData> list;


    public WeatherRecyclerViewAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_REG;
        }
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                binding = DataBindingUtil.inflate(li, ITEM_TODAY_RESOURCE, viewGroup, false);
                break;

            case VIEW_TYPE_REG:
                binding = DataBindingUtil.inflate(li, ITEM_REG_RESOURCE, viewGroup, false);
                break;

            default:
                throw new IllegalArgumentException();
        }
        WeatherViewHolder viewHolder = new WeatherViewHolder(binding);
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder viewHolder, int position) {
        viewHolder.getBinding().setVariable(BR.weatherData, list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        public WeatherViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    public List<WeatherData> getList() {
        return list;
    }

    public void setList(List<WeatherData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}