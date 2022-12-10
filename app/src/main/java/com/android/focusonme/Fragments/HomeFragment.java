package com.android.focusonme.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.focusonme.Activity.InternetBlockActivity;
import com.android.focusonme.Activity.JobReminderActivity;
import com.android.focusonme.Activity.LimitActivity;
import com.android.focusonme.Activity.RestrictActivity;
import com.android.focusonme.Activity.WebBlockActivity;
import com.android.focusonme.R;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    Calendar c = Calendar.getInstance();
    int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
    String greeting="";
    ImageButton ib1,ib2,ib3,ib4,ib5;
    Context context;

    public HomeFragment(Context context) {
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView greet= view.findViewById(R.id.greet);
        greet.setText(getGreetings());
        ib1= view.findViewById(R.id.imageButton);
        ib2= view.findViewById(R.id.imageButton2);
        ib3= view.findViewById(R.id.imageButton3);
        ib4= view.findViewById(R.id.imageButton4);
        ib5= view.findViewById(R.id.imageButton5);
        TextView name= view.findViewById(R.id.name);
        SharedPreferences sh = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s1 = sh.getString("name", "");
        name.setText(s1);

        ib1.setOnClickListener(view1 -> {
            Intent intent=new Intent(getActivity(), RestrictActivity.class);
            startActivity(intent);
        });
        ib2.setOnClickListener(view12 -> {
            Intent intent=new Intent(getActivity(), LimitActivity.class);
            startActivity(intent);
        });
        ib3.setOnClickListener(view12 -> {
            Intent intent=new Intent(getActivity(), WebBlockActivity.class);
            startActivity(intent);
        });
        ib4.setOnClickListener(view12 -> {
            Intent intent=new Intent(getActivity(), InternetBlockActivity.class);
            startActivity(intent);
        });
        ib5.setOnClickListener(view12 -> {
            Intent intent=new Intent(getActivity(), JobReminderActivity.class);
            startActivity(intent);
        });

        return view;
    }

    public String getGreetings(){
        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting="Good Morning.";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting="Good Afternoon.";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting="Good Evening.";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting="Good Evening.";
        }
        return greeting;
    }
}