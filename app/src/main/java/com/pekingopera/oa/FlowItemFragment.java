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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.pekingopera.oa.common.Fab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Flow;
import com.pekingopera.oa.model.FlowDoc;
import com.pekingopera.oa.model.FlowStep;
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
public class FlowItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "afItemFragment";
    private static final String ARG_FLOW = "flow_id";
    private static final String DIALOG_APPROVAL = "DialogConfirm";

    private static final int REQUEST_AGREED = 0;
    private static final int REQUEST_DISAGREED = 1;
    private static final int REQUEST_FINALIZED = 2;

    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    private TextView mFlowNameTextView;
    private TextView mFlowNoTextView;
    private TextView mDepartmentTextView;
    private TextView mRequestorTextView;
    private TextView mRequestTimeTextView;
    private TextView mRemarkTextView;
    private TextView mAmountTextView;
    private LinearLayout mAmountLinearLayout;
    private LinearLayout mBudgetLinearLayout;

    private TextView mBudgetItemNameTextView;
    private TextView mBudgetProjectNameTextView;
    private TextView mBudgetAmountTotalTextView;
    private TextView mBudgetAmountLeftTextView;
    private TextView mBudgetAmountToBePaidProcurementTextView;
    private TextView mBudgetAmountPaidProcurementTextView;
    private TextView mBudgetAmountPaidToBeReimbursementTextView;
    private TextView mBudgetAmountPaidReimbursementTextView;

    private RecyclerView mFlowStepRecyclerView;
    private RecyclerView mFlowAttachmentRecyclerView;

    private FlowStepAdapter mFlowStepAdapter;
    private FlowAttachmentAdapter mFlowAttachmentAdapter;
    private Fab mFab;

    private int mFlowId;
    private Flow mFlow;

    public static Fragment newInstance(int flowId) {
        Bundle args = new Bundle();
        args.putInt(ARG_FLOW, flowId);

        FlowItemFragment fragment = new FlowItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFlowId = getArguments().getInt(ARG_FLOW);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_approval_flow, container, false);

        setupFab(v);

        mFlowNameTextView = (TextView) v.findViewById(R.id.approval_flow_name);
        mFlowNoTextView = (TextView) v.findViewById(R.id.approval_flow_no);
        mDepartmentTextView = (TextView) v.findViewById(R.id.approval_flow_department);
        mRequestorTextView = (TextView) v.findViewById(R.id.approval_flow_requestor);
        mRequestTimeTextView = (TextView) v.findViewById(R.id.approval_flow_time);
        mRemarkTextView = (TextView) v.findViewById(R.id.approval_flow_remark);
        mAmountTextView = (TextView) v.findViewById(R.id.approval_flow_amount);

        mAmountLinearLayout = (LinearLayout) v.findViewById(R.id.approval_flow_amount_container);
        mBudgetLinearLayout = (LinearLayout) v.findViewById(R.id.approval_flow_budget_container);

        mBudgetItemNameTextView = (TextView) v.findViewById(R.id.approval_flow_budget_item_name);
        mBudgetProjectNameTextView = (TextView) v.findViewById(R.id.approval_flow_budget_project_name);
        mBudgetAmountTotalTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_total);
        mBudgetAmountLeftTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_left);
        mBudgetAmountToBePaidProcurementTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_paying_procument);
        mBudgetAmountPaidProcurementTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_paid_procument);
        mBudgetAmountPaidToBeReimbursementTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_paying_reimbursement);
        mBudgetAmountPaidReimbursementTextView = (TextView) v.findViewById(R.id.approval_flow_budget_amount_paid_reimbursement);

        mFlowStepRecyclerView = (RecyclerView) v.findViewById(R.id.approval_flow_steps);
        mFlowStepRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFlowAttachmentRecyclerView = (RecyclerView) v.findViewById(R.id.approval_flow_attachments);
        mFlowAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        if (mFlow == null) {
            return;
        }

        if (mFlow.isApprovalAuthorized()) {
            mFab.setVisibility(View.VISIBLE);
        }

        mFlowNameTextView.setText(mFlow.getFlowName());
        mFlowNoTextView.setText(mFlow.getFlowNo());
        mDepartmentTextView.setText(mFlow.getDepName());
        mRequestorTextView.setText(mFlow.getCreator());
        mRequestTimeTextView.setText(mFlow.getCreateTime());
        mRemarkTextView.setText(mFlow.getRemark());

        if (mFlow.getAmount() != 0) {
            mAmountLinearLayout.setVisibility(View.VISIBLE);

            mAmountTextView.setText(String.format("%.2f", mFlow.getAmount()));
        }

        if (mFlow.isBudgetInvolved() && mFlow.isBudgetAuthorized()) {
            mBudgetLinearLayout.setVisibility(View.VISIBLE);

            mBudgetItemNameTextView.setText(mFlow.getItemName());
            mBudgetProjectNameTextView.setText(mFlow.getProjectName());
            mBudgetAmountTotalTextView.setText(String.format("%.2f", mFlow.getTotalAmount()));
            mBudgetAmountLeftTextView.setText(String.format("%1$.2f (%2$.0f%%)", mFlow.getAmountLeft(), 100 * mFlow.getAmountLeft() / mFlow.getTotalAmount()));
            mBudgetAmountToBePaidProcurementTextView.setText(String.format("%.2f", mFlow.getAmountToBePaidProcurement()));
            mBudgetAmountPaidProcurementTextView.setText(String.format("%.2f", mFlow.getAmountPaidProcurement()));
            mBudgetAmountPaidToBeReimbursementTextView.setText(String.format("%.2f", mFlow.getAmountToBePaidReimbursement()));
            mBudgetAmountPaidReimbursementTextView.setText(String.format("%.2f", mFlow.getAmountPaidReimbursement()));
        }

        if (mFlowStepRecyclerView != null) {
            if (mFlowStepAdapter == null) {
                mFlowStepAdapter = new FlowStepAdapter(mFlow.getSteps());
                mFlowStepRecyclerView.setAdapter(mFlowStepAdapter);
            }
            else {
                mFlowStepAdapter.notifyDataSetChanged();
            }
        }

        if (mFlowAttachmentRecyclerView != null) {
            if (mFlowAttachmentAdapter == null) {
                mFlowAttachmentAdapter = new FlowAttachmentAdapter(mFlow.getAttachments());
                mFlowAttachmentRecyclerView.setAdapter(mFlowAttachmentAdapter);
            }
            else {
                mFlowAttachmentAdapter.notifyDataSetChanged();
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
        v.findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(this);
        v.findViewById(R.id.fab_sheet_item_photo).setOnClickListener(this);
        v.findViewById(R.id.fab_sheet_item_note).setOnClickListener(this);
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
                dialog.setTargetFragment(FlowItemFragment.this, REQUEST_AGREED);
                dialog.show(getFragmentManager(), DIALOG_APPROVAL);
                break;
            case "disagree":
                dialog = FlowApprovalFragment.newInstance("不同意");
                dialog.setTargetFragment(FlowItemFragment.this, REQUEST_DISAGREED);
                dialog.show(getFragmentManager(), DIALOG_APPROVAL);
                break;
            case "finalize":
                dialog = FlowApprovalFragment.newInstance("完结");
                dialog.setTargetFragment(FlowItemFragment.this, REQUEST_FINALIZED);
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
            Toast.makeText(getActivity(), "同意啦！" + words, Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_DISAGREED) {
            Toast.makeText(getActivity(), "不同意:(" + words, Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_FINALIZED) {
            Toast.makeText(getActivity(), "哈哈，完结啦！" + words, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private class FlowStepHolder extends RecyclerView.ViewHolder {
        private FlowStep mFlowStep;

        private TextView mStepNameTextView;
        private TextView mStepDescTextView;

        public FlowStepHolder(View itemView) {
            super(itemView);

            mStepNameTextView = (TextView) itemView.findViewById(R.id.item_flow_step_name);
            mStepDescTextView = (TextView) itemView.findViewById(R.id.item_flow_step_desc);
        }

        public void bindItemView(FlowStep flowStep) {
            mFlowStep = flowStep;

            mStepNameTextView.setText(mFlowStep.getStepName());
            mStepDescTextView.setText(mFlowStep.getDescription());
        }
    }

    private class FlowStepAdapter extends RecyclerView.Adapter<FlowStepHolder> {
        private List<FlowStep> mFlowSteps;

        public FlowStepAdapter(List<FlowStep> flowSteps) {
            mFlowSteps = flowSteps;
        }

        @Override
        public FlowStepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_flow_step, parent, false);

            return new FlowStepHolder(view);
        }

        @Override
        public void onBindViewHolder(FlowStepHolder holder, int position) {
            FlowStep flowStep = mFlowSteps.get(position);
            holder.bindItemView(flowStep);
        }

        @Override
        public int getItemCount() {
            return mFlowSteps.size();
        }
    }

    private class FlowAttachmentHolder extends RecyclerView.ViewHolder {
        private FlowDoc mFlowDoc;

        private TextView mFileNameTextView;

        public FlowAttachmentHolder(View itemView) {
            super(itemView);

            mFileNameTextView = (TextView) itemView.findViewById(R.id.item_flow_attachment_name);
        }

        public void bindItemView(FlowDoc flowDoc) {
            mFlowDoc = flowDoc;

            mFileNameTextView.setText(mFlowDoc.getFileName());
        }
    }

    private class FlowAttachmentAdapter extends RecyclerView.Adapter<FlowAttachmentHolder> {
        private List<FlowDoc> mFlowDocs;

        public FlowAttachmentAdapter(List<FlowDoc> flowDocs) {
            mFlowDocs = flowDocs;
        }

        @Override
        public FlowAttachmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_flow_attachment, parent, false);

            return new FlowAttachmentHolder(view);
        }

        @Override
        public void onBindViewHolder(FlowAttachmentHolder holder, int position) {
            FlowDoc flowDoc = mFlowDocs.get(position);
            holder.bindItemView(flowDoc);
        }

        @Override
        public int getItemCount() {
            return mFlowDocs.size();
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

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute: ");

//            getActivity().setProgressBarIndeterminateVisibility(false);

            if (result == null || result.isEmpty()) {
                Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_SHORT).show();

                return;
            }

            ResponseResult<Flow> responseResult;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResult<Flow>>() {}.getType();

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

            List<Flow> flows = responseResult.getList();

            if (flows == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (flows.size() != 1) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            mFlow = flows.get(0);

            updateUI();
        }

        @Override
        protected void onPreExecute() {
            // Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfFlowDetail());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("flowId", mFlowId, int.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfFlowDetail(), envelope);

                // Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                responseJSON = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseJSON;
        }
    }
}
