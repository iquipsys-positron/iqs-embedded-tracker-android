package com.iquipsys.tracker.phone.organizations;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

public class OrganizationsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public interface OnOrganizationsListFragmentListener {
        void onAddOrganization();
        void onSelectOrganization(Uri organizationUri);
    }

    private OnOrganizationsListFragmentListener _listener;
    private ConstraintLayout _organizationsListLayout;
    private LinearLayout _organizationsEmptyLayout;

    private static final int ORGANIZATIONS_LOADER = 0;
    private OrganizationsAdapter organizationsAdapter;

    public OrganizationsListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizations_list, container, false);

        // Initialize list layout and controls

        _organizationsListLayout = (ConstraintLayout) view.findViewById(R.id.organizationsListLayout);
        _organizationsListLayout.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.organizationsRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext())
        );

        organizationsAdapter = new OrganizationsAdapter(
                new OrganizationsAdapter.OrganizationClickListener() {
                    @Override
                    public void onClick(Uri organizationUri) {
                        if (_listener != null)
                            _listener.onSelectOrganization(organizationUri);
                    }
                }
        );
        recyclerView.setAdapter(organizationsAdapter);
        recyclerView.addItemDecoration(new ItemDivider(getContext()));
        recyclerView.setHasFixedSize(true);

        FloatingActionButton addOrganizationFab = (FloatingActionButton) view.findViewById(R.id.addOrganizationFab);
        addOrganizationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrganization();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize empty layout and controls

        _organizationsEmptyLayout = (LinearLayout) view.findViewById(R.id.organizationsEmptyLayout);
        _organizationsEmptyLayout.setVisibility(View.VISIBLE);

        Button addOrganizationButton = (Button) view.findViewById(R.id.addOrganizationButton);
        addOrganizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrganization();
            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrganizationsListFragmentListener) {
            _listener = (OnOrganizationsListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrganizationsListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    private void addOrganization() {
        if (_listener != null)
            _listener.onAddOrganization();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ORGANIZATIONS_LOADER, null, this);
    }

    public void updateOrganizations() {
        organizationsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ORGANIZATIONS_LOADER:
                return new CursorLoader(getActivity(),
                        Organization.CONTENT_URI,
                        null, null, null,
                        Organization.COLUMN_NAME + " COLLATE NOCASE ASC"
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            _organizationsListLayout.setVisibility(View.VISIBLE);
            _organizationsEmptyLayout.setVisibility(View.GONE);
        } else {
            _organizationsListLayout.setVisibility(View.GONE);
            _organizationsEmptyLayout.setVisibility(View.VISIBLE);
        }

        organizationsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _organizationsListLayout.setVisibility(View.GONE);
        _organizationsEmptyLayout.setVisibility(View.VISIBLE);

        organizationsAdapter.swapCursor(null);
    }

}
