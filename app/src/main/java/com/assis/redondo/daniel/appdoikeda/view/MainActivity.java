package com.assis.redondo.daniel.appdoikeda.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.data.DataController;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        if(DataController.getInstance(this).getFrameModels() == null){
            DataController.getInstance(this).loadFramesAndInsumos();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent newIntent = new Intent(MainActivity.this, CalculatorActivity.class);
                MainActivity.this.startActivity(newIntent);
                MainActivity.this.finish();
            }
        },1500);


    }

}
