package com.pekingopera.oa.fragment;

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
import com.pekingopera.oa.R;
import com.pekingopera.oa.activity.FlowItemActivity;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.SoapHelper;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Flow;
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
 * Created by wayne on 10/5/2016.
 */
public class ApprovalFlowListFragment extends Fragment {
    private static final String TAG = "aplFlowListFragment";

    private RecyclerView mRecyclerView;
    private FlowAdapter mAdapter;

    private List<Flow> mFlows = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (Utils.isNetworkConnected(getActivity())) {
            LoadTask task = new LoadTask();
            task.execute(User.get().getToken());
        }
        else {
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
        if (mFlows == null || mRecyclerView == null) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new FlowAdapter(mFlows);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class FlowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Flow mFlow;

        private TextView mFlowNameTextView;
        private TextView mCreatorTextView;
        private TextView mAmountTextView;
        private TextView mDateTextView;
        private TextView mModelNameTextView;

        public FlowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mFlowNameTextView = (TextView) itemView.findViewById(R.id.item_approval_flow_name);
            mModelNameTextView = (TextView) itemView.findViewById(R.id.item_approval_model_name);
            mCreatorTextView = (TextView) itemView.findViewById(R.id.item_approval_flow_creator);
            mAmountTextView = (TextView) itemView.findViewById(R.id.item_approval_flow_amount);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_approval_flow_time);
        }

        public void bindItemView(Flow flow) {
            mFlow = flow;

            mFlowNameTextView.setText(mFlow.getFlowName());
            mModelNameTextView.setText(mFlow.getModelName());
            mCreatorTextView.setText(mFlow.getCreator());
            if (mFlow.getAmount() == 0) {
                mAmountTextView.setText("");
            }
            else {
                mAmountTextView.setText(String.format("%.2f", mFlow.getAmount()));
            }
            mDateTextView.setText(mFlow.getCreateTime());
        }

        @Override
        public void onClick(View v) {
            Intent i = FlowItemActivity.newIntent(getActivity(), mFlow.getId());
            startActivity(i);
        }
    }


    private class FlowAdapter extends RecyclerView.Adapter<FlowHolder> {
        private List<Flow> mFlows;

        public FlowAdapter(List<Flow> flows) {
            mFlows = flows;
        }

        @Override
        public FlowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_approval_flow, parent, false);

            return new FlowHolder(view);
        }

        @Override
        public void onBindViewHolder(FlowHolder holder, int position) {
            Flow flow = mFlows.get(position);
            holder.bindItemView(flow);
        }

        @Override
        public int getItemCount() {
            return mFlows.size();
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
                Type resultType = new TypeToken<ResponseResults<Flow>>() {}.getType();

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

            mFlows = responseResults.getList();
            PagerItemLab.get(getActivity()).setItems(mFlows);

            if (mFlows == null) {
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

        // Method which invoke web method
        private String performLoadTask(String token) {
            // Create request
            SoapObject request = new SoapObject(SoapHelper.getWsNamespace(), SoapHelper.getWsMethodOfApprovalFlowList());

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
                androidHttpTransport.call(SoapHelper.getWsSoapAction() + SoapHelper.getWsMethodOfApprovalFlowList(), envelope);

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
