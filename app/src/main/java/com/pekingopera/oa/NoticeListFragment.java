package com.pekingopera.oa;

import android.os.AsyncTask;
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
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.Notice;
import com.pekingopera.oa.model.ResponseResult;
import com.pekingopera.oa.model.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.List;

public class NoticeListFragment extends Fragment {
    private static final String TAG = "NoticeListFragment";

    private RecyclerView mRecyclerView;
    private NoticeAdapter mAdapter;

    private List<Notice> mNotices = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.notice_recycler_view);
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

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        updateUI();
//    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new NoticeAdapter(mNotices);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class NoticeHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Notice mNotice;

        private TextView mTitleTextView;
        private TextView mTypeTextView;
        private TextView mDateTextView;

        public NoticeHoder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.item_notice_title);
            mTypeTextView = (TextView) itemView.findViewById(R.id.item_notice_type);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_notice_time);
        }

        public void bindNotice(Notice notice) {
            mNotice = notice;

            mTitleTextView.setText(mNotice.getTitle());
            mTypeTextView.setText(mNotice.getTypeName());
            mDateTextView.setText(mNotice.getAddTime());
        }

        @Override
        public void onClick(View v) {
//            startActivity(CrimePagerActivity.newIntent(getActivity(), mNotice.getId()));
            Toast.makeText(getActivity(), mNotice.getTitle() + " clicked.", Toast.LENGTH_SHORT).show();
        }
    }

    private class NoticeAdapter extends RecyclerView.Adapter<NoticeHoder> {

        private List<Notice> mNotices;

        public NoticeAdapter(List<Notice> notices) {
            mNotices = notices;
        }

        @Override
        public NoticeHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_notice, parent, false);

            return new NoticeHoder(view);
        }

        @Override
        public void onBindViewHolder(NoticeHoder holder, int position) {
            Notice notice = mNotices.get(position);
            holder.bindNotice(notice);
        }

        @Override
        public int getItemCount() {
            return mNotices.size();
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

            ResponseResult<Notice> responseResult;

            try {
                GsonBuilder gson = new GsonBuilder();
                Type resultType = new TypeToken<ResponseResult<Notice>>() {}.getType();

                responseResult = gson.create().fromJson(result, resultType);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (responseResult == null || responseResult.getError() == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG);
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

            mNotices = responseResult.getList();

            if (mNotices == null) {
                Toast toast = Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG);
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
            SoapObject request = new SoapObject(Utils.getWsNamespace(), Utils.getWsMethodOfNoticeList());

            request.addProperty(Utils.newPropertyInstance("token", token, String.class));

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
                androidHttpTransport.call(Utils.getWsSoapAction() + Utils.getWsMethodOfNoticeList(), envelope);

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
