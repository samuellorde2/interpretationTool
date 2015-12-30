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
	 // ��Ƶ�ļ������ַ
	 private static String path;
	 private  String paths = path;
	  File saveFilePath;
	// static String fileName;
	 String LOG="RecordService";
	 // ¼���ļ�����
	 private MediaPlayer myPlayer;
	 String music;
	 // ¼��
	 private MediaRecorder myRecorder;
	 private SharedPreferences sp;
	 private boolean isRecording=false;
	// private boolean isPlayRecording=false;
    Editor editor;
    //�жϰ��������ͣ����ţ�ռͣ��
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

	  // ����˷�Դ����¼��
	  //��ʼ��sharedPreference
	  sp =getSharedPreferences("RecordService",MODE_PRIVATE);


	  if (Environment.getExternalStorageState().equals(
			    Environment.MEDIA_MOUNTED)) {
			   try {
				//��ȡsdk��·��
			    path = Environment.getExternalStorageDirectory()
			      .getCanonicalPath().toString()
			      + "/XIONGRECORDERS";
			    File files = new File(path);
			    if (!files.exists()) {
			    	//�������ڣ������ļ���
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
	//Log.i(LOG, "1����recording");
	  myRecorder = new MediaRecorder();
	
	isRecording=true;
    paths = path+ "/" +new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".amr";

    saveFilePath = new File(paths);  
	
	  myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
	  // ���������ʽ
	  myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
	  // ���ñ����ʽ
	  myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    myRecorder.setOutputFile(saveFilePath.getAbsolutePath());  
  
    try {

		saveFilePath.createNewFile();

			  myRecorder.prepare();	        
	      // ��ʼ¼�� 

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
	Log.i(LOG, "û����release"+music);
	 if (saveFilePath.exists() && saveFilePath != null) {				 

		    myRecorder.stop();
		  myRecorder.release();			 
		    // �ж��Ƿ񱣴� �����������ɾ��
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
 // �ͷ���Դ
	super.onDestroy();
	 if (myPlayer.isPlaying()) {
			Log.i(LOG, "�Ƿ񲥷�1"+myPlayer.isPlaying());
		  myPlayer.stop();
		  myPlayer.release();
			Log.i(LOG, "�Ƿ񲥷�2"+myPlayer.isPlaying());
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
	//�ж��Ƿ��ڲ���¼��
	public boolean isPlayRecording() {
		return myPlayer.isPlaying();
	}
}



}
