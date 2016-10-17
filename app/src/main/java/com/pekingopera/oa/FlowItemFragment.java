package com.pekingopera.oa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.update.UpdateHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pekingopera.oa.common.Employee;
import com.pekingopera.oa.common.Fab;
import com.pekingopera.oa.common.FileHelper;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Flow;
import com.pekingopera.oa.model.FlowDoc;
import com.pekingopera.oa.model.FlowStep;
import com.pekingopera.oa.model.Gov;
import com.pekingopera.oa.model.ResponseBase;
import com.pekingopera.oa.model.ResponseResults;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
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
    private static final String DIALOG_REVIEWER = "DialogReviewer";

    private static final int REQUEST_AGREED = 0;
    private static final int REQUEST_DISAGREED = 1;
    private static final int REQUEST_FINALIZED = 2;
    private static final int REQUEST_REVIEWER = 3;

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
    private List<Employee> mEmployees = null;

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
        } else {
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
            } else {
                mFlowStepAdapter.notifyDataSetChanged();
            }
        }

        if (mFlowAttachmentRecyclerView != null) {
            if (mFlowAttachmentAdapter == null) {
                mFlowAttachmentAdapter = new FlowAttachmentAdapter(mFlow.getAttachments());
                mFlowAttachmentRecyclerView.setAdapter(mFlowAttachmentAdapter);
            } else {
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

        switch (v.getTag().toString()) {
            case "agree":
                CheckReviwerTask task = new CheckReviwerTask();
                task.execute(User.get().getToken(), String.valueOf(mFlowId));
                break;
            case "disagree":
                showDisagreeDialog();
                break;
            case "finalize":
                showEndDialog();
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

        String words;
        switch (requestCode) {
            case REQUEST_AGREED:
                words = data.getStringExtra(FlowApprovalFragment.EXTRA_RESULT);
                mFlow.setReviewWords(words);

                SubmitTask task1 = new SubmitTask();
                task1.execute(User.get().getToken());

                break;
            case REQUEST_DISAGREED:
                words = data.getStringExtra(FlowApprovalFragment.EXTRA_RESULT);
                mFlow.setReviewWords(words);

                RejectTask task2 = new RejectTask();
                task2.execute(User.get().getToken());
                break;
            case REQUEST_FINALIZED:
                words = data.getStringExtra(FlowApprovalFragment.EXTRA_RESULT);
                mFlow.setReviewWords(words);

                FinalizeTask task3 = new FinalizeTask();
                task3.execute(User.get().getToken());

                break;
            case REQUEST_REVIEWER:
                int reviewerId = data.getIntExtra(FlowSelectReviewerFragment.EXTRA_RESULT, -1);

                if (reviewerId != -1) {
                    UpdateReviewerTask task4 = new UpdateReviewerTask();
                    task4.execute(User.get().getToken(), String.valueOf(mFlowId), String.valueOf(reviewerId));
                }
        }
    }

    private void showReviewerDialog() {
        FlowSelectReviewerFragment dialog;
        dialog = FlowSelectReviewerFragment.newInstance(mEmployees);
        dialog.setTargetFragment(FlowItemFragment.this, REQUEST_REVIEWER);
        dialog.show(getFragmentManager(), DIALOG_REVIEWER);
    }

    private void showEndDialog() {
        FlowApprovalFragment dialog;
        dialog = FlowApprovalFragment.newInstance("审批结果（办结）");
        dialog.setTargetFragment(FlowItemFragment.this, REQUEST_FINALIZED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
    }

    private void showDisagreeDialog() {
        FlowApprovalFragment dialog;
        dialog = FlowApprovalFragment.newInstance("审批结果（不同意）");
        dialog.setTargetFragment(FlowItemFragment.this, REQUEST_DISAGREED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
    }

    private void showAgreeDialog() {
        FlowApprovalFragment dialog;
        dialog = FlowApprovalFragment.newInstance("审批结果（同意）");
        dialog.setTargetFragment(FlowItemFragment.this, REQUEST_AGREED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
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

    private class FlowAttachmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FlowDoc mFlowDoc;
        private TextView mFileNameTextView;

        private Future<File> downloading;

        public FlowAttachmentHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mFileNameTextView = (TextView) itemView.findViewById(R.id.item_flow_attachment_name);
        }

        public void bindItemView(FlowDoc flowDoc) {
            mFlowDoc = flowDoc;

            mFileNameTextView.setText(mFlowDoc.getFileName());
        }

        @Override
        public void onClick(View v) {
            if (mFlowDoc.getUri().isEmpty()) {
                Toast.makeText(getActivity(), "没有附件可以显示", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!FileHelper.isExternalStorageWritable()) {
                Toast.makeText(getActivity(), "设备没有用来存放文件的公用目录。", Toast.LENGTH_SHORT).show();
                return;
            }

            downloadFile();
        }

        private void downloadFile() {
            if (downloading != null && !downloading.isCancelled()) {
                resetDownload();
                return;
            }

            final ProgressDialog dlg = new ProgressDialog(getActivity());
            dlg.setTitle("正在下载...");
            dlg.setIndeterminate(false);
            dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dlg.show();

            File docDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            docDir.mkdir();
            if (!docDir.exists()) {
                Toast.makeText(getActivity(), "设备没有创建公共文档的权限。", Toast.LENGTH_SHORT).show();
                return;
            }
            File newFile = new File(docDir, mFlowDoc.getFileName());

            final String mimeType = FileHelper.getMineType(getActivity(), Uri.fromFile(newFile));
            if (mimeType == null || mimeType.isEmpty()) {
                Toast.makeText(getActivity(), "文件类型无法识别，不能浏览。", Toast.LENGTH_SHORT).show();
                return;
            }

            downloading = Ion.with(getActivity())
                    .load(mFlowDoc.getUri())
                    .progressDialog(dlg)
                    .setLogging(TAG, Log.DEBUG)
                    .write(newFile)
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            dlg.cancel();
                            resetDownload();

                            if (e != null) {
                                Toast.makeText(getActivity(), "下载出错，请重试。", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            vewiFile(result, mimeType);
                        }
                    });
        }

        private void vewiFile(File result, String mimeType) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(result);
            intent.setDataAndType(uri, mimeType);

            startActivity(intent);
        }

        private void resetDownload() {
            downloading.cancel();
            downloading = null;
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

            ResponseResults<Flow> responseResults;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResults<Flow>>() {
                }.getType();

                responseResults = gson.create().fromJson(result, resultType);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResults == null || responseResults.getError() == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (responseResults.getError().getResult() == 0) {
                Toast toast = Toast.makeText(getActivity(), responseResults.getError().getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            List<Flow> flows = responseResults.getList();

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
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfFlowRequest());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("id", mFlow.getId(), int.class));
            request.addProperty(Utils.newPropertyInstance("words", mFlow.getReviewWords(), String.class));
            request.addProperty(Utils.newPropertyInstance("depName", mFlow.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("docBody", mFlow.getDocBody(), String.class));
            request.addProperty(Utils.newPropertyInstance("currentDocPath", mFlow.getCurrentDocPath(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowFiles", mFlow.getFlowFiles(), String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfFlowRequest(), envelope);

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

            if (responseResult.getResult() == 0) {
                Toast toast = Toast.makeText(getActivity(), responseResult.getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            PagerItemLab.get(getActivity()).Remove(mFlow.getId());

            getActivity().finish();
        }
    }

    private class RejectTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0]);
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfFlowRejectRequest());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("id", mFlow.getId(), int.class));
            request.addProperty(Utils.newPropertyInstance("words", mFlow.getReviewWords(), String.class));
            request.addProperty(Utils.newPropertyInstance("depName", mFlow.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("docBody", mFlow.getDocBody(), String.class));
            request.addProperty(Utils.newPropertyInstance("currentDocPath", mFlow.getCurrentDocPath(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowFiles", mFlow.getFlowFiles(), String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfFlowRejectRequest(), envelope);

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

            if (responseResult.getResult() == 0) {
                Toast toast = Toast.makeText(getActivity(), responseResult.getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            PagerItemLab.get(getActivity()).Remove(mFlow.getId());

            getActivity().finish();
        }
    }

    private class FinalizeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0]);
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfFlowFinalizeRequest());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("id", mFlow.getId(), int.class));
            request.addProperty(Utils.newPropertyInstance("words", mFlow.getReviewWords(), String.class));
            request.addProperty(Utils.newPropertyInstance("depName", mFlow.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("docBody", mFlow.getDocBody(), String.class));
            request.addProperty(Utils.newPropertyInstance("currentDocPath", mFlow.getCurrentDocPath(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowFiles", mFlow.getFlowFiles(), String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfFlowFinalizeRequest(), envelope);

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

            if (responseResult.getResult() == 0) {
                Toast toast = Toast.makeText(getActivity(), responseResult.getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            PagerItemLab.get(getActivity()).Remove(mFlow.getId());

            getActivity().finish();
        }
    }

    // AsynTask class to handle Load Web Service call as separate UI Thread
    private class CheckReviwerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0], Integer.valueOf(params[1]));
        }

        // Method which invoke web method
        private String performLoadTask(String token, int flowId) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfMissedReviwer());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("flowId", flowId, int.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfMissedReviwer(), envelope);

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

            ResponseResults<Employee> responseResults;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResults<Employee>>() {}.getType();

                responseResults = gson.create().fromJson(result, resultType);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResults == null || responseResults.getError() == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (responseResults.getError().getResult() == 0){
                Toast toast = Toast.makeText(getActivity(), responseResults.getError().getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            mEmployees = responseResults.getList();

            if (mEmployees == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (mEmployees.size() == 0) {
                showAgreeDialog();
            }
            else {
                showReviewerDialog();
            }
        }

        @Override
        protected void onPreExecute() {
            // Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class UpdateReviewerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service
            return performLoadTask(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]));
        }

        // Method which invoke web method
        private String performLoadTask(String token, int flowId, int staffId) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfUpdateFlowReviewer());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("flowId", flowId, int.class));
            request.addProperty(Utils.newPropertyInstance("staffId", staffId, int.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfUpdateFlowReviewer(), envelope);

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

            if (responseResult.getResult() == 0) {
                Toast toast = Toast.makeText(getActivity(), responseResult.getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            showAgreeDialog();
        }
    }


}
