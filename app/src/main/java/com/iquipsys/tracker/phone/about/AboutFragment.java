package com.iquipsys.tracker.phone.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iquipsys.tracker.phone.R;


public class AboutFragment extends Fragment {

    private TextView _appVersionTextView;

    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        _appVersionTextView =  (TextView) view.findViewById(R.id.appVersionTextView);

        String version = "1.0"; // default value
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getApplication().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        _appVersionTextView.setText(String.format(getResources().getString(R.string.title_product), version));

        return view;
    }

}
