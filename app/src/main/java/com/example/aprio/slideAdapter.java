package com.example.aprio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class slideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public slideAdapter(Context context) {
        this.context = context;
    }

public String [] number_page={
        "1 of 3","2 of 3","3 of 3"
};


    public String [] information_page={
            "Personal informations","Address Informations","Location Map Feature"
    };


    @Override
    public int getCount() {
        return number_page.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slidelayout,container,false);
        TextView tv_inf=view.findViewById(R.id.tv_information);
        TextView tv_number=view.findViewById(R.id.tv_number);

        tv_inf.setText(information_page[position]);
        tv_number.setText(number_page[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
