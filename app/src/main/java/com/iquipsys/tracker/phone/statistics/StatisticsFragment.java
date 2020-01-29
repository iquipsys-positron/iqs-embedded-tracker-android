package com.iquipsys.tracker.phone.statistics;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.iquipsys.tracker.phone.R;


public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Configure period spinner
        Spinner periodSpinner = (Spinner) view.findViewById(R.id.statisticsPeriodSpinner);
        String[] spinnerValues = {
            getResources().getString(R.string.option_statistics_day),
            getResources().getString(R.string.option_statistics_week),
            getResources().getString(R.string.option_statistics_month),
            getResources().getString(R.string.option_statistics_year),
            //getResources().getString(R.string.option_statistics_total)
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerValues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(spinnerAdapter);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPeriod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }

    private void selectPeriod(int period) {
        switch (period) {
            case 0:
                // Show daily statistics
                break;
            case 1:
                // Show weekly statistics
                break;
            case 2:
                // Show monthly statistics
                break;
            case 3:
                // Show yearly statistics
                break;
            case 4:
                // Show total statistics
                break;
        }
    }

}
