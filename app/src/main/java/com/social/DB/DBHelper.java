package com.social.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.social.R;
import com.social.model.Video;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME =".db";
    public static final String CONTACTS_TABLE_NAME = "post";

    public DBHelper(Context context){
        super(context,context.getString(R.string.app_name)+DATABASE_NAME,null,1);
    }
    public void onCreate(SQLiteDatabase db) {

        // TODO Auto-generated method stub
        db.execSQL(
                "create table post" +
                        "(id int, userid text,userame text,profilephoto text, videourl text,location text,title text, play boolean,lat Double,lng Double)"
        );
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS post");
        onCreate(database);
    }
    public ArrayList<Video> getallpost() {
        ArrayList<Video> array_list = new ArrayList<Video>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from post", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Video video=new Video();
            video.setId(res.getInt(res.getColumnIndex("id")));
            video.setUserid(res.getString(res.getColumnIndex("userid")));
            video.setVideourl(res.getString(res.getColumnIndex("videourl")));
            video.setLocation(res.getString(res.getColumnIndex("location")));
            video.setTitle(res.getString(res.getColumnIndex("title")));
            video.setLng(res.getDouble(res.getColumnIndex("lat")));
            video.setLat(res.getDouble(res.getColumnIndex("lng")));
            video.setPlay(false);
            array_list.add(video);
            res.moveToNext();
        }
        return array_list;
    }
    public Video insertpost(Video video) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", video.getUserid());
        contentValues.put("videourl", video.getVideourl());
        contentValues.put("location", video.getLocation());
        contentValues.put("title", video.getTitle());
        contentValues.put("play", video.isPlay());
        contentValues.put("lat", video.getLat());
        contentValues.put("lng", video.getLng());
        db.insert("post", null, contentValues);
        String selectQuery = "SELECT * FROM post ORDER BY id DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        video.setId(cursor.getInt(cursor.getColumnIndex("id")));
        return video;
    }
}
