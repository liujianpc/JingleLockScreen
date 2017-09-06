package com.jingle.jinglelockscreen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LockScreenActivity extends Activity {

    private android.widget.ImageView imageview;
    private android.widget.Button closelock;
    private List<File> imageList = new ArrayList<>();
    private  ArrayList<String> imagePathList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        //截获系统锁屏
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        this.closelock = (Button) findViewById(R.id.close_lock);
        this.imageview = (ImageView) findViewById(R.id.image_view);
        closelock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE);
        Resources resources = getResources();
        int resId = resources.getIdentifier("navigation_bar_height","dimen","android");
        int naviHeight = resources.getDimensionPixelSize(resId);// get 导航栏高度
        int screenWidth = resources.getDisplayMetrics().widthPixels;//get 屏幕宽度
        int screenHeight = resources.getDisplayMetrics().heightPixels;//屏幕高度
        ViewGroup.LayoutParams layoutParams = imageview.getLayoutParams();
        layoutParams.width = screenWidth;
        layoutParams.height = screenHeight + naviHeight;//设置全屏
        initData();
        if(imagePathList != null && imagePathList.size() > 0){
            int index = (int)(Math.random()*imagePathList.size());
            Glide.with(this).load(imagePathList.get(index)).centerCrop().into(imageview);
        }
    }

    public void initData(){
        Intent intent = getIntent();
        if (intent != null){
           imagePathList = intent.getStringArrayListExtra("imagePathList");
           /* for (String imagePath: imagePathList
                 ) {
                imageList.add(new File())

            }*/
        }

    }
}
