package com.lzh.interpretationtool.activity;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.lzh.interpretationtool.R;
import com.lzh.interpretationtool.activity.Consecutive.ViewHolder;
import com.lzh.interpretationtool.tool.FormatHelper;
import com.lzh.interpretationtool.tool.MusicLoader;




import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ListView;

public class RecordList  extends Activity implements OnKeyListener {

	ListView listView;
	RecordFileAdapter recordFileAdapter;
	String path; 	
	String [] listFile={};
	//private List listFile=new ArrayList();
    String lOG="RecordList";
    SharedPreferences sp;
	 private MediaPlayer myPlayer;	
		private List<MusicLoader.MusicInfo> musicList;
   // boolean returnToRecord=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.v(lOG, "here i am 13");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_file_list);
		 myPlayer = new MediaPlayer();	
		 
		 //获取文件的数据库
		 MusicLoader musicLoader = MusicLoader.instanceForRecord(getContentResolver());
			musicList = musicLoader.getMusicList();
		 //获取文件路经
		if (Environment.getExternalStorageState().equals(
			    Environment.MEDIA_MOUNTED)) {
			   try {
			    path = Environment.getExternalStorageDirectory()
			      .getCanonicalPath().toString()
			      + "/XIONGRECORDERS";
			    File files = new File(path);
			    if (!files.exists()) {
			     files.mkdir();
			    }
			    listFile = files.list();
			   } catch (IOException e) {
			    e.printStackTrace();
			   }
			  }	
		for (int i = 0; i < musicList.size(); i++) {
			//Log.i(lOG, "名左右"+musicList.get(i).getTitle());
		}
		Log.v(lOG, "here i am 14here listfile"+listFile.length);
		recordFileAdapter=new RecordFileAdapter();
		if(listFile.length!=0){			
			Log.v(lOG, "here i am 15");
			sp =getSharedPreferences("RecordList",MODE_PRIVATE);
			listView=(ListView) findViewById(R.id.listView);
			listView.setAdapter(recordFileAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
			    	Editor editor=sp.edit();    	
			    	String controlFilePath=path + "/" + listFile[position];
			    	editor.putString("controlFilePath", controlFilePath);
			    	editor.commit();
			    	listView.showContextMenu();
				}
			});
		    registerForContextMenu(listView);
		}
		Log.v(lOG, "here i am 152");
		

	}
	//创建menu时
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub	
		super.onCreateContextMenu(menu, v, menuInfo);
		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.recordlist_delete, menu);
	}
	//点击删除录音时
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		  //int id = (int) info.id;
		switch (item.getItemId()) {
		case R.id.cancel:			
			break;

		case R.id.delete:
			String deleteFilePath=sp.getString("controlFilePath", "");
			File targetFile=new File(deleteFilePath);
			targetFile.delete();
			//recordFileAdapter.notifyDataSetChanged();
			finish();
			Intent intent=new Intent(RecordList.this, RecordList.class);
		    startActivity(intent);
			//todo将数据库的数据删除
		    break;
			
			
		case R.id.play:
	        try {
	       String playFilePath=sp.getString("controlFilePath", "");
	        myPlayer.reset();
	        myPlayer.setDataSource(playFilePath);
	        if (!myPlayer.isPlaying()) {
	         myPlayer.prepare();
	         myPlayer.start();
	        } else {
	         myPlayer.pause();
	        }
	       } catch (IOException e) {
	        e.printStackTrace();
	       }
			break;
		case R.id.stop:
	    	//Log.i(TAG, "来过");
	        if (myPlayer.isPlaying()) {
	            myPlayer.stop();
	            myPlayer.release();
	           }
			break;
		}
		return false;
		
	}
	class RecordFileAdapter extends BaseAdapter {
		private static final String TAG = "RecordFileAdapter";
		private LayoutInflater inflater;	

		 // 录音文件播放
			
		public RecordFileAdapter()
		{
	  
			inflater = LayoutInflater.from(RecordList.this);
			
				
			
	}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			Log.v(TAG, "listFile.length"+listFile.length);
				return listFile.length;

			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub 
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
		
			//TextView filename = (TextView) views.findViewById(R.id.show_file_name);
			//filename.setText(listFile[position]);	
			
			//新加入
			ViewHolder1 viewHolder; 
			if(convertView == null){
				convertView =inflater.inflate(R.layout.record_file_items, null);
				ImageView pImageView=(ImageView) convertView.findViewById(R.id.recordPhoto);
				TextView pTitle = (TextView) convertView.findViewById(R.id.show_file_name);
				TextView pDuration = (TextView) convertView.findViewById(R.id.recordduration);
				TextView pArtist = (TextView) convertView.findViewById(R.id.recordUser);
				viewHolder = new ViewHolder1(pImageView, pTitle, pDuration, pArtist);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder1) convertView.getTag();
			}			
			viewHolder.imageView.setImageResource(R.drawable.interpre_icon);
			viewHolder.title.setText(listFile[position]);
			String name=listFile[position];
			String changName=name.substring(0, 14);
			//Log.i(lOG,"修改的名字为"+changName);
			//判断在文件夹中存在的文件，是否存在数据库中							
				for (int j = 0; j < musicList.size(); j++) {	
					if (name.startsWith(musicList.get(j).getTitle())) {
							viewHolder.duration.setText(FormatHelper.formatDuration(musicList.get(j).getDuration()));
							viewHolder.artist.setText(musicList.get(j).getArtist());	
							return convertView;						
					}
				}				
			return convertView;
			
		}
		

	}
	
	//录音文件文件listview的holder
		class ViewHolder1{
			public ViewHolder1(ImageView pImageView, TextView pTitle, TextView pDuration, TextView pArtist){
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
		
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	
	if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
		//Log.i("有效吗？", "hanshuyouxia");
		finish();
		Intent intent=new Intent(RecordList.this,Consecutive.class);
		startActivity(intent);

		return true; // 阻止按键事件继续向下分发
	}
	return super.onKeyDown(keyCode, event);
	
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
	
}

