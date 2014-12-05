package com.cs465.rightthisway;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

public class HelpDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View view = inflater.inflate(R.layout.help, null);
	    builder.setView(view);
	    builder.setTitle("Intro Help");
	    // Add action buttons
	    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity

            }
	    });

	    
	    WebView webView = (WebView) view.findViewById(R.id.webView1);
        webView.loadUrl("file:///android_asset/index.html");
	    return builder.create();
	}
}
