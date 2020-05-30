package mm.hdsdownload.mk;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import java.util.EmptyStackException;


public class StreamVideo extends Activity {
    private static final String TAG_VIDURL = "video_url";
    String VideoURL;
    SimpleArcDialog dialog;
    VideoView videoview;
    private int position = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
        this.VideoURL = getIntent().getStringExtra(TAG_VIDURL);
        this.videoview = (VideoView) findViewById(R.id.streaming_video);
        this.dialog = new SimpleArcDialog(this);
        this.dialog.setConfiguration(new ArcConfiguration(this));
        this.dialog.setCancelable(false);
        this.dialog.show();
        try {
            Uri parse = Uri.parse(this.VideoURL);
            this.videoview.setMediaController(new MediaController(this));
            this.videoview.setVideoURI(parse);
        } catch (EmptyStackException e) {
            Log.e("Error", e.getMessage());
        }
        this.videoview.requestFocus();
        this.videoview.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                StreamVideo.this.dialog.dismiss();
                StreamVideo.this.videoview.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        StreamVideo.this.videoview.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        StreamVideo.this.videoview.start();
    }
}
