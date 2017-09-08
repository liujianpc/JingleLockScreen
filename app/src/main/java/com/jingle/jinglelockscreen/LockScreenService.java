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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private static ArrayList<String> imagePathList = new ArrayList<>();
    private static HashMap<String, TitleAndContent> titleAndContentHashMap = new HashMap<>();
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
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 8 || hour == 12) {
            getImageListByNetDownload();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void getImageListByNetDownload() {
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
                            // ToastUtil.showToast(getApplicationContext(), "下载完成");

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
    }

    class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour == 8 || hour == 12) {
                getImageListByNetDownload();
            }
            getImageListByDir();
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_ON") || action.equals("android.intent.action.SCREEN_OF")) {
                startIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.putExtra("imagePathList", imagePathList);
                startIntent.putExtra("titleAndContentMap", titleAndContentHashMap);
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
            unZipFile(zipFile);
        }
    }

    /**
     * 将文件zip解压到对应名称的文件夹
     *
     * @param file
     * @return
     */

    public void unZipFile(final File file) {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String imageDirPath = sdPath + "/" + file.getName().replaceAll(".zip", "");
        File imageDir = new File(imageDirPath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                   /* ZipFile zipFile = new ZipFile(file);
                    InputStream inputStream = null;
                    Enumeration enumeration = zipFile.entries();
                    while (enumeration.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                        inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                        File imageFile = new File(imageDirPath + "/" + zipEntry.getName());
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = inputStream.read()) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                            fileOutputStream.flush();
                        }
                        fileOutputStream.close();
                        imageList.add(imageFile);
                    }
                    inputStream.close();*/
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(fis));
                    ZipEntry zipEntry;
                    String zipName;
                    while ((zipEntry = zipIs.getNextEntry()) != null) {
                        zipName = zipEntry.getName();
                        File imageFile = new File(imageDirPath + "/" + zipName);
                        imageFile.createNewFile();
                        FileOutputStream out = new FileOutputStream(imageFile);
                        int len;
                        byte[] buffer = new byte[4096];
                        while ((len = zipIs.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();
             /*   int len;
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
        inZip.close();*/
                        if (zipName.contains(".jpg") || zipName.contains(".jpeg")) {
                            imageList.add(imageFile);
                        } else if (zipName.contains(".xml")) {
                            getTittleAndContentMap(imageFile);
                        }
                    }
                    zipIs.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void getImageListByDir() {
        File sdPath = Environment.getExternalStorageDirectory();
        //查找文件夹
        File[] files = sdPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!name.contains(".zip") && (name.contains("体育") || name.contains("生活") || name.contains("汽车") || name.contains("明星") || name.contains("时尚") || name.contains("旅行"))) {
                    return true;
                }
                return false;
            }
        });
        //查找文件夹里面的图片
        for (File file : files
                ) {
            File[] files_inDir = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains("type") || name.contains(".xml")) {
                        return false;
                    }
                    return true;
                }
            });
            for (File imageFile : files_inDir
                    ) {
                imagePathList.add(imageFile.getAbsolutePath());
            }
            File[] xmlFiles = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains("xml")) {
                        return true;
                    }
                    return false;
                }
            });
            getTittleAndContentMap(xmlFiles[0]);
        }
    }

    public void getTittleAndContentMap(File xmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("image");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                titleAndContentHashMap.put(element.getAttribute("src"), new TitleAndContent(element.getAttribute("title"), element.getAttribute("content")));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
