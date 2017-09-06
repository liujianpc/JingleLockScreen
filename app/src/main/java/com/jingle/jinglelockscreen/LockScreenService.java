package com.jingle.jinglelockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

/**
 * Created by liujian on 2017/9/5.
 */

public class LockScreenService extends Service {
    private Intent startIntent;
    private MyReceiver mReceiver;
    private List<ChannelModel> channelModelList = new ArrayList<>();
    private List<File> zipFileList = new ArrayList<>();
    private List<File> imageList = new ArrayList<>();
    private ArrayList<String> imagePathList = new ArrayList<>();
    private String response;
/*
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getJson("http://servicesupport1.hicloud.com:8080/servicesupport/theme/getThemeMagazine.do?language=Chinese&themename=Balance%28magazine%29&author=Huawei+Emotion+UI&version=2.3&screen=1920*1080&phoneType=ATH-TL00H&buildNumber=ATH-TL00HC01B399&isoCode=CN&versionCode=40000");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            Model model = gson.fromJson(response, Model.class);
            if (model.getResultinfo().equals("success") && model.getChannellist() != null) {
                channelModelList = model.getChannellist();
                DLManager dlManager = DLManager.getInstance(this);
                dlManager.setDebugEnable(true);
                for (ChannelModel channelModel : channelModelList
                        ) {
                    String fileName = channelModel.getChname() + "_" + channelModel.getUpdatetime() + ".zip";
                    File zipFiletemp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);
                    if (zipFiletemp.exists() && zipFiletemp.length() > 0) {
                        zipFileList.add(zipFiletemp);
                        continue;
                    }
                    dlManager.dlStart(channelModel.getUrl(), Environment.getExternalStorageDirectory().getAbsolutePath(), fileName, new SimpleDListener() {
                        @Override
                        public void onPrepare() {
                            super.onPrepare();
                        }

                        @Override
                        public void onStart(String fileName, String realUrl, int fileLength) {
                            super.onStart(fileName, realUrl, fileLength);
                        }

                        @Override
                        public void onProgress(int progress) {
                            super.onProgress(progress);
                        }

                        @Override
                        public void onStop(int progress) {
                            super.onStop(progress);
                        }

                        @Override
                        public void onFinish(File file) {
                            super.onFinish(file);
                            zipFileList.add(file);
                            ToastUtil.showToast(getApplicationContext(), "下载完成");

                        }

                        @Override
                        public void onError(int status, String error) {
                            super.onError(status, error);
                        }
                    });

                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (zipFileList != null && zipFileList.size() > 0) {
                    getImageList();
                    for (File imageFile : imageList
                            ) {
                        imagePathList.add(imageFile.getAbsolutePath());

                    }
                }

            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_ON") || action.equals("android.intent.action.SCREEN_OF")) {
                startIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.putExtra("imagePathList", imagePathList);
                startActivity(startIntent);
            }
        }
    }

    /**
     * 根据网络地址获取json
     *
     * @param address
     * @return
     */
    public void getJson(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = client.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        response = EntityUtils.toString(entity, "utf-8");
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("exception", "json解析错误");
                }
            }
        }).start();

    }

    /**
     * 解压所有zip文件，并将图片加入到list
     */

    public void getImageList() {
        for (File zipFile : zipFileList
                ) {
            imageList.add(unZipFile(zipFile));
        }
    }

    /**
     * 将文件zip解压到对应名称的文件夹
     *
     * @param file
     * @return
     */

    public File unZipFile(File file) {
        File imageFile = null;
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String imageDirPath = sdPath + "/" + file.getAbsolutePath().replaceAll(".zip", "");
        File imageDir = new File(imageDirPath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        try {
            ZipFile zipFile = new ZipFile(file);
            InputStream inputStream = null;
            Enumeration enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                imageFile = new File(imageDirPath + "/" + zipEntry.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read()) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    fileOutputStream.flush();
                }
                fileOutputStream.close();

            }
            inputStream.close();
          /*  FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry zipEntry;
            String zipName;
            while ((zipEntry = zipIs.getNextEntry()) != null) {
                zipName = zipEntry.getName();
                imageFile = new File(imageDirPath + File.pathSeparator + zipName);
                imageFile.createNewFile();
                FileOutputStream out = new FileOutputStream(imageFile);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = zipIs.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
             *//*   int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();*//*

            }
            zipIs.close();*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }

}