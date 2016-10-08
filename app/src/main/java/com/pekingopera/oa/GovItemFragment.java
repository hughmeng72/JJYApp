package com.pekingopera.oa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.pekingopera.oa.common.Fab;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Flow;
import com.pekingopera.oa.model.FlowDoc;
import com.pekingopera.oa.model.FlowStep;
import com.pekingopera.oa.model.Gov;
import com.pekingopera.oa.model.ResponseBase;
import com.pekingopera.oa.model.ResponseResult;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wayne on 10/5/2016.
 */
public class GovItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "GovItemFragment";
    private static final String ARG_GOV = "gov_id";
    private static final String DIALOG_APPROVAL = "DialogConfirm";

    private static final int REQUEST_AGREED = 0;
    private static final int REQUEST_DISAGREED = 1;
    private static final int REQUEST_FINALIZED = 2;

    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    private TextView mFlowNameTextView;
    private TextView mDepartmentTextView;
    private TextView mRequestorTextView;
    private TextView mRequestTimeTextView;
    private TextView mRemarkTextView;

    private RecyclerView mStepRecyclerView;
    private RecyclerView mAttachmentRecyclerView;

    private FlowStepAdapter mFlowStepAdapter;
    private AttachmentAdapter mAttachmentAdapter;
    private Fab mFab;

    private int mGovId;
    private Gov mGov;

    public static Fragment newInstance(int govId) {
        Bundle args = new Bundle();
        args.putInt(ARG_GOV, govId);

        GovItemFragment fragment = new GovItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGovId = getArguments().getInt(ARG_GOV);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gov, container, false);

        setupFab(v);

        mFlowNameTextView = (TextView) v.findViewById(R.id.gov_name);
        mDepartmentTextView = (TextView) v.findViewById(R.id.gov_department);
        mRequestorTextView = (TextView) v.findViewById(R.id.gov_requestor);
        mRequestTimeTextView = (TextView) v.findViewById(R.id.gov_time);
        mRemarkTextView = (TextView) v.findViewById(R.id.gov_remark);

        mStepRecyclerView = (RecyclerView) v.findViewById(R.id.gov_steps);
        mStepRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAttachmentRecyclerView = (RecyclerView) v.findViewById(R.id.gov_attachments);
        mAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (Utils.isNetworkConnected(getActivity())) {
            LoadTask task = new LoadTask();
            task.execute(User.get().getToken());
        }
        else {
            Toast.makeText(getActivity(), R.string.prompt_internet_connection_broken, Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void updateUI() {
        if (mGov == null) {
            return;
        }

        if (mGov.isApprovalAuthorized()) {
            mFab.setVisibility(View.VISIBLE);
        }

        mFlowNameTextView.setText(mGov.getFlowName());
        mDepartmentTextView.setText(mGov.getDepName());
        mRequestorTextView.setText(mGov.getCreator());
        mRequestTimeTextView.setText(mGov.getCreateTime());
        mRemarkTextView.setText(mGov.getRemark());

        if (mStepRecyclerView != null) {
            if (mFlowStepAdapter == null) {
                mFlowStepAdapter = new FlowStepAdapter(mGov.getSteps());
                mStepRecyclerView.setAdapter(mFlowStepAdapter);
            }
            else {
                mFlowStepAdapter.notifyDataSetChanged();
            }
        }

        if (mAttachmentRecyclerView != null) {
            if (mAttachmentAdapter == null) {
                mAttachmentAdapter = new AttachmentAdapter(mGov.getAttachments());
                mAttachmentRecyclerView.setAdapter(mAttachmentAdapter);
            }
            else {
                mAttachmentAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setupFab(View v) {

        mFab = (Fab) v.findViewById(R.id.fab);
        View sheetView = v.findViewById(R.id.fab_sheet);
        View overlay = v.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.theme_accent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(mFab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.theme_primary_dark2));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });

        // Set material sheet item click listeners
        v.findViewById(R.id.fab_sheet_item_gov_agree).setOnClickListener(this);
        v.findViewById(R.id.fab_sheet_item_gov_finalize).setOnClickListener(this);
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getActivity().getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        materialSheetFab.hideSheet();

        FlowApprovalFragment dialog;

        switch (v.getTag().toString()) {
            case "agree":
                dialog = FlowApprovalFragment.newInstance("同意");
                dialog.setTargetFragment(GovItemFragment.this, REQUEST_AGREED);
                dialog.show(getFragmentManager(), DIALOG_APPROVAL);
                break;
            case "finalize":
                dialog = FlowApprovalFragment.newInstance("完结");
                dialog.setTargetFragment(GovItemFragment.this, REQUEST_FINALIZED);
                dialog.show(getFragmentManager(), DIALOG_APPROVAL);
                break;
            default:
                Toast.makeText(getActivity(), v.getTag() + " Item pressed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String words = data.getStringExtra(FlowApprovalFragment.EXTRA_RESULT);

        if (requestCode == REQUEST_AGREED) {
            mGov.setReviewWords(words);

            SubmitTask task = new SubmitTask();
            task.execute(User.get().getToken());

            return;
        }

        if (requestCode == REQUEST_FINALIZED) {
            Toast.makeText(getActivity(), "哈哈，完结啦！" + words, Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private class StepHolder extends RecyclerView.ViewHolder {
        private FlowStep mStep;

        private TextView mStepNameTextView;
        private TextView mStepDescTextView;

        public StepHolder(View itemView) {
            super(itemView);

            mStepNameTextView = (TextView) itemView.findViewById(R.id.item_flow_step_name);
            mStepDescTextView = (TextView) itemView.findViewById(R.id.item_flow_step_desc);
        }

        public void bindItemView(FlowStep step) {
            mStep = step;

            mStepNameTextView.setText(mStep.getStepName());
            mStepDescTextView.setText(mStep.getDescription());
        }
    }


    private class FlowStepAdapter extends RecyclerView.Adapter<StepHolder> {
        private List<FlowStep> mSteps;

        public FlowStepAdapter(List<FlowStep> steps) {
            mSteps = steps;
        }

        @Override
        public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_flow_step, parent, false);

            return new StepHolder(view);
        }

        @Override
        public void onBindViewHolder(StepHolder holder, int position) {
            FlowStep flowStep = mSteps.get(position);
            holder.bindItemView(flowStep);
        }

        @Override
        public int getItemCount() {
            return mSteps.size();
        }
    }


    private class AttachmentHolder extends RecyclerView.ViewHolder {
        private FlowDoc mDoc;

        private TextView mFileNameTextView;

        public AttachmentHolder(View itemView) {
            super(itemView);

            mFileNameTextView = (TextView) itemView.findViewById(R.id.item_flow_attachment_name);
        }

        public void bindItemView(FlowDoc doc) {
            mDoc = doc;

            mFileNameTextView.setText(mDoc.getFileName());
        }
    }


    private class AttachmentAdapter extends RecyclerView.Adapter<AttachmentHolder> {
        private List<FlowDoc> mDocs;

        public AttachmentAdapter(List<FlowDoc> docs) {
            mDocs = docs;
        }

        @Override
        public AttachmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_flow_attachment, parent, false);

            return new AttachmentHolder(view);
        }

        @Override
        public void onBindViewHolder(AttachmentHolder holder, int position) {
            FlowDoc doc = mDocs.get(position);
            holder.bindItemView(doc);
        }

        @Override
        public int getItemCount() {
            return mDocs.size();
        }
    }


    // AsynTask class to handle Load Web Service call as separate UI Thread
    private class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0]);
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfGovDetail());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("govId", mGovId, int.class));

            // Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            // Set output SOAP object
            envelope.setOutputSoapObject(request);

            // Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SoapHelper.getWsUrl());

            String responseJSON = null;

            try {
                // Invoke web service
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfGovDetail(), envelope);

                // Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                responseJSON = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseJSON;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute: ");

//            getActivity().setProgressBarIndeterminateVisibility(false);

            if (result == null || result.isEmpty()) {
                Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_SHORT).show();

                return;
            }

            ResponseResult<Gov> responseResult;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResult<Gov>>() {}.getType();

                responseResult = gson.create().fromJson(result, resultType);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResult == null || responseResult.getError() == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (responseResult.getError().getResult() == 0){
                Toast toast = Toast.makeText(getActivity(), responseResult.getError().getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            List<Gov> govs = responseResult.getList();

            if (govs == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (govs.size() != 1) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            mGov = govs.get(0);

            updateUI();
        }

        @Override
        protected void onPreExecute() {
            // Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class SubmitTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0]);
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfGovRequest());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("id", mGov.getId(), int.class));
            request.addProperty(Utils.newPropertyInstance("words", mGov.getReviewWords(), String.class));
            request.addProperty(Utils.newPropertyInstance("depName", mGov.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("currentDocPath", mGov.getCurrentDocPath(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowFiles", mGov.getFlowFiles(), String.class));

            // Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.addMapping(SoapHelper.getWsNamespace(), "gov", Gov.class);

            // Set output SOAP object
            envelope.setOutputSoapObject(request);

            // Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SoapHelper.getWsUrl());

            String responseJSON = null;

            try {
                // Invoke web service
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfGovRequest(), envelope);

                // Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                responseJSON = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseJSON;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute: ");

            if (result == null || result.isEmpty()) {
                Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_SHORT).show();

                return;
            }

            ResponseBase responseResult;

            try {
                responseResult = new Gson().fromJson(result, ResponseBase.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResult == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (responseResult.getResult() == 0){
                Toast toast = Toast.makeText(getActivity(), responseResult.getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            PagerItemLab.get(getActivity()).Remove(mGov.getId());

            getActivity().finish();
        }
    }

}
