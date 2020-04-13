package com.read.dream.readboybox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.read.dream.readboybox.R;
import com.read.dream.readboybox.widget.date.DatePicker;
import com.read.dream.readboybox.widget.date.DatePickerDialogFragment;
import com.read.dream.readboybox.widget.timeSelect.TimeSelectorView;

import java.util.List;

public class DataPickSimpleActivity extends AppCompatActivity {

    private TimeSelectorView tsvTimeSelector;

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

        this.tsvTimeSelector = (TimeSelectorView) findViewById(R.id.tsvTimeSelector);

        tsvTimeSelector.setOnChangeTimeListener(new TimeSelectorView.OnChangeTimeListener() {
            @Override
            public void onChangeTime(boolean isSelect, List<Integer> seletedList) {
                Log.e("xxx", "当前是选中？：" + isSelect + "    集合中选中的有：" + seletedList.toString());
            }
        });
    }

}
