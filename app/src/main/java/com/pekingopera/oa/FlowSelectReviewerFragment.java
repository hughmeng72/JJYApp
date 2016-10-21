package com.pekingopera.oa;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.pekingopera.oa.model.Employee;

import java.io.Serializable;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wayne on 10/7/2016.
 */

public class FlowSelectReviewerFragment extends DialogFragment {
    private static final String TAG = "fsReviewerFragment";
    private static final String ARG_LIST_EMPLOYEE = "id";
    public static final String EXTRA_RESULT = "com.pekingopera.oa.reviewer";

    private RecyclerView mReviwerRecyclerView;
    private ReviewerAdapter mReviwerAdapter;

    private List<Employee> mEmployees;

    public static FlowSelectReviewerFragment newInstance(List<Employee> employees) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_EMPLOYEE, (Serializable) employees);

        FlowSelectReviewerFragment fragment = new FlowSelectReviewerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEmployees = (List<Employee>) getArguments().getSerializable(ARG_LIST_EMPLOYEE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycler_list, null);

        mReviwerRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_recycler_view);
        mReviwerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("选择分管领导")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean found = false;
                        for(Employee e : mEmployees) {
                            if (e.isSelected()) {
                                found = true;
                                sendResult(RESULT_OK, e.getId());

                                break;
                            }
                        }

                        if (!found) {
                            Toast.makeText(getActivity(), "请选择分管领导", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }

    private void updateUI() {
        if (mEmployees == null || mReviwerRecyclerView == null) {
            return;
        }

        if (mReviwerAdapter == null) {
            mReviwerAdapter = new ReviewerAdapter(mEmployees);
            mReviwerRecyclerView.setAdapter(mReviwerAdapter);
        } else {
            mReviwerAdapter.notifyDataSetChanged();
        }
    }

    private void sendResult(int resultCode, int reviewerId) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, reviewerId);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class ReviewerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Employee mEmployee;

        private TextView mReviewerNameTextView;
        private CheckBox mSelectCheckBox;

        public ReviewerHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mReviewerNameTextView = (TextView) itemView.findViewById(R.id.item_employee_name);
            mSelectCheckBox = (CheckBox) itemView.findViewById(R.id.item_employee_select);
            mSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmployee.setSelected(mSelectCheckBox.isChecked());
                }
            });
        }

        public void bindItemView(Employee employee) {
            mEmployee = employee;

            mReviewerNameTextView.setText(mEmployee.getRealName());
            mSelectCheckBox.setChecked(mEmployee.isSelected());
        }

        @Override
        public void onClick(View v) {
            mSelectCheckBox.setChecked(!mSelectCheckBox.isChecked());
            mEmployee.setSelected(mSelectCheckBox.isChecked());
        }
    }

    private class ReviewerAdapter extends RecyclerView.Adapter<ReviewerHolder> {
        private List<Employee> mEmployees1;

        public ReviewerAdapter(List<Employee> employees) {
            mEmployees1 = employees;
        }

        @Override
        public ReviewerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_select_reviewer, parent, false);

            return new ReviewerHolder(view);
        }

        @Override
        public void onBindViewHolder(ReviewerHolder holder, int position) {
            Employee employee = mEmployees1.get(position);
            holder.bindItemView(employee);
        }

        @Override
        public int getItemCount() {
            return mEmployees1.size();
        }
    }
}

