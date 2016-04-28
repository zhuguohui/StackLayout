package com.zhuguohui.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.zhuguohui.learn.adapter.ImageAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private StackLayout gallery;
    int [] rid=new int[]{R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5};
    ImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery= (StackLayout) findViewById(R.id.gallery);
        adapter=new ImageAdapter(this,rid);
        gallery.setAdapter(adapter);
        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_btns);
        int count = layout.getChildCount();
        for(int i=0;i<count;i++){
            layout.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                gallery.takeOff(true,true);
                break;
            case R.id.btn2:
                gallery.takeOff(true,false);
                break;
            case R.id.btn3:
                gallery.takeOff(false,true);
                break;
            case R.id.btn4:
                gallery.takeOff(false,false);
                break;
        }

    }
}
