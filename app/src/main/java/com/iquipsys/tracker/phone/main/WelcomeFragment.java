package com.iquipsys.tracker.phone.main;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iquipsys.tracker.phone.R;


public class WelcomeFragment extends Fragment {

    private TextView _appVersionTextView;

    public interface OnWelcomeFragmentListener {
        void onAddOrganization();
    }

    private OnWelcomeFragmentListener _listener;

    public WelcomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        _appVersionTextView =  (TextView) view.findViewById(R.id.appVersionTextView);

        String version = "1.0"; // default value
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getApplication().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        _appVersionTextView.setText(String.format(getResources().getString(R.string.title_product), version));

        Button addOrganizationButton = (Button) view.findViewById(R.id.welcomeAddOrganizationButton);
        addOrganizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null)
                    _listener.onAddOrganization();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnWelcomeFragmentListener) {
            _listener = (OnWelcomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWelcomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

}
