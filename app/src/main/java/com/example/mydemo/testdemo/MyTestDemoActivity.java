package com.example.mydemo.testdemo;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemo.R;

public class MyTestDemoActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test_demo);
        RecyclerView rv = findViewById(R.id.rv2);

        rv.setLayoutManager(new GridLayoutManager(MyTestDemoActivity.this,3));

        GridAdapter adapter = new GridAdapter(MyTestDemoActivity.this);


        rv.setAdapter(adapter);
    }
}
