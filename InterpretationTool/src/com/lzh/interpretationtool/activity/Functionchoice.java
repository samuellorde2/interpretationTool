package com.lzh.interpretationtool.activity;


import com.lzh.interpretationtool.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;

public class Functionchoice extends Activity implements OnKeyListener{
	
	
   Button consecButton;
   Button simutaButton;
   Button practiseHistory;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);

	setContentView(R.layout.functionchoice);
	consecButton=(Button) findViewById(R.id.consecButton);
	consecButton.setOnClickListener(new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(Functionchoice.this,Consecutive.class);
			startActivity(intent);
			finish();
		}
	});
	
	
	simutaButton=(Button) findViewById(R.id.simutaButton);
	simutaButton.setOnClickListener(new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(Functionchoice.this,Consecutive.class);
			startActivity(intent);
			finish();
		}
	});
	
	practiseHistory=(Button) findViewById(R.id.practiseHistory);
	practiseHistory.setOnClickListener(new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(Functionchoice.this,Consecutive.class);
			startActivity(intent);
			finish();
		}
	});
	
	
}
@Override
public boolean onKey(View v, int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub

	return false;
}

@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
        finish();
	   System.exit(0);

		return super.onKeyDown(keyCode, event);
	}
}
