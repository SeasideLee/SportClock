package com.example.seasidelee.sportsclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectSongs extends Activity {
    private ListView listView;
    private ArrayList<Songs> listMusic;
    private ImageView queren;
    private ImageView quxiao;
    public MediaPlayer mediaPlayer = new MediaPlayer();
    private List<HashMap<String, String>> songlist = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter;
    private int pre = -1;
    private boolean isplay = false;
    public static SelectSongs instance = null;
    public Intent preintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_songs);
        instance = SelectSongs.this;
        listView = (ListView)findViewById(R.id.SongList);
        queren = (ImageView)findViewById(R.id.queren);
        quxiao = (ImageView)findViewById(R.id.quxiao);
        listMusic = getsongsdata(this);
        int num = listMusic.size();
        for(int i = 0; i < num; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("SongTitle", listMusic.get(i).getTitle());
            songlist.add(map);
        }
        adapter = new SimpleAdapter(this, songlist, R.layout.songitem, new String[] {"SongTitle","isSelect","isplaying"}, new int[] {R.id.SongTitle, R.id.queren, R.id.att});
        listView.setAdapter(adapter);

        preintent = this.getIntent();
        long uri = preintent.getLongExtra("Uri", -1);
        if(uri >= 0) {
            for (int i = 0; i < num; i++) {
                if (uri == listMusic.get(i).getUrl()) {
                    pre = i;
                    break;
                }
            }
            for(int i = 0; i < num; i++){
                songlist.get(i).put("isSelect", null);
                songlist.get(i).put("isplaying", null);
            }
            songlist.get(pre).put("isSelect", String.valueOf(R.drawable.queren2));
            adapter.notifyDataSetChanged();
            pre = -1;
        }
        queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SelectSongs.this, CreateClock.class);
                if(pre >= 0){
                    Songs chosensong = listMusic.get(pre);
                    intent.putExtra("Uri",chosensong.getUrl());
                    intent.putExtra("songtitle",chosensong.getTitle());
                }
                intent.putExtra("hour", preintent.getIntExtra("hour",7));
                intent.putExtra("minute", preintent.getIntExtra("minute", 30));
                intent.putExtra("date", preintent.getBooleanArrayExtra("date"));
                intent.putExtra("lable", preintent.getStringExtra("lable"));
                intent.putExtra("ID", preintent.getIntExtra("ID", 0));
                intent.putExtra("iscreate", preintent.getBooleanExtra("iscreate", true));
                intent.putExtra("isreturnfromselect", true);
                intent.putExtra("clockposition", 1);
                CreateClock.instance.finish();
                startActivity(intent);
            }
        });

        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSongs.this.finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Songs song  = listMusic.get(position);
                long url = song.getUrl();
                Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    isplay = false;
                }
                else if(pre == position){
                    mediaPlayer.start();
                    isplay = true;
                }
                if(pre != position) {
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), contentUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    isplay = true;
                    pre = position;
                }
                int num = listMusic.size();
                for(int i = 0; i < num; i++){
                    songlist.get(i).put("isSelect", null);
                    songlist.get(i).put("isplaying", null);
                }
                songlist.get(position).put("isSelect", String.valueOf(R.drawable.queren2));
                if(isplay) songlist.get(position).put("isplaying", "   (播放中)");
                adapter.notifyDataSetChanged();
            }
        });
    }

    public ArrayList<Songs> getsongsdata(Context context){
        ArrayList<Songs> songlist = new ArrayList<Songs>();
        ContentResolver cr = context.getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if(cr != null){
           Cursor cursor = cr.query(uri, null, null, null, null);
            if(null == cursor) return null;
            if(cursor.moveToFirst()){
                do{
                    Songs song = new Songs();
                    String title = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE));
                    Long idColumn = cursor.getLong(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    if(name != null) {
                        String sbr = name.substring(name.length() - 3, name.length());
                        if (sbr.equals("mp3")) {
                            song.setTitle(title);
                            song.setUrl(idColumn);
                            songlist.add(song);
                        }
                    }
                }while(cursor.moveToNext());
            }
        }
        return songlist;
    }

    @Override
    protected void onStop(){
        super.onStop();
        isplay = false;
        if(pre >= 0) {
            int num = listMusic.size();
            for (int i = 0; i < num; i++) {
                songlist.get(i).put("isSelect", null);
                songlist.get(i).put("isplaying", null);
            }
            songlist.get(pre).put("isSelect", String.valueOf(R.drawable.queren2));
            adapter.notifyDataSetChanged();
        }
        mediaPlayer.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_songs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
