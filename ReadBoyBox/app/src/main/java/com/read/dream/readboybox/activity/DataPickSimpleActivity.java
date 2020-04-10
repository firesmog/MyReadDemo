package com.read.dream.readboybox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.read.dream.readboybox.R;
import com.read.dream.readboybox.widget.date.DatePicker;
import com.read.dream.readboybox.widget.date.DatePickerDialogFragment;

public class DataPickSimpleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pick_simple);
        final TextView dateTv = findViewById(R.id.tv_date);
        DatePicker datePicker = findViewById(R.id.datePicker);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                    @Override
                    public void onDateChoose(int year, int month, int day) {
                        Toast.makeText(getApplicationContext(), year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
                    }
                });
                datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
            }
        });
        datePicker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                dateTv.setText(year + "-" + month + "-" + day);
            }
        });
    }

}
