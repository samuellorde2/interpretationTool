package com.lzh.interpretationtool.tool;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicLoader {
	
	Context context;
	SharedPreferences sp;
	
	private static final String TAG = "com.lzh.interpretationtool.tool.MusicLoader";
	
	private static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	
	private static MusicLoader musicLoader;
	
	private static ContentResolver contentResolver;
	
	private Uri contentUri = Media.EXTERNAL_CONTENT_URI;
	//String[]sharedPreferencedName;
	
	private String[] projection = {
			Media._ID,
			Media.TITLE,
			Media.DATA,
			Media.ALBUM,
			Media.ARTIST,
			Media.DURATION,			
			Media.SIZE
	};
	private String where =  "is_music > 0 and _data not like '/mnt/sdcard/XIONGRECORDERS%'" ;
	//判断是否为自身录音文件的选择语句
	private String where1 =  "_data like '/mnt/sdcard/XIONGRECORDERS%' and is_music > 0 " ;
	
	private String sortOrder = Media.DATA;
	
	//返回的是所有音乐文件
	public static MusicLoader instance(ContentResolver pContentResolver){
		if(musicLoader == null){
			contentResolver = pContentResolver;
			musicLoader = new MusicLoader();			
		}
		return musicLoader;
	}
	//返回录音文件
	public static MusicLoader instanceForRecord(ContentResolver pContentResolver){
		if(musicLoader == null){
			contentResolver = pContentResolver;
			musicLoader = new MusicLoader("Record");			
		}
		return musicLoader;
	}
	
	//查询出所有非录音文件的文件
	private MusicLoader(){	
		Cursor cursor = contentResolver.query(contentUri, null, where, null, null);
		//Log.i("TAG", "查询的路径为:"+contentUri);
		if(cursor == null){
			//Log.v(TAG,"Line(37	)	Music Loader cursor == null.");
		}else if(!cursor.moveToFirst()){
			//Log.v(TAG,"Line(39	)	Music Loader cursor.moveToFirst() returns false.");
		}else{			
			int displayNameCol = cursor.getColumnIndex(Media.TITLE);
			int albumCol = cursor.getColumnIndex(Media.ALBUM);
			int idCol = cursor.getColumnIndex(Media._ID);
			int durationCol = cursor.getColumnIndex(Media.DURATION);
			int sizeCol = cursor.getColumnIndex(Media.SIZE);
			int artistCol = cursor.getColumnIndex(Media.ARTIST);
			int urlCol = cursor.getColumnIndex(Media.DATA);			
			do{
				String title = cursor.getString(displayNameCol);
				String album = cursor.getString(albumCol);
				long id = cursor.getLong(idCol);				
				int duration = cursor.getInt(durationCol);
				long size = cursor.getLong(sizeCol);
				String artist = cursor.getString(artistCol);
				String url = cursor.getString(urlCol);
				
				MusicInfo musicInfo = new MusicInfo(id, title);
				musicInfo.setAlbum(album);
				musicInfo.setDuration(duration);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setUrl(url);
				musicList.add(musicInfo);
				
			}while(cursor.moveToNext());
		}
	}
	
	//录音文件的数据库路径
	private MusicLoader(String none){	
		Cursor cursor = contentResolver.query(contentUri, null, where1, null, null);
		Log.i("TAG", "查询的路径为11111:"+contentUri);
		if(cursor == null){
			Log.v(TAG,"Line(37	)	Music Loader cursor == null.");
		}else if(!cursor.moveToFirst()){
			Log.v(TAG,"Line(39	)	Music Loader cursor.moveToFirst() returns false.");
		}else{			
			int displayNameCol = cursor.getColumnIndex(Media.TITLE);
			int albumCol = cursor.getColumnIndex(Media.ALBUM);
			int idCol = cursor.getColumnIndex(Media._ID);
			int durationCol = cursor.getColumnIndex(Media.DURATION);
			int sizeCol = cursor.getColumnIndex(Media.SIZE);
			int artistCol = cursor.getColumnIndex(Media.ARTIST);
			int urlCol = cursor.getColumnIndex(Media.DATA);			
			do{
				String title = cursor.getString(displayNameCol);
				String album = cursor.getString(albumCol);
				long id = cursor.getLong(idCol);				
				int duration = cursor.getInt(durationCol);
				long size = cursor.getLong(sizeCol);
				String artist = cursor.getString(artistCol);
				String url = cursor.getString(urlCol);
				
				MusicInfo musicInfo = new MusicInfo(id, title);
				musicInfo.setAlbum(album);
				musicInfo.setDuration(duration);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setUrl(url);
				musicList.add(musicInfo);
				
			}while(cursor.moveToNext());
		}
	}
	
	
	//改变consecutive的播放音乐		
	private MusicLoader(String none,Context context){		
		Cursor cursor = contentResolver.query(contentUri, null, where, null, null);
		Log.i("TAG", "查询的路径为11111:"+contentUri);						
		if(cursor == null){
			Log.v(TAG,"Line(37	)	Music Loader cursor == null.");
		}else if(!cursor.moveToFirst()){
			Log.v(TAG,"Line(39	)	Music Loader cursor.moveToFirst() returns false.");
		}else{			
			int displayNameCol = cursor.getColumnIndex(Media.TITLE);
			int albumCol = cursor.getColumnIndex(Media.ALBUM);
			int idCol = cursor.getColumnIndex(Media._ID);
			int durationCol = cursor.getColumnIndex(Media.DURATION);
			int sizeCol = cursor.getColumnIndex(Media.SIZE);
			int artistCol = cursor.getColumnIndex(Media.ARTIST);
			int urlCol = cursor.getColumnIndex(Media.DATA);			
			do{
				String title = cursor.getString(displayNameCol);
				String album = cursor.getString(albumCol);
				long id = cursor.getLong(idCol);				
				int duration = cursor.getInt(durationCol);
				long size = cursor.getLong(sizeCol);
				String artist = cursor.getString(artistCol);
				String url = cursor.getString(urlCol);
				
				MusicInfo musicInfo = new MusicInfo(id, title);
				musicInfo.setAlbum(album);
				musicInfo.setDuration(duration);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setUrl(url);
				musicList.add(musicInfo);
				
			}while(cursor.moveToNext());
		}
	}

	
	public List<MusicInfo> getMusicList(){
		return musicList;
	}
	
	public Uri getMusicUriById(long id){
		Uri uri = ContentUris.withAppendedId(contentUri, id);
		return uri;
	}	

	public static class MusicInfo implements Parcelable{										
		private long id;
		private String title;
		private String album;
		private int duration;
		private long size;
		private String artist;		
		private String url;		
		
		public MusicInfo(){
			
		}
		
		public MusicInfo(long pId, String pTitle){
			id = pId;
			title = pTitle;
		}
		
		public String getArtist() {
			return artist;
		}

		public void setArtist(String artist) {
			this.artist = artist;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}		

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}	

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(id);
			dest.writeString(title);
			dest.writeString(album);
			dest.writeString(artist);
			dest.writeString(url);
			dest.writeInt(duration);
			dest.writeLong(size);
		}
		
		public static final Parcelable.Creator<MusicInfo> 
			CREATOR = new Creator<MusicLoader.MusicInfo>() {
			
			@Override
			public MusicInfo[] newArray(int size) {
				return new MusicInfo[size];
			}
			
			@Override
			public MusicInfo createFromParcel(Parcel source) {
				MusicInfo musicInfo = new MusicInfo();
				musicInfo.setId(source.readLong());
				musicInfo.setTitle(source.readString());
				musicInfo.setAlbum(source.readString());
				musicInfo.setArtist(source.readString());
				musicInfo.setUrl(source.readString());
				musicInfo.setDuration(source.readInt());
				musicInfo.setSize(source.readLong());
				return musicInfo;
			}
		};
	}
	
	//将数据xiongcode下的记录清除
	public void deleteDatabase()
	{

		
	}	

}
