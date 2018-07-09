package bengkelcyber.royyandzakiy.soundtrigger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mRecorder;
    private TextView amplitudeValue;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amplitudeValue = (TextView) findViewById(R.id.amplitudeValue);
        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPermission();
        if (mRecorder == null) {
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new RecorderTask(mRecorder), 0, 500);
                try {
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("ERROR",e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    0);
        }
    }

    private void updateAmplitude(double amplitudeDb) {
        amplitudeValue.setText(String.valueOf(amplitudeDb) + " dB");
        if (amplitudeDb > 70) {
            triggered();
        } else {
            mainLayout.setBackgroundColor(Color.WHITE);
        }
    }

    private void triggered() {
        mainLayout.setBackgroundColor(Color.RED);
    }

    private class RecorderTask extends TimerTask {
        private MediaRecorder mRecorder;

        public RecorderTask(MediaRecorder mRecorder) {
            this.mRecorder = mRecorder;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int amplitude = mRecorder.getMaxAmplitude();
                    double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                    updateAmplitude(Math.round(amplitudeDb));
                }
            });
        }
    }
}