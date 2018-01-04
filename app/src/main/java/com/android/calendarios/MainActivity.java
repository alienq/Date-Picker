package com.android.calendarios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.mylibrary.LibraryDialog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LibraryDialog test= findViewById(R.id.testt);

        test.setOnConfirmListener(new LibraryDialog.ConfirmListener() {
            @Override
            public void onConfirm(int year, int month, int day) {
                Log.e(TAG, "year: "+year+" month: "+month+" day: "+day );
            }
        });
    }
}
