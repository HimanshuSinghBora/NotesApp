package com.example.notesapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notesapp.MainActivity;
import com.example.notesapp.R;

public class IntroPage extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_intro_page, container, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences Users= getActivity().getSharedPreferences("User", MODE_PRIVATE);
                Boolean check = Users.getBoolean("flag",false);
                FragmentManager manager=getParentFragmentManager();
                if(check){
                    FragmentTransaction tr2 = manager.beginTransaction();
                    tr2.replace(R.id.frameLayout, new NotesPage());
                    tr2.commit();
                }
                else{
                    FragmentTransaction tr2 = manager.beginTransaction();
                    tr2.replace(R.id.frameLayout, new LoginPage());
                    tr2.commit();
                }
            }
        },5000);

        return view;
    }
}