package com.lain.loadmoretest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lain.loadmoretest.base.SimpleActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(v -> {
            Intent intent = SimpleActivity.getStartIntent(MainActivity.this, Fragment1.class);
            startActivity(intent);
        });

        findViewById(R.id.btn2).setOnClickListener(v -> {
            Intent intent = SimpleActivity.getStartIntent(MainActivity.this, Fragment2.class);
            startActivity(intent);
        });

        findViewById(R.id.btn3).setOnClickListener(v ->{
            Intent intent = SimpleActivity.getStartIntent(MainActivity.this, Fragment3.class);
            startActivity(intent);
        });

        findViewById(R.id.btn4).setOnClickListener(v ->{
            Intent intent = SimpleActivity.getStartIntent(MainActivity.this, Fragment4.class);
            startActivity(intent);
        });
    }
}
