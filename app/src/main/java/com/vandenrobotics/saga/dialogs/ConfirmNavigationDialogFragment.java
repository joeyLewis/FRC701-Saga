package com.vandenrobotics.saga.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.vandenrobotics.saga.R;

/**
 * ConfirmNavigationDialogFragment.java
 * created by:      joeyLewis   on  8/12/15
 * last edited by:  joeyLewis   on  8/12/15
 * handles the confirmation of navigation to external website locations
 */
public class ConfirmNavigationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle bundle = getArguments();
        final String mURL = bundle.getString("URL");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_ConfirmNavigationTitle)
                .setMessage(mURL)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // open up the url www.vandenrobotics.com
                        Uri uri = Uri.parse("http://" + mURL);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
