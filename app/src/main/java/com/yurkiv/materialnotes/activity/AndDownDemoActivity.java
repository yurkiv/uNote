package com.yurkiv.materialnotes.activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.TimingLogger;
import android.widget.TextView;

import com.yurkiv.materialnotes.R;


public class AndDownDemoActivity extends Activity {
    TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anddown);
        tv=(TextView)findViewById(R.id.name);
    }

}