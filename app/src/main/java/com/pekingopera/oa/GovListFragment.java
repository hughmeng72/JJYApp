package com.pekingopera.oa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Gov;
import com.pekingopera.oa.model.ResponseResults;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wayne on 10/7/2016.
 */
public class GovListFragment extends Fragment {
    private static final String TAG = "GovListFragment";

    private RecyclerView mRecyclerView;
    private GovAdapter mAdapter;

    private List<Gov> mGovs = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (Utils.isNetworkConnected(getActivity())) {
            LoadTask task = new LoadTask();
            task.execute(User.get().getToken());
        } else {
            Toast.makeText(getActivity(), R.string.prompt_internet_connection_broken, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    private void updateUI() {
        if (mGovs == null || mRecyclerView == null) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new GovAdapter(mGovs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class GovHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Gov mGov;

        private TextView mGovNameTextView;
        private TextView mCreatorTextView;
        private TextView mCategoryTextView;
        private TextView mDateTextView;

        public GovHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mGovNameTextView = (TextView) itemView.findViewById(R.id.item_gov_name);
            mCreatorTextView = (TextView) itemView.findViewById(R.id.item_gov_creator_step_name);
            mCategoryTextView = (TextView) itemView.findViewById(R.id.item_gov_category_status);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_gov_time);
        }

        public void bindItemView(Gov gov) {
            mGov = gov;

            mGovNameTextView.setText(mGov.getFlowName());
            mDateTextView.setText(mGov.getCreateTime());

            if (mGov.getCreatorId() == User.get().getUserId()) {
                mCreatorTextView.setText(mGov.getCurrentStepName());
                mCategoryTextView.setText(mGov.getStatusDesc());
            }
            else {
                mCreatorTextView.setText(mGov.getCreator());
                mCategoryTextView.setText(mGov.getModelName());
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = GovItemActivity.newIntent(getActivity(), mGov.getId());
            startActivity(i);
        }
    }

    private class GovAdapter extends RecyclerView.Adapter<GovHolder> {
        private List<Gov> mGovs;

        public GovAdapter(List<Gov> govs) {
            mGovs = govs;
        }

        @Override
        public GovHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_gov, parent, false);

            return new GovHolder(view);
        }

        @Override
        public void onBindViewHolder(GovHolder holder, int position) {
            Gov gov = mGovs.get(position);
            holder.bindItemView(gov);
        }

        @Override
        public int getItemCount() {
            return mGovs.size();
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
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfGovList());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfGovList(), envelope);

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
                Type resultType = new TypeToken<ResponseResults<Gov>>() {
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

            mGovs = responseResults.getList();
            PagerItemLab.get(getActivity()).setItems(mGovs);

            if (mGovs == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.prompt_system_error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

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
}
