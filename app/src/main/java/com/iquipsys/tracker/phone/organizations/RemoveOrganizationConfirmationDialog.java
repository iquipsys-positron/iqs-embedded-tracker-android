package com.iquipsys.tracker.phone.organizations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.iquipsys.tracker.phone.R;

public class RemoveOrganizationConfirmationDialog extends DialogFragment {
    private DialogInterface.OnClickListener _listener;

    public RemoveOrganizationConfirmationDialog() {
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        _listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.confirm_organization_remove);

        builder.setPositiveButton(R.string.button_remove,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (_listener != null)
                            _listener.onClick(dialog, button);
                    }
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        return builder.create();
    }
}