package com.example.matrisview;

import com.example.matrisview.MatrixView.MatrixCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String tag = "MainActivity";
	private MatrixView matrixView;
	private Button btn3,btn4,btn5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindowManager();
		init();
	}
	
	private void init(){
		btn3 = (Button) findViewById(R.id._3);
		btn3.setOnClickListener(l);
		btn4 = (Button) findViewById(R.id._4);
		btn4.setOnClickListener(l);
		btn5 = (Button) findViewById(R.id._5);
		btn5.setOnClickListener(l);
		matrixView = (MatrixView) findViewById(R.id.mMatrixView);
		matrixView.setOnFinishListener(new MatrixCallback() {

			@Override
			public void onFinish(byte[] password) {
				Toast.makeText(MainActivity.this, "Password: "+printBytes(password), Toast.LENGTH_SHORT).show();
				Log.e(tag, "Password: "+printBytes(password));
			}
		});
	}
	
	private String printBytes(byte[] b){
		StringBuffer str = new StringBuffer();
		int i=0;
		while(i<b.length){
			str.append(b[i]);
			if(i!=b.length-1){
				str.append(", ");
			}
			i++;
		}
		return str.toString();
	}
	
	private OnClickListener l = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id._3:
				matrixView.setSize(3);
				break;
			case R.id._4:
				matrixView.setSize(4);
				break;
			case R.id._5:
				matrixView.setSize(5);
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
