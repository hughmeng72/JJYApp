package com.pekingopera.oa.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pekingopera.oa.R;

/**
 * Created by wayne on 9/24/2016.
 */
public class MainFragment extends Fragment {

    public enum ActionEnum {
        lock, unlock, inquiry, signIn, aboutMe, more
    }

    private ImageButton mFlowImageButton;
    private ImageButton mDocImageButton;
    private ImageButton mCalendarImageButton;
    private ImageButton mFlowInquiryImageButton;
    private ImageButton mFlow2InquiryImageButton;
    private ImageButton mDocInquiryImageButton;
    private ImageButton mNoticeImageButton;
    private ImageButton mMailImageButton;
    private ImageButton mSetupImageButton;

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, parent, false);

        mFlowImageButton = (ImageButton) v.findViewById(R.id.main_flowImageButton);
        mDocImageButton = (ImageButton) v.findViewById(R.id.main_docImageButton);
        mCalendarImageButton = (ImageButton) v.findViewById(R.id.main_calendarImageButton);
        mFlowInquiryImageButton = (ImageButton) v.findViewById(R.id.main_flowInquiryImageButton);
        mFlow2InquiryImageButton = (ImageButton) v.findViewById(R.id.main_flow2InquiryImageButton);
        mDocInquiryImageButton = (ImageButton) v.findViewById(R.id.main_docInquiryImageButton);
        mNoticeImageButton = (ImageButton) v.findViewById(R.id.main_noticeImageButton);
        mMailImageButton = (ImageButton) v.findViewById(R.id.main_mailImageButton);
        mSetupImageButton = (ImageButton) v.findViewById(R.id.main_setupImageButton);

        View.OnClickListener inActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.inAction(v);
            }
        };

        mNoticeImageButton.setOnClickListener(inActionListener);

        mFlowImageButton.setOnClickListener(inActionListener);
        mDocImageButton.setOnClickListener(inActionListener);
        mCalendarImageButton.setOnClickListener(inActionListener);
        mFlowInquiryImageButton.setOnClickListener(inActionListener);
        mFlow2InquiryImageButton.setOnClickListener(inActionListener);
        mDocInquiryImageButton.setOnClickListener(inActionListener);
        mMailImageButton.setOnClickListener(inActionListener);
        mSetupImageButton.setOnClickListener(inActionListener);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_form_request:
                Intent intent = new Intent(getActivity(), GeneralFormRequestActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void inAction(View v);
    }

}