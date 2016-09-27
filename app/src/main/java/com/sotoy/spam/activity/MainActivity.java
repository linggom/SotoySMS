/*
   Copyright 2016 Narrative Nights Inc. All Rights Reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.sotoy.spam.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sotoy.spam.controller.CountVectorizer;
import com.sotoy.spam.R;
import com.sotoy.spam.controller.TextDetector;

public class MainActivity extends AppCompatActivity  {
	private static final String TAG = "DrawDigitActivity";


	private TextDetector mDetector = new TextDetector();
	private Button button;
	private EditText text;
	private TextView lbl;


	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button);
		text = (EditText) findViewById(R.id.editText);
		lbl = (TextView) findViewById(R.id.textView2);
		text.setText("Pelanggan Yth; No.Anda resmi terpilih sebagai pemenang ke 2 dari UNDIAN PT.M-TRONIK Pin Anda 25e477r silakan cek pin anda di; www.thr-mtronik.tk.\n");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lbl.setText("Your Text is : " + isSpam(text.getText().toString()));
			}
		});

		boolean text_setup = mDetector.setupTextDetector(this);

		if( !text_setup) {
			Log.i(TAG, "Detector Text setup failed");
			return;
		}
		Log.e("SPAM", "ispam = " + isSpam("Halo"));
	}

	private boolean isSpam(String text) {
		int[] features = CountVectorizer.getInstance(this).transform(text);
		return mDetector.detectText(features) == 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


}
