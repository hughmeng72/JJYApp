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
import android.text.Html;
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
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pekingopera.oa.model.Employee;
import com.pekingopera.oa.common.Fab;
import com.pekingopera.oa.common.FileHelper;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
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
public class GovItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "GovItemFragment";
    private static final String ARG_GOV = "gov_id";

    private static final String DIALOG_APPROVAL = "DialogConfirm";
    private static final String DIALOG_REVIEWER = "DialogReviewer";

    private static final int REQUEST_AGREED = 0;
    private static final int REQUEST_FINALIZED = 1;
    private static final int REQUEST_REVIEWER = 2;

    public static final String MODEL_NOTICE_LETTER = "普发件";

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
    private TextView mFinalizeActionTextView;
    private TextView mSubmitTextView;

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

            if (mGov.getModelName().equals(MODEL_NOTICE_LETTER)) {
                mSubmitTextView.setText("签收");
            }
            else {
                mSubmitTextView.setText("提交");
                mFinalizeActionTextView.setVisibility(View.VISIBLE);
            }
        }

        mFlowNameTextView.setText(mGov.getFlowName());
        mDepartmentTextView.setText(mGov.getDepName());
        mRequestorTextView.setText(mGov.getCreator());
        mRequestTimeTextView.setText(mGov.getCreateTime());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRemarkTextView.setText(Html.fromHtml(mGov.getRemark(), Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            mRemarkTextView.setText(Html.fromHtml(mGov.getRemark()));
        }

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
        mFinalizeActionTextView = (TextView) v.findViewById(R.id.fab_sheet_item_gov_finalize);
        mSubmitTextView = (TextView) v.findViewById(R.id.fab_sheet_item_gov_agree);

        mFinalizeActionTextView.setOnClickListener(this);
        mSubmitTextView.setOnClickListener(this);
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
                if (mGov.getModelName().equals(MODEL_NOTICE_LETTER)) {
                    showSignDialog();
                }
                else {
                    CheckReviwerTask task = new CheckReviwerTask();
                    task.execute(User.get().getToken(), String.valueOf(mGovId));
                }

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
                mGov.setReviewWords(words);

                SubmitTask task1 = new SubmitTask();
                task1.execute(User.get().getToken());

                break;
            case REQUEST_FINALIZED:
                words = data.getStringExtra(FlowApprovalFragment.EXTRA_RESULT);
                mGov.setReviewWords(words);

                FinalizeTask task2 = new FinalizeTask();
                task2.execute(User.get().getToken());

                break;
            case REQUEST_REVIEWER:
                int reviewerId = data.getIntExtra(FlowSelectReviewerFragment.EXTRA_RESULT, -1);

                if (reviewerId != -1) {
                    UpdateReviewerTask task3 = new UpdateReviewerTask();
                    task3.execute(User.get().getToken(), String.valueOf(mGovId), String.valueOf(reviewerId));
                }
        }
    }

    private void showReviewerDialog(List<Employee> employees) {
        FlowSelectReviewerFragment dialog;
        dialog = FlowSelectReviewerFragment.newInstance(employees);
        dialog.setTargetFragment(GovItemFragment.this, REQUEST_REVIEWER);
        dialog.show(getFragmentManager(), DIALOG_REVIEWER);
    }

    private void showSignDialog() {
        FlowApprovalFragment dialog;

        dialog = FlowApprovalFragment.newInstance("签收");
        dialog.setTargetFragment(GovItemFragment.this, REQUEST_AGREED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
    }

    private void showAgreeDialog() {
        FlowApprovalFragment dialog;
        dialog = FlowApprovalFragment.newInstance("审批结果（同意）");
        dialog.setTargetFragment(GovItemFragment.this, REQUEST_AGREED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
    }

    private void showEndDialog() {
        FlowApprovalFragment dialog;

        dialog = FlowApprovalFragment.newInstance("审批结果（办结）");
        dialog.setTargetFragment(GovItemFragment.this, REQUEST_FINALIZED);
        dialog.show(getFragmentManager(), DIALOG_APPROVAL);
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


    private class AttachmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FlowDoc mDoc;
        private TextView mFileNameTextView;

        private Future<File> downloading;

        public AttachmentHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mFileNameTextView = (TextView) itemView.findViewById(R.id.item_flow_attachment_name);
        }

        public void bindItemView(FlowDoc doc) {
            mDoc = doc;

            mFileNameTextView.setText(mDoc.getFileName());
        }

        @Override
        public void onClick(View v) {
            if (mDoc.getUri().isEmpty()) {
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
            File newFile = new File(docDir, mDoc.getFileName());

            final String mimeType = FileHelper.getMineType(getActivity(), Uri.fromFile(newFile));
            if (mimeType == null || mimeType.isEmpty()) {
                Toast.makeText(getActivity(), "文件类型无法识别，不能浏览。", Toast.LENGTH_SHORT).show();
                return;
            }

            downloading = Ion.with(getActivity())
                    .load(mDoc.getUri())
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

            ResponseResults<Gov> responseResults;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResults<Gov>>() {}.getType();

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

            List<Gov> govs = responseResults.getList();

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
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfGovFinalizeRequest());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("id", mGov.getId(), int.class));
            request.addProperty(Utils.newPropertyInstance("words", mGov.getReviewWords(), String.class));
            request.addProperty(Utils.newPropertyInstance("depName", mGov.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("currentDocPath", mGov.getCurrentDocPath(), String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfGovFinalizeRequest(), envelope);

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

    private class CheckReviwerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground...");

            // Invoke web service
            return performLoadTask(params[0], Integer.valueOf(params[1]));
        }

        // Method which invoke web method
        private String performLoadTask(String token, int flowId) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfMissedGovReviwer());

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfMissedGovReviwer(), envelope);

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

            List<Employee> employees = responseResults.getList();

            if (employees == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (employees.size() == 0) {
                showAgreeDialog();
            }
            else {
                showReviewerDialog(employees);
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
            Log.i(TAG, "doInBackground...");

            // Invoke web service
            return performLoadTask(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]));
        }

        // Method which invoke web method
        private String performLoadTask(String token, int flowId, int staffId) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfUpdateGovReviewer());

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfUpdateGovReviewer(), envelope);

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
