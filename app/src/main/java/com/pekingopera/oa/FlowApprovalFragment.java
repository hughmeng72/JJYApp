package com.pekingopera.oa;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wayne on 10/7/2016.
 */

public class FlowApprovalFragment extends DialogFragment {
    private static final String TAG = "FlowApprovalFragment";
    private static final String ARG_TITLE = "result";
    public static final String EXTRA_RESULT = "com.pekingopera.oa.result";


    private EditText mEditText;

    private String mTitle;


    public static FlowApprovalFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);

        FlowApprovalFragment fragment = new FlowApprovalFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTitle = getArguments().getString(ARG_TITLE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_approval_words, null);

        mEditText = (EditText) v.findViewById(R.id.dialog_approval_words);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mTitle)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(RESULT_OK, mEditText.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }

    private void sendResult(int resultCode, String words) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, words);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
