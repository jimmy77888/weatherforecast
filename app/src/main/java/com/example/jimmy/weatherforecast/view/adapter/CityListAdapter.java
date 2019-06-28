package com.example.jimmy.weatherforecast.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.jimmy.weatherforecast.R;
import com.example.jimmy.weatherforecast.databinding.CityItemBinding;
import com.example.jimmy.weatherforecast.model.City;

import java.util.List;

//TODO change to RecyclerView in next version
public class CityListAdapter extends BaseAdapter implements View.OnClickListener {

    private List<City> list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int cityID);
    }
    public CityListAdapter(List<City> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(List<City> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int pos) {
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertedView, ViewGroup viewGroup) {
        if (convertedView == null) {
            CityItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.city_item, viewGroup, false);
            convertedView = binding.getRoot();
            convertedView.setTag(new ViewHolder(binding, pos));
            convertedView.setOnClickListener(this);
        }
        else {
            ViewHolder viewHolder = (ViewHolder)convertedView.getTag();
            viewHolder.setCity(pos);
        }
        return convertedView;
    }

    class ViewHolder{
        public CityItemBinding binding;
        public int pos;
        public ViewHolder(CityItemBinding binding, int pos) {
            this.binding = binding;
            this.pos = pos;
            setCity(pos);
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public void setCity(int pos) {
            binding.setCity(list.get(pos));

        }
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        int pos = viewHolder.getPos();
        onItemClickListener.onItemClick((list.get(pos)).cityID);
    }
}
