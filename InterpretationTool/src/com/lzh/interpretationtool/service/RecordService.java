package com.lzh.interpretationtool.service;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service {	
	 // 音频文件保存地址
	 private static String path;
	 private  String paths = path;
	  File saveFilePath;
	// static String fileName;
	 String LOG="RecordService";
	 // 录音文件播放
	 private MediaPlayer myPlayer;
	 String music;
	 // 录音
	 private MediaRecorder myRecorder;
	 private SharedPreferences sp;
	 private boolean isRecording=false;
	// private boolean isPlayRecording=false;
    Editor editor;
    //判断按键的类型：播放，占停等
    //int i;
	 
private Binder recordBinder = new RecordBinder();
		
	

	
	@Override	
	public IBinder onBind(Intent intent) {		
		return recordBinder;
	}	
@Override
public void onCreate() {
	// TODO Auto-generated method stub
	super.onCreate();

	myPlayer = new MediaPlayer();

	  // 从麦克风源进行录音
	  //初始化sharedPreference
	  sp =getSharedPreferences("RecordService",MODE_PRIVATE);


	  if (Environment.getExternalStorageState().equals(
			    Environment.MEDIA_MOUNTED)) {
			   try {
				//获取sdk卡路径
			    path = Environment.getExternalStorageDirectory()
			      .getCanonicalPath().toString()
			      + "/XIONGRECORDERS";
			    File files = new File(path);
			    if (!files.exists()) {
			    	//若不存在，创建文件夹
			     files.mkdir();
			    }
			    //listFile = files.list();
			   } catch (IOException e) {
			    e.printStackTrace();
			   }
	  }
	  
}


@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub

	return super.onStartCommand(intent, flags, startId);
	
}

public void startRecord()
{
	//Log.i(LOG, "1在这recording");
	  myRecorder = new MediaRecorder();
	
	isRecording=true;
    paths = path+ "/" +new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".amr";

    saveFilePath = new File(paths);  
	
	  myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
	  // 设置输出格式
	  myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
	  // 设置编码格式
	  myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    myRecorder.setOutputFile(saveFilePath.getAbsolutePath());  
  
    try {

		saveFilePath.createNewFile();

			  myRecorder.prepare();	        
	      // 开始录音 

	     myRecorder.start();

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    editor=sp.edit();
	editor.putString("MusicAddress",saveFilePath.getAbsolutePath());
	editor.commit();

				
	};
	
	
public void stopRecord(){
	 isRecording=false;
	music=sp.getString("MusicAddress", "");
	Log.i(LOG, "没忘记release"+music);
	 if (saveFilePath.exists() && saveFilePath != null) {				 

		    myRecorder.stop();
		  myRecorder.release();			 
		    // 判断是否保存 如果不保存则删除
			}
	 


}

public void playRecord() {
	 try {
	      myPlayer.reset();
	  	music=sp.getString("MusicAddress", "");
	      

	      myPlayer.setDataSource(music);
	      if (!myPlayer.isPlaying()) {

	       myPlayer.prepare();
	       myPlayer.start();
	      // isPlayRecording=true;
	      } else {
	       myPlayer.pause();
	       //isPlayRecording=false;
	      }
	     } catch (IOException e) {
	      e.printStackTrace();
	     }
}

public void stopPlayRecord() {
    if (myPlayer.isPlaying()) {
        myPlayer.stop();
       // myPlayer.release();
        
       }
	
}
@Override
public void onDestroy() {
 // 释放资源
	super.onDestroy();
	 if (myPlayer.isPlaying()) {
			Log.i(LOG, "是否播放1"+myPlayer.isPlaying());
		  myPlayer.stop();
		  myPlayer.release();
			Log.i(LOG, "是否播放2"+myPlayer.isPlaying());
		 }
	  myPlayer.release();
 //myRecorder.release();
// super.onDestroy();
}

public class RecordBinder extends Binder{	
	public boolean isRecording() {
		return isRecording;		
	}		
	public void startRecording()
	{
		startRecord();
	}	
	public void stopRecording() {
		stopRecord();
	}
	public void playRecording(){
		playRecord();
	}
	public void  stopPlayRecording() {
		stopPlayRecord();
	}	
	//判断是否在播放录音
	public boolean isPlayRecording() {
		return myPlayer.isPlaying();
	}
}



}
