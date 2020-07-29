package orritt.browning.blurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.FileNotFoundException;

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

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    TextView text;
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button)findViewById(R.id.button);
        videoView = (VideoView)findViewById(R.id.videoView);
        text = (TextView)findViewById(R.id.textView);
        button.setVisibility(View.INVISIBLE);

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

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            videoPath = targetUri.toString();
            Toast.makeText(getApplicationContext(), videoPath, Toast.LENGTH_SHORT).show();
            try {
                videoView.setVideoURI(targetUri);
                videoView.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
