package com.jingle.jinglelockscreen;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUSET_PERMISSION_CODE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, LockScreenService.class);
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) == PackageManager.PERMISSION_GRANTED){
        startService(intent);
        finish();
       /* }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission_group.STORAGE},REQUSET_PERMISSION_CODE);
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUSET_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
                finish();
            } else {
                ToastUtil.showToast(MainActivity.this, "没有存储授权！");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true).setTitle("错误提示").setMessage("前往设置存储权限").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(/*Settings.ACTION_APPLICATION_SETTINGS*/);//"com.huawei.permissionmanager.ui","com.huawei.permissionmanager.ui.SingleAppActivity"
                            ComponentName cm = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
                            intent.setComponent(cm);
                            intent.setAction("android.intent.action.VIEW");
                        startActivity(intent);
                    }
                }).create();
                builder.show();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
