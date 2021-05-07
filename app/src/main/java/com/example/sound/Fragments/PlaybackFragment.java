package com.example.sound.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.sound.Models.RecordingItem;
import com.example.sound.R;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaybackFragment extends DialogFragment
{

    private RecordingItem item;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

    long minutes = 0;
    long seconds = 0;

    @BindView(R.id.file_name_text_view) TextView fileNameTxtView;
    @BindView(R.id.file_length_text_view) TextView fileLengthTxtView;
    @BindView(R.id.current_progress_text_view) TextView fileCurrentProgress;
    @BindView(R.id.seekbar) SeekBar seekBar;
    @BindView(R.id.fab_play) FloatingActionButton floatingActionButton;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (RecordingItem)getArguments().getSerializable("item");

        minutes = TimeUnit.MILLISECONDS.toMinutes(item.getLength());
        seconds = TimeUnit.MILLISECONDS.toSeconds(item.getLength()) - TimeUnit.MINUTES.toSeconds(minutes);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_playback,null);
        ButterKnife.bind(this,view);


        setSeekbarValue();
        
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onPlay(isPlaying);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = !isPlaying;
            }
        });
        
        fileNameTxtView.setText(item.getName());
        fileLengthTxtView.setText(String.format("%02d:%02d",minutes,seconds));

        builder.setView(view);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    private void onPlay(boolean isPlaying) throws IOException {
        if(!isPlaying)
        {
            if (mediaPlayer == null)
            {
                startPlaying();
            }
        }
        else
        {
            pausePlaying();
        }
    }

    private void pausePlaying()
    {
        floatingActionButton.setImageResource(R.drawable.ic_media_play);
        handler.removeCallbacks(mRunnable);
        mediaPlayer.pause();
    }

    private void startPlaying() throws IOException {
        floatingActionButton.setImageResource(R.drawable.ic_media_pause);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setDataSource(item.getPath());
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setSeekbarValue() {
        ColorFilter colorFilter = new LightingColorFilter(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary));

        seekBar.getProgressDrawable().setColorFilter(colorFilter);
        seekBar.getThumb().setColorFilter(colorFilter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(mRunnable);
                    
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()
                    - TimeUnit.MINUTES.toSeconds(minutes));
                    
                    fileCurrentProgress.setText(String.format("%02d:%02d:%02d",minutes,seconds));
                    
                    updateSeekBar();
                }
                else if (mediaPlayer == null && fromUser)
                {
                    try {
                        prepareMediaPlayerFromPoint(progress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void prepareMediaPlayerFromPoint(int progress) throws IOException {
        mediaPlayer = new MediaPlayer();
        
        mediaPlayer.setDataSource(item.getPath());
        mediaPlayer.prepare();
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.seekTo(progress);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
    }

    private void stopPlaying() 
    {
        floatingActionButton.setImageResource(R.drawable.ic_media_play);
        handler.removeCallbacks(mRunnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        
        seekBar.setProgress(seekBar.getMax());
        isPlaying = !isPlaying;
        
        fileCurrentProgress.setText(fileLengthTxtView.getText());
        seekBar.setProgress(seekBar.getMax());
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null)
            {
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                
                fileCurrentProgress.setText(String.format("%02d:%02d",minutes,seconds));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() 
    {
        handler.postDelayed(mRunnable,1000);
    }
}
