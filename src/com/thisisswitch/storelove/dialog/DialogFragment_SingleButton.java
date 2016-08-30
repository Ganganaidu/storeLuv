package com.thisisswitch.storelove.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.thisisswitch.storelove.R;

public class DialogFragment_SingleButton extends DialogFragment {


	public static DialogFragment_SingleButton newInstance(String string) {
		DialogFragment_SingleButton frag = new DialogFragment_SingleButton();
		Bundle args = new Bundle();
		args.putString("title", string);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater

		builder.setIcon(R.drawable.ic_launcher).setTitle(R.string.app_name).setMessage(title)
		.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});      
		return builder.create();
	}
}
