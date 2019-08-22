package com.zoomtic.tamilswipe.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoomtic.tamilswipe.R;
import com.ybs.countrypicker.Country;

import java.util.List;

public class CountryListAdapter extends BaseAdapter{

    // Declare Variables
    LayoutInflater mInflater;
    private List<Country> mAllCountryList;
    private List<String> mSelectedCountryList;
    private Context mContext;

    public CountryListAdapter(Context context, List<String> selectedCountryList, List<Country> allCountryList) {
        this.mSelectedCountryList = selectedCountryList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mAllCountryList = allCountryList;
    }

    public class ViewHolder {
        TextView countryNameTextView;
        ImageView flagImageView;
        ImageView checkedImageButton;
    }

    @Override
    public int getCount() {
        return mAllCountryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllCountryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_country, null);
            holder.flagImageView = view.findViewById(R.id.imageview_flag);
            holder.countryNameTextView = view.findViewById(R.id.textview_country);
            holder.checkedImageButton = view.findViewById(R.id.imageButton_checked);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Country country = mAllCountryList.get(position);
        holder.countryNameTextView.setText(country.getName());
        holder.flagImageView.setImageResource(country.getFlag());
        if(mSelectedCountryList.contains(country.getCode())){
            holder.checkedImageButton.setImageResource(R.drawable.item_selected);
        }else{
            holder.checkedImageButton.setImageResource(R.drawable.item_unselected);
        }

        return view;
    }
}
