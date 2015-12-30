package com.lzh.interpretationtool.activity;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lzh.interpretationtool.R;
import com.lzh.interpretationtool.tool.FormatHelper;
import com.lzh.interpretationtool.tool.MusicLoader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseMediaActivity extends Activity implements OnKeyListener,OnClickListener{
	
	
	String TAG="ChooseMediaActivity";
	Map<String, String> isCheckMap =  new HashMap<String, String>();
	private CheckBox musicCheckBox;
	SharedPreferences sp;
	Button confirm;
	Button cancel;
	String musicName;
	private ListView xuanze;	
	private List<MusicLoader.MusicInfo> musicList;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.choose_media);
	initGagdet();		
}
public void initGagdet() {


	confirm=(Button) findViewById(R.id.add);
	cancel=(Button) findViewById(R.id.quxiao);
	confirm.setOnClickListener(this);
	cancel.setOnClickListener(this);
	sp =getSharedPreferences("ChoosedMusic",MODE_PRIVATE);
	xuanze = (ListView) findViewById(R.id.xuanze);	
	Music1Adapter adapter = new Music1Adapter();
	MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
	musicList = musicLoader.getMusicList();
	//判断sp是否曾经选过歌曲
	for (int i = 0; i < musicList.size(); i++) {
		if (sp.contains(musicList.get(i).getTitle())) {
			String titleName=sp.getString(musicList.get(i).getTitle(), 0+"");
			isCheckMap.put(titleName, titleName);
		}
	}	
	if (musicList.size()!=0) {
		
		xuanze.setAdapter(adapter);
		xuanze.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				RelativeLayout layout=(RelativeLayout) view;
				
				ImageView image=(ImageView)layout.getChildAt(0);
				if(isCheckMap!=null && isCheckMap.containsKey(musicList.get(position).getTitle()))
				{				
			     isCheckMap.remove(musicList.get(position).getTitle());
			    // Log.i(TAG, "否"+position);
			    image.setBackgroundResource(R.drawable.notchecked);
			    
				}else {
				 musicName=musicList.get(position).getTitle();
				 isCheckMap.put(musicName, musicName);
				// Log.i(TAG, "是"+position);
				    image.setBackgroundResource(R.drawable.checked);
				}
				
				
				
			}
		});
	}

	
}
//手机上的音乐文件的适配器

class Music1Adapter extends BaseAdapter{

	@Override 
	public int getCount() {
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
			convertView = LayoutInflater.from(ChooseMediaActivity.this).inflate(R.layout.choose_media_items, null);
			ImageView pImageView = (ImageView) convertView.findViewById(R.id.musicCheckImage);
			TextView pTitle = (TextView) convertView.findViewById(R.id.file_name);
			TextView pDuration = (TextView) convertView.findViewById(R.id.musicduration);
			TextView pArtist = (TextView) convertView.findViewById(R.id.recordUser);
			viewHolder = new ViewHolder(pImageView, pTitle, pDuration, pArtist);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}		
		//判断是否已选
		if(isCheckMap!=null && isCheckMap.containsKey(musicList.get(position).getTitle()))
		{		
			viewHolder.imageView.setBackgroundResource(R.drawable.checked);			
		}else {
			viewHolder.imageView.setBackgroundResource(R.drawable.notchecked);
		}
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
@Override
public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	return false;
}
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	
	if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
		//Log.i("有效吗？", "hanshuyouxia");
		return true; // 阻止按键事件继续向下分发
	}	
	return super.onKeyDown(keyCode, event);	
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.add:
		Editor editor =sp.edit();
		editor.clear();
		for (int i = 0; i < musicList.size(); i++) {
			if (isCheckMap!=null&&isCheckMap.containsKey(musicList.get(i).getTitle())) {				
				editor.putString(musicList.get(i).getTitle(),musicList.get(i).getTitle());
			}
		}
		editor.commit();
		
		for (int i = 0; i < musicList.size(); i++) {
			if (sp.contains(musicList.get(i).getTitle())) {
				String xString=sp.getString(musicList.get(i).getTitle(), 0+"");
				Log.i(TAG, "sp中"+xString);
			}
		}

		Intent intent=new Intent(ChooseMediaActivity.this,Consecutive.class);
		startActivity(intent);
		finish();
		break;

	case R.id.quxiao:
		
		Intent intent2=new Intent(ChooseMediaActivity.this,Consecutive.class);
		startActivity(intent2);
		finish();
		break;
	}
	
}
}
