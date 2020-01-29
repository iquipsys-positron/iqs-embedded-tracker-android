package com.iquipsys.tracker.phone.organizations;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

public class OrganizationsAdapter extends RecyclerView.Adapter<OrganizationsAdapter.ViewHolder> {

    public interface OrganizationClickListener {
        void onClick(Uri organizationUri);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView _textView;
        private long _rowID;

        public ViewHolder(View itemView) {
            super(itemView);
            _textView = (TextView) itemView.findViewById(android.R.id.text1);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(Organization.buildOrganizationUri(_rowID));
                        }
                    }
            );
        }

        public void set_rowID(long _rowID) {
            this._rowID = _rowID;
        }
    }

    private Cursor cursor = null;
    private final OrganizationClickListener clickListener;

    public OrganizationsAdapter(OrganizationClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.set_rowID(cursor.getLong(cursor.getColumnIndex(Organization._ID)));
        holder._textView.setText(cursor.getString(cursor.getColumnIndex(Organization.COLUMN_NAME)));
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
