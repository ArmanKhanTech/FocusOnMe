package com.android.focusonme.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.focusonme.Activity.MainActivity;
import com.android.focusonme.Activity.UsageActivity;
import com.android.focusonme.Adapter.AppAdapter;
import com.android.focusonme.Adapter.AppList;
import com.android.focusonme.R;

import java.util.ArrayList;

public class UsageFragment extends Fragment {

    Context context;
    LinearLayout ll;
    FrameLayout fl23;
    ArrayList<AppList> list;
    ListView usageList;
    loadApps la=new loadApps();
    AppAdapter adapter;

    public UsageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_usage, container, false);

        usageList=view.findViewById(R.id.usage_list);
        EditText editText = view.findViewById(R.id.search_bar123);
        context=getActivity();
        ll=view.findViewById(R.id.lv23);
        fl23=view.findViewById(R.id.fl23);
        fl23.setVisibility(View.GONE);

        usageList.setTextFilterEnabled(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        usageList.setOnItemClickListener((adapterView, view13, i, l) -> {
            AppList appList = adapter.getItem(i);
            String appName=appList.getName();
            String packageName= appList.getPackageName();
            Intent intent=new Intent(getActivity(), UsageActivity.class);
            intent.putExtra("PKG_NAME",packageName);
            intent.putExtra("APP_NAME",appName);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        la.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class loadApps extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            list=((MainActivity) requireActivity()).getInstalledApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);
            ll.setVisibility(View.GONE);
            fl23.setVisibility(View.VISIBLE);
            adapter=new AppAdapter(requireActivity(),list);
            usageList.setAdapter(adapter);

            AppList appList = adapter.getItem(1);

            cancel(true);
        }
    }
}