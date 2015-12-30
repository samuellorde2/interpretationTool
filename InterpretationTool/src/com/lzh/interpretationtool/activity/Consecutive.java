package com.lzh.interpretationtool.activity;

/**
 * 1.加入向左滑动，进入录音界面的功能
 * 2.点击导入键，将选中音乐道路播放列表
 */
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import com.lzh.interpretationtool.R;

import com.lzh.interpretationtool.service.NatureService;
import com.lzh.interpretationtool.service.NatureService.NatureBinder;
import com.lzh.interpretationtool.service.RecordService;
import com.lzh.interpretationtool.service.RecordService.RecordBinder;
import com.lzh.interpretationtool.tool.FormatHelper;
import com.lzh.interpretationtool.tool.MusicLoader;
import com.lzh.interpretationtool.tool.MusicLoader.MusicInfo;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Consecutive extends Activity implements OnClickListener,OnKeyListener{
	Button recordAndStop;
	//LinearLayout recordLayout;
	Button startStopPlayRecord;
	private TextView tvCurrentMusic;
	Button recordFile;	//下一曲
	private Button btnNext;
	//源文件播放和暂停键
	private Button btnStartStop;
	private Button btnDetail;
	private Button input;
	Intent serviceIntent;
	//播放源文件进度条
	private SeekBar pbDuration;
	private ListView lvSongs;

	private List<MusicLoader.MusicInfo> musicList1;
	private List<MusicLoader.MusicInfo> musicList=new ArrayList<MusicLoader.MusicInfo>();
	private int currentMusic; // The music that is playing.
	private int currentPosition; //The position of the music is playing.
	private int currentMax;
	String TAG="Consecutive" ;
	 private ListView listView;
	static String fileName;
	 // 音频文件保存地址
	 private String path;
	 private String paths = path;
	 File saveFilePath;	
	 // 所录音的文件
	 AlertDialog aler = null;	
    private ProgressReceiver progressReceiver;	
	private NatureBinder natureBinder;	
	private RecordBinder recordBinder;		
	//向右划，进入录音文件界面
	private GestureDetector gestureDetector;
	final int RIGHT = 0;
	final int LEFT = 1;	
	SharedPreferences sp;
	//连接NatureService的接口	
	//连接natureservice的服务 负责播放音乐的
	private ServiceConnection serviceConnection = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName name) {		
		}	
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			natureBinder = (NatureBinder) service;			
		}
	};
	
	private void connectToNatureService(){		
		Intent intent = new Intent(Consecutive.this, NatureService.class);				
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);				
	}
	
	
	//负责连接recordService的接口，控制recordservice
	private ServiceConnection recordConnection = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName name) {			
		}		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			recordBinder = (RecordBinder) service;			
		}
	};
	
	private void connectToRecordService(){		
		Intent intent2 = new Intent(Consecutive.this, RecordService.class);				
		bindService(intent2, recordConnection, BIND_AUTO_CREATE);				
	}		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.consecutive);	
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titilebar1); 
		initUi();			     
		//点击录音按键的代码
		connectToNatureService();
		connectToRecordService();		
	}		
	public void onResume(){
		Log.v(TAG, "OnResume register Progress Receiver");
		super.onResume();										
		registerReceiver();
		if(natureBinder != null){
			if(natureBinder.isPlaying()){
				btnStartStop.setBackgroundResource(R.drawable.pause);
				//btnStartStop.setBackgroundResource(R.drawable.pause);
			}else{
				btnStartStop.setBackgroundResource(R.drawable.play);
				//btnStartStop.setBackgroundResource(R.drawable.play);
			}
			natureBinder.notifyActivity();
		}
	}
	
	public void onPause(){
		Log.v(TAG, "OnPause unregister Progress Receiver");
		
		super.onPause();
		//解除播放源文件进度的广播接受者
		unregisterReceiver(progressReceiver);
	}
	
	public void onStop(){
		Log.v(TAG, "OnStop");
		super.onStop();				
	}
	
	
	public void initUi()
	{
		

		recordAndStop=(Button) findViewById(R.id.recordAndStop);
		startStopPlayRecord=(Button) findViewById(R.id.startStopPlayRecord);
		recordFile=(Button) findViewById(R.id.recordFile);
		btnStartStop = (Button)findViewById(R.id.btnStartStop);
		btnNext = (Button)findViewById(R.id.btnNext);
		btnDetail = (Button) findViewById(R.id.btnDetail);
        input=(Button) findViewById(R.id.input);
		tvCurrentMusic = (TextView) findViewById(R.id.tvCurrentMusic1);
		//recordLayout=(LinearLayout) findViewById(R.id.recordLayout);
		recordAndStop.setOnClickListener(this);		
		startStopPlayRecord.setOnClickListener(this);
		recordFile.setOnClickListener(this);
		btnStartStop.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnDetail.setOnClickListener(this);
		input.setOnClickListener(this);
		//播放系统音乐文件部分
		pbDuration = (SeekBar) findViewById(R.id.pbDuration);	
		pbDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {		
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					natureBinder.changeProgress(progress);
				}
			}
		});
		
		

	
		MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
		musicList1= musicLoader.getMusicList();
		
		sp =getSharedPreferences("ChoosedMusic",MODE_PRIVATE);
		if (musicList1.size()!=0) {					
		for (int i = 0; i < musicList1.size(); i++) {		
			if (sp.contains(musicList1.get(i).getTitle())) {
				String title=musicList1.get(i).getTitle();
				Log.i(TAG, title);	
				musicList.add(musicList1.get(i));
			}else
			{
				//musicList.remove(i);				
			}
		}
		}
		
		lvSongs = (ListView) findViewById(R.id.lvSongs);	
		MusicAdapter adapter = new MusicAdapter();			
		if (musicList.size()!=0) {
			lvSongs.setAdapter(adapter);
			lvSongs.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					currentMusic = position;
					natureBinder.startPlay(currentMusic,0);
					if(natureBinder.isPlaying()){	
						btnStartStop.setBackgroundResource(R.drawable.pause);		
					}
				}
			});
		}
		//初始化向左划进入录音文件界面
		gestureDetector = new GestureDetector(Consecutive.this,onGestureListener);
	}
	
	//用服务进行和停止录音
	public void startandStopRecording() {
			   
		    //Log.i(TAG, "文件名为w"+recordBinder.isRecording());	
		    
		    //为非录音状态
		    if (!recordBinder.isRecording()) {
		    	//若在播放录音
		    	if (recordBinder.isPlayRecording()) {
					//停止播放录音
		    		recordBinder.stopPlayRecording();
		    	}	
		    	//若在播放源文件：
		    	if (natureBinder.isPlaying()) {
		    		//停止播放
		    		natureBinder.startPlay(currentMusic, currentPosition);			
				}
		        Log.i(TAG, "我在录音中"+recordBinder.isRecording());	
					recordBinder.startRecording();
					recordAndStop.setBackgroundResource(R.drawable.record_stop);	
				    Toast.makeText(getApplicationContext(), "it is recording", 3000).show();
			//停止录音
		    }else if (recordBinder.isRecording()) {
		        Log.i(TAG, "非录音中"+recordBinder.isRecording());	
				recordBinder.stopRecording();
				  //Log.i(TAG, "进入2"+recordBinder.isRecording());	
				recordAndStop.setBackgroundResource(R.drawable.record_start);	
			    Toast.makeText(getApplicationContext(), "recording is stop", 3000).show();
			}
		   // recordBinder.startRecording();

	}		
	//播放录音
	public void startStopPlayRecording() {
		//播放录音时不予处理
		if (recordBinder.isRecording()) {
			 Toast.makeText(getApplicationContext(), "请您先停止录音", 3000).show();
	    //录音停止完成时
		}else {
			  //如果正在播放录音，则停止播放
			if (recordBinder.isPlayRecording()) {
				//recordBinder.stopPlayRecording();
				recordBinder.stopPlayRecording();
				startStopPlayRecord.setBackgroundResource(R.drawable.play);
		         Toast.makeText(getApplicationContext(), "停止播放", 3000).show();
			}else {
				//若未播放录音
				//判断源文件音乐是否开启，若开启则关闭源文件
				if (natureBinder.isPlaying()) {
					natureBinder.stopPlay();
				}
				 recordBinder.playRecording();
					startStopPlayRecord.setBackgroundResource(R.drawable.pause);
		         Toast.makeText(getApplicationContext(), "播放录音", 3000).show();
			}
		}

	}
	

	 private void recordFileList() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(Consecutive.this,RecordList.class);
		//Log.v(TAG, "here i am 1");
		startActivity(intent);
		//Log.v(TAG, "here i am 12");
		overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		//录音+停录
		case R.id.recordAndStop:
			startandStopRecording();
			break;
		//播放录音+停止播放键		
		case R.id.startStopPlayRecord:
			startStopPlayRecording();
			break;
		
		case R.id.recordFile:	
			Log.v(TAG, "here i am 1");
			 recordFileList();
			 finish();
			 break;			 
	    //播放口译文件
		case R.id.btnStartStop:
			play(currentMusic,R.id.btnStartStop);
			 break;
			 //下一曲
		case R.id.btnNext:
			natureBinder.toNext();
			 break;
			 //查看细节
		case R.id.btnDetail:						
			Intent intent = new Intent(Consecutive.this,DetailActivity.class);
			intent.putExtra(DetailActivity.MUSIC_LENGTH, currentMax);
			intent.putExtra(DetailActivity.CURRENT_MUSIC, currentMusic);
			intent.putExtra(DetailActivity.CURRENT_POSITION, currentPosition);			
			startActivity(intent);	
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			finish();
			break;	 
			
		case R.id.input:
			Intent intent2 = new Intent(Consecutive.this,ChooseMediaActivity.class);			
			startActivity(intent2);	
			finish();
			break;
		
			
			
			
		}
	}

   //播放源文件
	private void play(int position, int resId){	
		//停止播放
		if(natureBinder.isPlaying()){		
			natureBinder.stopPlay();
			btnStartStop.setBackgroundResource(R.drawable.play);
			//btnStartStop.setBackgroundResource(R.drawable.play);
			//开始播放
		}else{
		
			natureBinder.startPlay(position,currentPosition);
			btnStartStop.setBackgroundResource(R.drawable.pause);
			//btnStartStop.setBackgroundResource(R.drawable.pause);
		}
	}

	protected void onDestroy() {
		  // 释放资源
		super.onDestroy();
		// stopService(serviceIntent);
			if(natureBinder != null){
				unbindService(serviceConnection);
			}
			if(recordBinder != null){
				unbindService(recordConnection);
			}
	 }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(Consecutive.this,Functionchoice.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	//注册service传递过来的广播监听器，更新进度条
	private void registerReceiver(){
		progressReceiver = new ProgressReceiver();	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NatureService.ACTION_UPDATE_PROGRESS);
		intentFilter.addAction(NatureService.ACTION_UPDATE_DURATION);
		intentFilter.addAction(NatureService.ACTION_UPDATE_CURRENT_MUSIC);
		registerReceiver(progressReceiver, intentFilter);
	}
	
	
	//手机上的音乐文件的适配器
	
	class MusicAdapter extends BaseAdapter{

		@Override 
		public int getCount() {
			Log.v(TAG, "musicList.size"+musicList.size());
			return musicList.size();
		}

		@Override
		public Object getItem(int position) {
			return musicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return musicList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder; 
			if(convertView == null){
				convertView = LayoutInflater.from(Consecutive.this).inflate(R.layout.music_item, null);
				ImageView pImageView = (ImageView) convertView.findViewById(R.id.albumPhoto);
				TextView pTitle = (TextView) convertView.findViewById(R.id.title);
				TextView pDuration = (TextView) convertView.findViewById(R.id.duration);
				TextView pArtist = (TextView) convertView.findViewById(R.id.artist);
				viewHolder = new ViewHolder(pImageView, pTitle, pDuration, pArtist);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.imageView.setImageResource(R.drawable.interpre_icon);
			viewHolder.title.setText(musicList.get(position).getTitle());
			viewHolder.duration.setText(FormatHelper.formatDuration(musicList.get(position).getDuration()));
			viewHolder.artist.setText(musicList.get(position).getArtist());
			
			return convertView;
		}		
	}
	//手机音乐文件listview的holder
	class ViewHolder{
		public ViewHolder(ImageView pImageView, TextView pTitle, TextView pDuration, TextView pArtist){
			imageView = pImageView;
			title = pTitle;
			duration = pDuration;
			artist = pArtist;
		}
		
		ImageView imageView;
		TextView title;
		TextView duration;
		TextView artist;
	}
	
	//处理service传过来的进度条事件，更新进度条

	class ProgressReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(NatureService.ACTION_UPDATE_PROGRESS.equals(action)){
				int progress = intent.getIntExtra(NatureService.ACTION_UPDATE_PROGRESS, 0);
				if(progress > 0){
					currentPosition = progress; // Remember the current position
					pbDuration.setProgress(progress / 1000);
				}
			}else if(NatureService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)){
				//Retrive the current music and get the title to show on top of the screen.
				currentMusic = intent.getIntExtra(NatureService.ACTION_UPDATE_CURRENT_MUSIC, 0);
				if (!musicList.isEmpty()) {
					Log.i(TAG, "进来了吗");
					tvCurrentMusic.setText(musicList.get(currentMusic).getTitle());
					Log.i(TAG, "进来了吗+打印出来"+musicList.get(currentMusic).getTitle());
				}
				
			}else if(NatureService.ACTION_UPDATE_DURATION.equals(action)){
				//Receive the duration and show under the progress bar
				//Why do this ? because from the ContentResolver, the duration is zero.
				currentMax = intent.getIntExtra(NatureService.ACTION_UPDATE_DURATION, 0);				
				int max = currentMax / 1000;
				//Log.v(TAG, "[Main ProgressReciver] Receive duration : " + max);
				pbDuration.setMax(currentMax / 1000);						
			}
		}
		
	}
	
	
	
	private GestureDetector.OnGestureListener onGestureListener = 
			new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				float x = e2.getX() - e1.getX();
				float y = e2.getY() - e1.getY();

				if (x > 0) {
					doResult(RIGHT);
				} else if (x < 0) {
					doResult(LEFT);
				}
				return true;
			}
		};

	 

		public boolean onTouchEvent(MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		public void doResult(int action) {

			switch (action) {
			case RIGHT:
				System.out.println("go right");

				break;

			case LEFT:
				System.out.println("go left");
				Log.i(TAG, "进来了的");
				Intent intent=new Intent(Consecutive.this,RecordList.class);
				finish();
				startActivity(intent);
				overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
				break;

			}
		}
		
	  
}
