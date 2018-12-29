package com.kp.core.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author kaushal Prajapati (kaushal2406@gmail.com)
 *
 */
public class ConfirmDialog extends DialogFragment {
	CharSequence title, message;
	IDialogCallback dialogCallback;
	String yes="",no="";
	public ConfirmDialog(){

	}
	public ConfirmDialog(String yes, String no,CharSequence title, CharSequence message, IDialogCallback dialogCallback) {
		this.yes = yes;
		this.no = no;
		this.title = title;
		this.message = message;
		this.dialogCallback = dialogCallback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


	AlertDialog alertDialog =  new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message)
				.setNegativeButton(no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogCallback.onConfirmDialogNoClick();
					}
				}).setPositiveButton(yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogCallback.onConfirmDialogYesClick();
					}
				}).create();

		/*final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
		positiveButtonLL.gravity = Gravity.CENTER;
		positiveButton.setLayoutParams(positiveButtonLL);*/


		return  alertDialog;
	}

	public interface IDialogCallback {
		public void onConfirmDialogYesClick();
		public void onConfirmDialogNoClick();
	}
}
