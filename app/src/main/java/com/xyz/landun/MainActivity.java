package com.xyz.landun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private MyView myView;
    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myView = findViewById(R.id.my_view);
        textView1 = findViewById(R.id.text1);
        SeekBar seekBarX = findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(listener1);
        SeekBar seekBarWidth = findViewById(R.id.seekBar2);
        textView2 = findViewById(R.id.text2);
        seekBarWidth.setOnSeekBarChangeListener(listener2);
        findViewById(R.id.auto).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.auto) {
            myView.autoNext();
        } else if (id == R.id.stop) {
            myView.stop();
        } else if (id == R.id.next) {
            myView.goNext();
        } else if (id == R.id.reset) {
            myView.reset();
        }
    }

    SeekBar.OnSeekBarChangeListener listener1 = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            myView.setDelayMillis(progress);
            textView1.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener listener2 = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            myView.setSingleGridWidth(progress);
            textView2.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


}