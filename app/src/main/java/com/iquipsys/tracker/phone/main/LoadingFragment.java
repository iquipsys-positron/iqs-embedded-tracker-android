package com.iquipsys.tracker.phone.main;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iquipsys.tracker.phone.R;

public class LoadingFragment extends Fragment {

    private TextView _appVersionTextView;

    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        _appVersionTextView =  (TextView) view.findViewById(R.id.appVersionTextView);

        String version = "1.0"; // default value
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getApplication().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        _appVersionTextView.setText(String.format(getResources().getString(R.string.title_product), version));

        ProgressBar loadingProgress = (ProgressBar) view.findViewById(R.id.loadingProgress);
        loadingProgress.setIndeterminate(true);

        return view;
    }

}
