package com.pekingopera.oa.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.AsyncSemaphore;
import com.pekingopera.oa.R;
import com.pekingopera.oa.common.PictureUtil;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Flow;
import com.pekingopera.oa.model.ResponseBase;
import com.pekingopera.oa.model.ResponseResult;
import com.pekingopera.oa.model.ResponseResults;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wayne on 10/27/2016.
 */
public class FormRequestFragment extends Fragment {
    private static final String TAG = "FormRequestFragment";

    private static final String DIALOG_IMAGE = "image";

    private static final int ACTION_TAKE_PHOTO_1 = 0;
    private static final int ACTION_TAKE_PHOTO_2 = 1;
    private static final int ACTION_TAKE_PHOTO_3 = 2;

    private EditText mFormNameEditText;
    private EditText mRemarkEditText;
    private EditText mAmountEditText;
    private EditText mDocBodyEditText;

    private ImageButton mP1ImageButton;
    private ImageButton mP2ImageButton;
    private ImageButton mP3ImageButton;

    private Button mConfirmationButton;

    private RadioButton mCashRadioButton;
    private RadioButton mCheckRadioButton;
    private RadioButton mWireRadioButton;

    private Spinner mBudgetItemSpinner;
    private ArrayAdapter<String> mBudgetItemArrayAdapter;

    private List<String> mBugdgetItems;

    private Flow mFlow = new Flow();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_request, container, false);

        mFormNameEditText = (EditText) view.findViewById(R.id.form_request_formNameEditText);
        mRemarkEditText = (EditText) view.findViewById(R.id.form_request_remarkEditText);
        mAmountEditText = (EditText) view.findViewById(R.id.form_request_amountEditText);
        mDocBodyEditText = (EditText) view.findViewById(R.id.form_request_docBodyEditText);

        mP1ImageButton = (ImageButton) view.findViewById(R.id.form_request_p1ImageButton);
        mP2ImageButton = (ImageButton) view.findViewById(R.id.form_request_p2ImageButton);
        mP3ImageButton = (ImageButton) view.findViewById(R.id.form_request_p3ImageButton);

        mConfirmationButton = (Button)view.findViewById(R.id.form_request_confirmationButton);

        mCashRadioButton = (RadioButton) view.findViewById(R.id.form_request_cashRadioButton);
        mCheckRadioButton = (RadioButton) view.findViewById(R.id.form_request_checkRadioButton);
        mWireRadioButton = (RadioButton) view.findViewById(R.id.form_request_wireRadioButton);

        mCashRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckRadioButton.setChecked(false);
                mWireRadioButton.setChecked(false);
                mCashRadioButton.setChecked(true);
            }
        });

        mCheckRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCashRadioButton.setChecked(false);
                mCheckRadioButton.setChecked(true);
                mWireRadioButton.setChecked(false);
            }
        });

        mWireRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCashRadioButton.setChecked(false);
                mCheckRadioButton.setChecked(false);
                mWireRadioButton.setChecked(true);
            }
        });

        mBudgetItemSpinner = (Spinner) view.findViewById(R.id.form_request_budgetItemSpinner);

        mP1ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlow.getPhotoName1() == null || mFlow.getPhotoName1().isEmpty()) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_1);
                } else {
                    FragmentManager fm = getActivity().getFragmentManager();
                    ImageFragment.newInstance(mFlow.getPhoto1FilePath()).show(fm, DIALOG_IMAGE);
                }
            }
        });

        mP2ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlow.getPhotoName2() == null || mFlow.getPhotoName2().isEmpty()) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_2);
                } else {
                    FragmentManager fm = getActivity().getFragmentManager();
                    ImageFragment.newInstance(mFlow.getPhoto2FilePath()).show(fm, DIALOG_IMAGE);
                }
            }
        });

        mP3ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlow.getPhotoName3() == null || mFlow.getPhotoName3().isEmpty()) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_3);
                } else {
                    FragmentManager fm = getActivity().getFragmentManager();
                    ImageFragment.newInstance(mFlow.getPhoto3FilePath()).show(fm, DIALOG_IMAGE);
                }
            }
        });

        mConfirmationButton = (Button) view.findViewById(R.id.form_request_confirmationButton);
        mConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdate();
            }
        });

        if (Utils.isNetworkConnected(getActivity())) {
            LoadTask task = new LoadTask();
            task.execute(User.get().getToken());
        } else {
            Toast.makeText(getActivity(), R.string.prompt_internet_connection_broken, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void doUpdate() {

        if (!passedValidation()) {
            return;
        }

        mConfirmationButton.setEnabled(false);

        updateModel();

        UpdateTask task = new UpdateTask();
        task.execute(User.get().getToken());
    }

    private void updateModel() {
        mFlow.setFlowName(mFormNameEditText.getText().toString());
        mFlow.setRemark(mRemarkEditText.getText().toString());
        mFlow.setPaymentTerm(mCashRadioButton.isChecked() ? mCashRadioButton.getText().toString()
                : mCheckRadioButton.isChecked() ? mCheckRadioButton.getText().toString()
                : mWireRadioButton.getText().toString());
        mFlow.setAmount(Double.valueOf(mAmountEditText.getText().toString()));
        mFlow.setDocBody(mDocBodyEditText.getText().toString());

        if (!(mFlow.getPhotoName1() == null || mFlow.getPhotoName1().isEmpty())) {
            mFlow.setFlowFiles("~/Files/FlowFiles/Mobile/" + mFlow.getPhotoName1() + "|");
        }

        if (!(mFlow.getPhotoName2() == null || mFlow.getPhotoName2().isEmpty())) {
            mFlow.setFlowFiles(mFlow.getFlowFiles() + "~/Files/FlowFiles/Mobile/" + mFlow.getPhotoName2() + "|");
        }

        if (!(mFlow.getPhotoName3() == null || mFlow.getPhotoName3().isEmpty())) {
            mFlow.setFlowFiles(mFlow.getFlowFiles() + "~/Files/FlowFiles/Mobile/" + mFlow.getPhotoName3() + "|");
        }
    }

    private boolean passedValidation() {
        if (Utils.IsNullOrEmpty(mFormNameEditText.getText().toString())) {
            Toast.makeText(getActivity(), "请输入名称", Toast.LENGTH_SHORT).show();
            mFormNameEditText.requestFocus();
            return false;
        }

        if (Utils.IsNullOrEmpty(mRemarkEditText.getText().toString())) {
            Toast.makeText(getActivity(), "请输入摘要", Toast.LENGTH_SHORT).show();
            mRemarkEditText.requestFocus();
            return false;
        }

        if (Utils.IsNullOrEmpty(mAmountEditText.getText().toString())) {
            Toast.makeText(getActivity(), "请输入金额", Toast.LENGTH_SHORT).show();
            mAmountEditText.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        Log.d(TAG, "Got a picture: " + mCurrentPhotoPath);

        switch (requestCode) {
            case ACTION_TAKE_PHOTO_1:
                mFlow.setPhoto1FilePath(mCurrentPhotoPath);

                if (mCurrentPhotoPath != null) {
                    PictureUtil.showPic(mP1ImageButton, mCurrentPhotoPath);
                }
                break;
            case ACTION_TAKE_PHOTO_2:
                mFlow.setPhoto2FilePath(mCurrentPhotoPath);

                if (mCurrentPhotoPath != null) {
                    PictureUtil.showPic(mP2ImageButton, mCurrentPhotoPath);
                }
                break;
            case ACTION_TAKE_PHOTO_3:
                mFlow.setPhoto3FilePath(mCurrentPhotoPath);

                if (mCurrentPhotoPath != null) {
                    PictureUtil.showPic(mP3ImageButton, mCurrentPhotoPath);
                }
                break;
        }

        mCurrentPhotoPath = null;
    }

    private String mCurrentPhotoPath;

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setupPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();

            f = null;
            mCurrentPhotoPath = null;

            return;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setupPhotoFile() throws IOException {

        File f = PictureUtil.createImageFile(null);
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private class UpdateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (mFlow.getPhotoName1() != null && !mFlow.getPhotoName1().isEmpty()) {
                Log.i(TAG, "Uploading photo 1...");

                PictureUtil.uploadPhoto(mFlow.getPhotoName1(), mFlow.getPhoto1FilePath());

                Log.i(TAG, "Uploaded photo 1...");
            }

            if (mFlow.getPhotoName2() != null && !mFlow.getPhotoName2().isEmpty()) {
                Log.i(TAG, "Uploading photo 2...");

                PictureUtil.uploadPhoto(mFlow.getPhotoName2(), mFlow.getPhoto2FilePath());

                Log.i(TAG, "Uploaded photo 2...");
            }

            if (mFlow.getPhotoName3() != null && !mFlow.getPhotoName3().isEmpty()) {
                Log.i(TAG, "Uploading photo 3...");

                PictureUtil.uploadPhoto(mFlow.getPhotoName3(), mFlow.getPhoto3FilePath());

                Log.i(TAG, "Uploaded photo 3...");
            }

            // Invoke web service
            return performLoadTask(params[0]);
        }

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfSaveFlow());

            User user = User.get();

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("modelName", mFlow.getModelName(), String.class));
            request.addProperty(Utils.newPropertyInstance("id", 0, int.class));
            request.addProperty(Utils.newPropertyInstance("userId", user.getUserId(), int.class));
            request.addProperty(Utils.newPropertyInstance("creatorRealName", user.getRealName(), String.class));
            request.addProperty(Utils.newPropertyInstance("creatorDepName", user.getDepName(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowName", mFlow.getFlowName(), String.class));
            request.addProperty(Utils.newPropertyInstance("docBody", mFlow.getDocBody(), String.class));
            request.addProperty(Utils.newPropertyInstance("remark", mFlow.getRemark(), String.class));
            request.addProperty(Utils.newPropertyInstance("flowFiles", mFlow.getFlowFiles(), String.class));
            request.addProperty(Utils.newPropertyInstance("amount", String.valueOf(mFlow.getAmount()), String.class));
            request.addProperty(Utils.newPropertyInstance("projectId", user.getDepId(), int.class));
            request.addProperty(Utils.newPropertyInstance("budgetItemName", mFlow.getItemName(), String.class));
            request.addProperty(Utils.newPropertyInstance("paymentMethod", mFlow.getPaymentTerm(), String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfSaveFlow(), envelope);

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

            mConfirmationButton.setEnabled(true);

            if (result == null || result.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.prompt_system_error, Toast.LENGTH_SHORT).show();

                return;
            }

            ResponseBase responseResult;

            try {
                responseResult = new Gson().fromJson(result, ResponseBase.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResult == null || responseResult.getErrorInfo() == null) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.prompt_system_error, Toast.LENGTH_LONG);
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

            Toast.makeText(getActivity().getApplicationContext(), "申请单保存成功。", Toast.LENGTH_SHORT).show();

            getActivity().finish();
;        }

        @Override
        protected void onPreExecute() {
            // Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfBudgetList());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));
            request.addProperty(Utils.newPropertyInstance("depId", User.get().getDepId(), int.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfBudgetList(), envelope);

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

            ResponseResults<String> responseResults;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResults<String>>() {
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

            List<String> items = responseResults.getList();

            if (items == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            mBugdgetItems = items;

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

    private void updateUI() {
        mBudgetItemArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mBugdgetItems);
        mBudgetItemArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBudgetItemSpinner.setAdapter(mBudgetItemArrayAdapter);

        mBudgetItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemName = parent.getItemAtPosition(position).toString();
                mFlow.setItemName(itemName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
