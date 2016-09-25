package com.pekingopera.oa;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LoginFragment extends Fragment {
    private final String TAG = "LoginFragment";

    public static final String PREF_REMEMBER_ME = "RememberMe";
    public static final String PREF_USERNAME = "UserName";
    public static final String PREF_PASSWORD = "Password";

    private EditText mUserNameEditText;
    private EditText mPasswordTextEdit;
    private Button mLoginButton;
    private CheckBox mRememberMeCheckBox;

    private Gson gson = new Gson();

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mRememberMeCheckBox = (CheckBox) v.findViewById(R.id.fragment_login_rememberMeCheckBox);

        mUserNameEditText = (EditText) v.findViewById(R.id.fragment_login_userNameEditText);
        mPasswordTextEdit = (EditText) v.findViewById(R.id.fragment_login_passwordEditText);

        mLoginButton = (Button) v.findViewById(R.id.fragment_login_loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        rememberMe();

        return v;
    }

    private void login() {

        String userName = mUserNameEditText.getText().toString();
        String password = mPasswordTextEdit.getText().toString();

        if (Utils.isNetworkConnected(getActivity())) {
            enableControls(false);

            LoginTask task = new LoginTask();
            task.execute(userName, password);
        } else {
            Toast toast = Toast.makeText(getActivity(), R.string.prompt_internet_connection_broken, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void enableControls(boolean enabled) {
        this.mLoginButton.setEnabled(enabled);
    }

    private void rememberMe() {
        boolean rememberMe = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_REMEMBER_ME,
                false);

        mRememberMeCheckBox.setChecked(rememberMe);

        if (rememberMe) {
            mUserNameEditText.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                    PREF_USERNAME, ""));

            mPasswordTextEdit.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                    PREF_PASSWORD, ""));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLoginValiated();
    }

    // AsynTask class to handle Login Web Service call as separate UI Thread
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground: " + params.toString());

            // Invoke web service GetTokenByUserNameAndPasswordResult
            return performLoginTask(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute: ");

            enableControls(true);

//            getActivity().setProgressBarIndeterminateVisibility(false);

            User my = null;

            if (null == result || result.isEmpty()) {
                Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_SHORT).show();

                return;
            }

            try {
                my = gson.fromJson(result, User.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (null == my || null == my.getError()) {
                Toast toast = Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (0 == my.getError().getResult()){
                Toast toast = Toast.makeText(getActivity(), my.getError().getErrorInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if (null == my.getToken() || my.getToken().isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            User.setUser(my);

            if (mRememberMeCheckBox.isChecked()) {
                String userName = mUserNameEditText.getText().toString();
                String password = mPasswordTextEdit.getText().toString();

                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putBoolean(PREF_REMEMBER_ME, true).putString(PREF_USERNAME, userName)
                        .putString(PREF_PASSWORD, password).commit();
            } else {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putBoolean(PREF_REMEMBER_ME, false).remove(PREF_USERNAME).remove(PREF_PASSWORD)
                        .commit();
            }

            Log.i(TAG, User.get().getToken());

            mListener.onLoginValiated();
        }

        @Override
        protected void onPreExecute() {
            // Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        // Method which invoke web method
        private String performLoginTask(String userName, String password) {
            // Create request
            SoapObject request = new SoapObject(Utils.getWsNamespace(), Utils.getWsMethodOfUserAuthentication());

            request.addProperty(Utils.newPropertyInstance("userName", userName, String.class));
            request.addProperty(Utils.newPropertyInstance("password", password, String.class));

            // Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            // Set output SOAP object
            envelope.setOutputSoapObject(request);

            // Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(Utils.getWsUrl());

            String responseJSON = null;

            try {
                // Invoke web service
                androidHttpTransport.call(Utils.getWsSoapAction() + Utils.getWsMethodOfUserAuthentication(), envelope);

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
