package orritt.browning.blurapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    TextView text;
    String videoPath;
    Boolean videoSelected = false;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        //connect buttons
        final Button button = (Button)findViewById(R.id.button);
        videoView = (VideoView)findViewById(R.id.videoView);
        text = (TextView)findViewById(R.id.textView);
        button.setVisibility(View.INVISIBLE);

        //load opencv
        OpenCVLoader.initDebug();

        //ask for permissions
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE }, 2);
            Log.e("###################: ", "WRITE EXTERNAL");
        }

        //load cascade classifiers
        load_face_cascade();
        load_profile_cascade();

        videoView.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                button.setVisibility(View.VISIBLE);
                text.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (videoSelected != true) {
                    Toast.makeText(getApplicationContext(), "No video selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    //TODO send to loading screen while waiting
                    Log.e("######: ", "Starting thread");
                    new Thread() {
                        @Override
                        public void run() {
                            Model.model(videoPath, load_face_cascade(), load_profile_cascade());
                        }
                    }.start();
                    Log.e("######: ", "Thread created");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            videoPath = getRealPathFromURI(this, targetUri);
            videoSelected = true;
            try {
                videoView.setVideoURI(targetUri);
                videoView.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public CascadeClassifier load_face_cascade(){
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir,"frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            CascadeClassifier face_cascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if(face_cascade.empty())
            {
                Log.e("####","--(!)Error loading A\n");
                return null;
            }
            else
            {
                Log.e("####",
                        "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                return face_cascade;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("#####", "Failed to load cascade. Exception thrown: " + e);
        }
        return null;
    }
    public CascadeClassifier load_profile_cascade(){
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_profileface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir,"profileface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            CascadeClassifier face_cascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if(face_cascade.empty())
            {
                Log.e("####","--(!)Error loading A\n");
                return null;
            }
            else
            {
                Log.e("####",
                        "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                return face_cascade;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("####", "Failed to load cascade. Exception thrown: " + e);
        }
        return null;
    }

}
