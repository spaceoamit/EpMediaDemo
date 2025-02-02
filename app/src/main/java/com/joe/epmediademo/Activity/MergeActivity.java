package com.joe.epmediademo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.epmediademo.Application.MyApplication;
import com.joe.epmediademo.R;
import com.joe.epmediademo.Utils.UriUtils;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class MergeActivity extends AppCompatActivity implements View.OnClickListener {

	private static final int CHOOSE_FILE = 11;
	private TextView tv_add;
	private Button bt_add, bt_merge;
	private List<EpVideo> videoList;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		initView();
	}

	private void initView() {
		tv_add = (TextView) findViewById(R.id.tv_add);
		bt_add = (Button) findViewById(R.id.bt_add);
		bt_merge = (Button) findViewById(R.id.bt_merge);
		videoList = new ArrayList<>();
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setTitle("Processing");
		bt_add.setOnClickListener(this);
		bt_merge.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_add:
				chooseFile();
				break;
			case R.id.bt_merge:
				mergeVideo();
				break;
		}
	}

	/**
	 * 选择文件
	 */
	private void chooseFile() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, CHOOSE_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case CHOOSE_FILE:
				if (resultCode == RESULT_OK) {
					String videoUrl = UriUtils.getPath(MergeActivity.this, data.getData());
					tv_add.setText(tv_add.getText() + videoUrl + "\n");
					videoList.add(new EpVideo(videoUrl));
					break;
				}
		}
	}

	/**
	 * 合并视频
	 */
	private void mergeVideo() {
		if (videoList.size() > 1) {
			mProgressDialog.setProgress(0);
			mProgressDialog.show();
			final String outPath = MyApplication.getSavePath() + "outmerge.mp4";
			EpEditor.merge(videoList, new EpEditor.OutputOption(outPath), new OnEditorListener() {
				@Override
				public void onSuccess() {

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							Toast.makeText(MergeActivity.this, "Edit completed:"+outPath, Toast.LENGTH_SHORT).show();
							mProgressDialog.dismiss();

							Intent v = new Intent(Intent.ACTION_VIEW);
							v.setDataAndType(Uri.parse(outPath), "video/mp4");
							startActivity(v);
						}
					});

				}

				@Override
				public void onFailure() {
					Toast.makeText(MergeActivity.this, "Edit failed", Toast.LENGTH_SHORT).show();
					mProgressDialog.dismiss();
				}

				@Override
				public void onProgress(float v) {
					mProgressDialog.setProgress((int) (v * 100));
				}

			});
		} else {
			Toast.makeText(this, "Add at least two videos", Toast.LENGTH_SHORT).show();
		}
	}
}
