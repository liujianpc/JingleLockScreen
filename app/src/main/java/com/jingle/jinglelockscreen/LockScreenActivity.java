package com.jingle.jinglelockscreen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LockScreenActivity extends Activity {

    private android.widget.ImageView imageview;
    private android.widget.ImageButton closelock;
    //private List<File> imageList = new ArrayList<>();
    private ArrayList<String> imagePathList;
    int screenWidth_other;
    int screenHeight_other;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        //截获系统锁屏
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        this.closelock = (ImageButton) findViewById(R.id.close_lock);
        this.imageview = (ImageView) findViewById(R.id.image_view);
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        closelock.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int firstX, firstY;
            int totalDx, totalDy;
            int firstViewLfet, firstViewRight, firstViewTop, firstViewBottom;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                switch (eventAction) {
                    //按下时的绝对位置
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        firstX = lastX;
                        firstY = lastY;
                        firstViewLfet = v.getLeft();
                        firstViewRight = v.getRight();
                        firstViewTop = v.getTop();
                        firstViewBottom = v.getBottom();

                        screenWidth_other = v.getWidth();
                        screenHeight_other = v.getHeight();
                        break;
                    //移动时的绝对位置
                    case MotionEvent.ACTION_MOVE:
                        //移动的位移差值
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        //相对于parent 的View上下左右位置
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
/*

                        //如果left < 0，则是左移，右边框上次位置加上左移部分
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        //
                        if (right > screenWidth_other) {
                            right = screenWidth_other;
                            left = right - v.getWidth();
                        }

                        //如果top < 0，则是上移，下边框上次位置加上移部分
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }

                        if (bottom > screenHeight_other) {
                            bottom = screenHeight_other;
                            top = bottom - v.getHeight();
                        }
*/

                        //重新layout
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        totalDx = Math.abs((int) event.getRawX() - firstX);
                        totalDy = Math.abs((int) event.getRawY() - firstY);
                        v.layout(left, top, right, bottom);
                        if (totalDx >= 400 || totalDy >= 400) {
                            finish();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (totalDx >= 400 || totalDy >= 400) {
                            finish();
                        } else {
                            v.layout(firstViewLfet, firstViewTop, firstViewRight, firstViewBottom);
                        }
                        break;
                }

                return false;
            }
        });
        /*closelock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE */ | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Resources resources = getResources();
        int resId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int naviHeight = resources.getDimensionPixelSize(resId);// get 导航栏高度
        // int screenWidth = resources.getDisplayMetrics().widthPixels;//get 屏幕宽度
        //  int screenHeight = resources.getDisplayMetrics().heightPixels;//屏幕高度
        ViewGroup.LayoutParams layoutParams = imageview.getLayoutParams();
        layoutParams.width = screenWidth;
        layoutParams.height = screenHeight + naviHeight;//设置全屏
        initData();
        if (imagePathList != null && imagePathList.size() > 0) {
            int index = (int) (Math.random() * imagePathList.size());
            Glide.with(this).load(imagePathList.get(index)).centerCrop().into(imageview);
        }
    }

    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            imagePathList = intent.getStringArrayListExtra("imagePathList");
           /* for (String imagePath: imagePathList
                 ) {
                imageList.add(new File())

            }*/
        }

    }
}
