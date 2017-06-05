package com.aklar.killertones;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class GenerateSound extends Activity {
	private final int duration = 1; // seconds
	private final int sampleRate = 8000;
	private final int numSamples = duration * sampleRate;
	private final double sample[] = new double[numSamples];
	private  double freqOfTone = 10; // hz

	private final byte generatedSnd[] = new byte[2 * numSamples];
	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			8000, AudioFormat.CHANNEL_OUT_DEFAULT,
			AudioFormat.ENCODING_PCM_16BIT, numSamples,
			AudioTrack.MODE_STREAM);
	Handler handler = new Handler();
	PlayThread mThread = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button playButton  = (Button) findViewById(R.id.play_button);


		View.OnClickListener buttonListener = new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				// CreateNPlay();
				if(mThread !=null)
				{  
					stopThread();
				}
				else
					CreateNPlay();
			}
		};
		playButton.setOnClickListener(buttonListener);


		SeekBar freqSeek = (SeekBar)findViewById(R.id.freqseek);
		freqSeek.setMax(20000);

		TextView curValue = (TextView) findViewById(R.id.seek_cur);
		curValue.setText("10");
		genTone();
		freqSeek.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				// TODO Auto-generated method stub
				TextView curValue = (TextView) findViewById(R.id.seek_cur);
				if(progress>10)
				{
					curValue.setText(""+progress);
					freqOfTone = progress;
				}
				else
				{
					freqOfTone = 10;
				}
				genTone();

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}});

	}


	void CreateNPlay()
	{
		startThread();
	}


	@Override
	protected void onResume() {
		super.onResume();


	}

	@Override
	protected void onStop() {
		super.onStop();


		if(mThread !=null)
		{  mThread.interrupt();
		mThread = null;
		}


	}
	void genTone(){
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (((double)sampleRate)/freqOfTone));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (double dVal : sample) {
			short val = (short) (dVal * 32767);
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	void playSound(){

		// Log.v("Generate Sound","play");
		audioTrack.flush();
		//audioTrack.f
		audioTrack.write(generatedSnd, 0, numSamples);
		
		if(audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING)
			audioTrack.play();
	}
	
	class PlayThread extends Thread {
        // Must be volatile:
        private volatile boolean stop = false;

        public void run() {
                while (!stop) {
                        //System.out.println("alive");
                        playSound();
                }
                if (stop)
                        System.out.println("Detected stop");
        }

        public synchronized void requestStop() {
                stop = true;
        }
}

public synchronized void startThread(){
        if(mThread == null){
                mThread = new PlayThread();
                mThread.start();
        }
}

public synchronized void stopThread(){
        if(mThread != null){
                mThread.requestStop();
                mThread = null;
        }
}

	
} 