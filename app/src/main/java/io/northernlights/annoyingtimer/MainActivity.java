package io.northernlights.annoyingtimer;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Widget
    Button actionButton;
    Button resetCountdownButton;
    SeekBar seekMinutes;
    SeekBar seekSeconds;
    TextView timeTextView;
    TextView alarmSoundTextView;

    // Logic objects
    CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;

    // Application state
    private enum ApplicationState {
        STOPPED,
        STARTED,
        PAUSED,
        COMPLETED
    }

    private ApplicationState applicationState = ApplicationState.STOPPED;
    private int countdownValue;
    private int currentCountdownValue;
    private int currentAlarmSound = R.raw.foghorn;

    public void bootstrapTextViews() {
        timeTextView = findViewById(R.id.timeTextView);
        alarmSoundTextView = findViewById(R.id.alarmSoundTextView);
    }

    public void bootstrapButtons() {
        actionButton = findViewById(R.id.actionButton);
        resetCountdownButton = findViewById(R.id.resetCountdownButton);
    }

    public void bootstrapSeekBars() {
        seekMinutes = findViewById(R.id.seekMinutes);
        seekSeconds = findViewById(R.id.seekSeconds);

        int minutes = seekMinutes.getProgress();
        int seconds = seekSeconds.getProgress();

        countdownValue = minutes*60+seconds;

        drawTime(countdownValue);

        seekMinutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int seconds = seekSeconds.getProgress();
                countdownValue = i*60+seconds;

                drawTime(countdownValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minutes = seekMinutes.getProgress();
                countdownValue = minutes*60+i;

                drawTime(countdownValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void bootstrapPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, currentAlarmSound);
    }

    public void bootstrapTimer(int time) {
        countDownTimer = new CountDownTimer(time*1000, 250) {
            @Override
            public void onTick(long remainingMilliseconds) {
                currentCountdownValue = (int)Math.floor((float) remainingMilliseconds / 1000);

                drawTime(currentCountdownValue);

                if(currentCountdownValue == 0) {
                    countDownTimer.cancel();
                    blowAlarm();
                }
            }

            @Override
            public void onFinish() {
            }
        };
    }

    public void drawTime(int time) {
        int minutes = time / 60;
        int seconds = time - minutes * 60;

        String timeString = String.format(Locale.ITALY,"%02d", minutes) + ":" + String.format(Locale.ITALY,"%02d", seconds);

        timeTextView.setText(timeString);
    }

    private void blowAlarm() {
        mediaPlayer.start();

        applicationState = ApplicationState.COMPLETED;
        actionButton.setText(R.string.snooze);
        resetCountdownButton.setVisibility(View.VISIBLE);
    }

    public void actionButtonClicked(View view) {
        switch(applicationState) {
            case STOPPED:
                applicationState = ApplicationState.STARTED;
                actionButton.setText(R.string.pause);

                seekMinutes.setEnabled(false);
                seekSeconds.setEnabled(false);

                bootstrapTimer(countdownValue);
                countDownTimer.start();

                break;

            case STARTED:
                applicationState = ApplicationState.PAUSED;
                actionButton.setText(R.string.resume);

                countDownTimer.cancel();

                resetCountdownButton.setVisibility(View.VISIBLE);

                break;

            case PAUSED:
                applicationState = ApplicationState.STARTED;
                actionButton.setText(R.string.pause);

                bootstrapTimer(currentCountdownValue);
                countDownTimer.start();

                resetCountdownButton.setVisibility(View.INVISIBLE);

                break;

            case COMPLETED:
                mediaPlayer.stop();

                break;

            default:
                applicationState = ApplicationState.STOPPED;
        }
    }

    public void resetCountdownButtonClicked(View view) {
        mediaPlayer.stop();

        applicationState = ApplicationState.STOPPED;
        actionButton.setText(R.string.start);

        seekMinutes.setEnabled(true);
        seekSeconds.setEnabled(true);

        bootstrapSeekBars();
        bootstrapPlayer();

        countDownTimer.cancel();

        resetCountdownButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_foghorn_alarm:
                currentAlarmSound = R.raw.foghorn;
                alarmSoundTextView.setText(R.string.foghorn);
                bootstrapPlayer();

                return true;

            case R.id.set_roster_alarm:
                currentAlarmSound = R.raw.rooster;
                alarmSoundTextView.setText(R.string.roster);
                bootstrapPlayer();

                return true;

            case R.id.set_submarine_alarm:
                currentAlarmSound = R.raw.submarine;
                alarmSoundTextView.setText(R.string.submarine);
                bootstrapPlayer();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bootstrapTextViews();
        bootstrapButtons();
        bootstrapSeekBars();
        bootstrapPlayer();
    }
}
