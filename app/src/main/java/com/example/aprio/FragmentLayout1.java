package com.example.aprio;

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


}