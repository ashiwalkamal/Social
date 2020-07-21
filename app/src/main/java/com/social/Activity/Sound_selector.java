package com.social.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.deskode.recorddialog.RecordDialog;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Gallery_video;
import com.social.adpaters.Sound_adpater;
import com.social.databinding.ActivitySoundSelectorBinding;
import com.social.model.AudioModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.social.Activity.MainActivity.gallery;

public class Sound_selector extends AppCompatActivity {
    ActivitySoundSelectorBinding binding;
    Sound_adpater sound_adpater;
    ArrayList<AudioModel> sounds=new ArrayList<>();

    ContentResolver contentResolver;

    Cursor cursor;
    String filenow;
    Uri uri;
    RecordDialog recordDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_sound_selector);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
       //click
        click();
        sounds=GetAllMediaMp3Files();
        // to loaddata
       load();
    }

    private void load() {
        if(sounds.size()!=0)
        {
            binding.soundList.setVisibility(View.VISIBLE);
            binding.nrecord.setVisibility(View.GONE);
            //show list
            list();
        }
        else
        {
            binding.soundList.setVisibility(View.GONE);
            binding.nrecord.setVisibility(View.VISIBLE);
        }
    }

    private void list() {
        binding.soundList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        sound_adpater = new Sound_adpater(this,Sound_selector.this,  sounds);
        binding.soundList.setAdapter(sound_adpater);
    }

    private void click() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record_audio();
            }
        });
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    public ArrayList<AudioModel> GetAllMediaMp3Files(){

        contentResolver = getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            Utlity.show_toast(this,"Something Went Wrong.");
        } else if (!cursor.moveToFirst()) {
            Utlity.show_toast(this,"No Music Found");

        }
        else {

            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {
                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);
                AudioModel audioModel = new AudioModel();
                int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int singer = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
               int path= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                String SongTitle = cursor.getString(Title);
                String artist = cursor.getString(singer);
                String paths = cursor.getString(path);
                audioModel.setaName(SongTitle);
                audioModel.setaArtist(artist);
                audioModel.setaPath(paths);
                // Adding Media File Names to ListElementsArrayList.
                sounds.add(audioModel);

            } while (cursor.moveToNext());
        }
        return sounds;
    }


    public ArrayList<AudioModel> getAllAudioFromDevice(final Context context) {

        final ArrayList<AudioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%utm%"}, null);
        if (c != null) {
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                String name = path.substring(path.lastIndexOf("/") + 1);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);
                tempAudioList.add(audioModel);
            }
            c.close();
        }
        return tempAudioList;
    }

    public void selectted(String getaPath) {
        Intent intent=new Intent();
        intent.setData(Uri.parse(getaPath));
        setResult(RESULT_OK, intent);
       finish();//finishing activity
    }


    public void record_audio()
    {
        recordDialog = RecordDialog.newInstance("Record Audio");
        recordDialog.setMessage("Press for record");
        recordDialog.setTitle("");
        recordDialog.show(this.getFragmentManager(),"TAG");
        recordDialog.setPositiveButton("Save", new RecordDialog.ClickListener() {
            @Override
            public void OnClickListener(String path) {
                // Great! User has recorded and saved the audio file
                AudioModel model=new AudioModel();
                model.setaName(path);
                model.setaArtist(MainActivity.firebaseUser.getDisplayName());
                model.setaPath(path);
                sounds.add(0,model);
                list();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 ) {

            }
        }

}