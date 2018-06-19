/*
 * Copyright 2016-2017 Cisco Systems Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ciscospark.androidsdk.internal;

import javax.inject.Inject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import com.cisco.spark.android.callcontrol.CallControlService;
import com.github.benoitdion.ln.Ln;
import com.ciscospark.androidsdk.phone.internal.RotationHandler;

/**
 * Created by qimdeng on 3/1/18.
 */
public class AcquirePermissionActivity extends Activity {
	public static final String PERMISSION_TYPE = "permission_type";
	public static final String PERMISSION_SCREEN_SHOT = "permission_screen_shot";
	public static final String PERMISSION_CAMERA_MIC = "permission_camera_mic";
	public static final String CALL_DIRECTION = "call_direction";
	public static final String CALL_KEY = "call_key";
	public static final String CALL_STRING = "call_string";
	public static final String CALL_DATA = "call_data";
	protected static final int REQUEST_CAMERA_MIC = 0;
	protected static final int REQUEST_MEDIA_PROJECTION = 1;

	@Inject
	CallControlService _callControlService;

	private Bundle _callData;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		String type = getIntent().getStringExtra(PERMISSION_TYPE);
		if (PERMISSION_SCREEN_SHOT.equals(type)) {
			Ln.d("request PERMISSION_SCREEN_SHOT");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
				startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
			} else {
				Ln.e("Android version is lower than LOLLIPOP");
				finish();
			}
		}else if (PERMISSION_CAMERA_MIC.equals(type)){
			Ln.d("request PERMISSION_CAMERA_MIC");
			_callData = getIntent().getBundleExtra(CALL_DATA);
			int permissionCheck_camera = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA);
			int permissionCheck_mic = ContextCompat.checkSelfPermission(this,
				Manifest.permission.RECORD_AUDIO);
			String[] permissions = null;
			if (permissionCheck_camera != PackageManager.PERMISSION_GRANTED && permissionCheck_mic != PackageManager.PERMISSION_GRANTED) {
				permissions = new String[]{
					Manifest.permission.CAMERA,
					Manifest.permission.RECORD_AUDIO
				};
			}else if (permissionCheck_camera != PackageManager.PERMISSION_GRANTED){
				permissions = new String[]{
					Manifest.permission.CAMERA
				};
			}else if (permissionCheck_mic != PackageManager.PERMISSION_GRANTED){
				permissions = new String[]{
					Manifest.permission.RECORD_AUDIO
				};
			}
			if (permissions != null) {
				ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_MIC);
			}else{
				Ln.i("Do not need request permission, and make call directly");
				RotationHandler.makeCall(_callData, true);
				finish();
			}
		}else{
			finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_MEDIA_PROJECTION) {
			if (resultCode == Activity.RESULT_OK) {
				RotationHandler.setScreenshotPermission((Intent) data.clone());
			} else {
				Ln.i("User cancelled screen request");
				RotationHandler.setScreenshotPermission(null);
			}
		}
		finish();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_CAMERA_MIC){
			boolean result_ok = true;
			for (int result : grantResults){
				if (result != PackageManager.PERMISSION_GRANTED){
					result_ok = false;
					break;
				}
			}
			Ln.d("onRequestPermissionsResult: " + result_ok);
			RotationHandler.makeCall(_callData, result_ok);
		}
		finish();
	}
}