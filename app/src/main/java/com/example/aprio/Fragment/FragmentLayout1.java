package com.example.aprio.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aprio.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentLayout1 extends Fragment {

    @BindView(R.id.etSignupName)
    EditText etNameSignupSeller;
    @BindView(R.id.etSignupEmail)
    EditText etEmailSignupSeller;
    @BindView(R.id.etPasswordSignupSeller)
    EditText etPasswordSignupSeller;
    @BindView(R.id.etPhoneSignupSeller)
    EditText etPhoneSignupSeller;
    @BindView(R.id.btnPost)
    Button btnNextOne;
    private FragmentLayout1Listener listener;

    @OnClick(R.id.btnPost)
    public void onViewClicked() {
        String username = etNameSignupSeller.getText().toString().trim();
        String password = etPasswordSignupSeller.getText().toString().trim();
        String email = etEmailSignupSeller.getText().toString().trim();
        String phone = etPhoneSignupSeller.getText().toString().trim();

        listener.onInputFragmentLayout1Sent(username,password,email,phone);

    }

    public interface FragmentLayout1Listener {
        void onInputFragmentLayout1Sent(CharSequence username, CharSequence password, CharSequence email, CharSequence phone);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signlayout1, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentLayout1Listener) {
            listener = (FragmentLayout1Listener) context;
        } else {
            throw new RuntimeException(context.toString() + "Must ImplementAListene");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


}