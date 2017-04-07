package com.pekingopera.oa.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pekingopera.oa.R;
import com.pekingopera.oa.activity.FlowItemActivity;
import com.pekingopera.oa.common.FileHelper;
import com.pekingopera.oa.common.IPager;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.common.Utils;
import com.pekingopera.oa.model.FlowDoc;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by wayne on 10/2/2016.
 */
public class WebPageFragment<T extends IPager> extends Fragment {
    private static final String ARG_URI = "page_url";
    private static final String ARG_INDEX = "page_index";

    private Uri mUri = null;
    private ProgressBar mProgressBar;
    private WebView mWebView;

    private RecyclerView mFlowAttachmentRecyclerView;
    private FlowAttachmentAdapter mFlowAttachmentAdapter;

    private int mCurrentIndex;
    private T mCurrentItem;

    public static Fragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        WebPageFragment fragment = new WebPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);

        WebPageFragment fragment = new WebPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);

        mCurrentIndex = getArguments().getInt(ARG_INDEX, -1);
        if (mCurrentIndex >= 0) {
            mCurrentItem = (T) PagerItemLab.get(getActivity()).getItems().get(mCurrentIndex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web_page, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_web_page_progress_bar);
        mProgressBar.setMax(100); // WebChromeClient reports in range 0-100

        mFlowAttachmentRecyclerView = (RecyclerView) v.findViewById(R.id.web_attachments);
        mFlowAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mCurrentItem.getAttachments() != null && mCurrentItem.getAttachments().size() > 0 && mCurrentItem != null) {
            LinearLayout attachmentContainer = (LinearLayout) v.findViewById(R.id.web_attachment_container);
            attachmentContainer.setVisibility(View.VISIBLE);

            if (mFlowAttachmentAdapter == null) {
                mFlowAttachmentAdapter = new FlowAttachmentAdapter(mCurrentItem.getAttachments());
                mFlowAttachmentRecyclerView.setAdapter(mFlowAttachmentAdapter);
            }
        }

        mWebView = (WebView) v.findViewById(R.id.fragment_web_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            public void onReceivedTitle(WebView webView, String title) {
//                AppCompatActivity activity = (AppCompatActivity) getActivity();
//                activity.getSupportActionBar().setSubtitle(title);
//                Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                gotoFlow(Uri.parse(url));

                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                gotoFlow(request.getUrl());

                return true;
            }

            private void gotoFlow(Uri uri) {
                if (uri != null) {
                    String modelName = uri.getQueryParameter("modelname");
                    int id = Integer.parseInt(uri.getQueryParameter("fid"));

//                    if (modelName.equals("报销申请")) {
//                        Intent i = FlowItemActivity.newIntent(getActivity(), id);
//                        startActivity(i);
//                    }

                    switch (modelName) {
                        case "合同审核申请":
                        case "用印申请":
                        case "公务出差":
                        case "公务接待":
                        case "公务用餐":
                        case "机票申请":
                        case "交款申请":
                        case "演出票领用申请":
                        case "用车申请":
                        case "预算调整":
                        case "专项报销申请":
                        case "报销申请":
                            Intent i = FlowItemActivity.newIntent(getActivity(), id);
                            startActivity(i);

                            break;
                    }
                }
            }
        });

        if (!(mCurrentItem == null || Utils.IsNullOrEmpty(mCurrentItem.getUri().toString()))) {
            mWebView.loadUrl(mCurrentItem.getUri().toString());
        } else {
            mWebView.loadUrl(mUri.toString());
        }

        return v;
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
}
