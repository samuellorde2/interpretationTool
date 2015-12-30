package com.lzh.interpretationtool.activity;

import com.lzh.interpretationtool.R;


import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.Button;

public class MainActivity extends Activity{

	
     String LOG="MainActivity";
     Button EtoC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "Ìø×ª½øÈë2");
        setContentView(R.layout.activity_main);
        EtoC=(Button) findViewById(R.id.EtoC);
        EtoC.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,Functionchoice.class);
				startActivity(intent);
			}
		});
    }

}
