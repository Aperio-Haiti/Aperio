package com.example.aprio;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class FragmentLayout1 extends Fragment {

    private FragmentLayout1Listener listener;

    public interface FragmentLayout1Listener{
        void onInputFragmentLayout1Sent(CharSequence username,CharSequence password,CharSequence email);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.signlayout1, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentLayout1Listener){
            listener = (FragmentLayout1Listener)context;
        }else {
            throw new RuntimeException(context.toString() + "Must ImplementAListene");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


}