package com.pekingopera.oa;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by wayne on 9/24/2016.
 */
public class MainFragment extends Fragment {

    public enum ActionEnum {
        lock, unlock, inquiry, signIn, aboutMe, more
    }

    private ImageButton mLockImageButton;
    private ImageButton mUnlockImageButton;
    private ImageButton mInquiryImageButton;
    private ImageButton mSigninImageButton;
    private ImageButton mAboutMeImageButton;
    private ImageButton mMoreImageButton;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, parent, false);

        mLockImageButton = (ImageButton) v
                .findViewById(R.id.main_flowImageButton);
        mUnlockImageButton = (ImageButton) v
                .findViewById(R.id.main_docImageButton);
        mInquiryImageButton = (ImageButton) v
                .findViewById(R.id.main_flowInquiryImageButton);
        mSigninImageButton = (ImageButton) v
                .findViewById(R.id.main_flow2InquiryImageButton);
        mAboutMeImageButton = (ImageButton) v
                .findViewById(R.id.main_noticeImageButton);
        mMoreImageButton = (ImageButton) v
                .findViewById(R.id.main_mailImageButton);

        View.OnClickListener inActionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.inAction(v);
            }
        };

        mLockImageButton.setOnClickListener(inActionListener);
        mUnlockImageButton.setOnClickListener(inActionListener);
        mInquiryImageButton.setOnClickListener(inActionListener);
        mSigninImageButton.setOnClickListener(inActionListener);
        mAboutMeImageButton.setOnClickListener(inActionListener);
        mMoreImageButton.setOnClickListener(inActionListener);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
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