package top.giftedcc.features;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by chang on 2018/11/24.
 * Class:  测量噪声，获取必要信息的类
 */

public class UploaddBData extends Service {

    private AudioRecord audioRecord = null;
    private LocationManager locationmanager = null;
    private LocationListener loctListener = null;
    private PlaceParseHelper placeparsehelper = null;
    private Location dlocation = null;// 用户目前位置
    private String phonemodel = "";// 用户手机型号
    private String IMEI = "";// 手机IMEI值
    private int bufferSize = 100;// 存麦克风数据的数组大小
    private static final int DURATION = 60;// 单位时间：60秒
    private int settingtimes = 1;// 测量周期
    private static final int PER_LENGTH = 55;// 单位长度，55米之内视为同一地点
    private long timeperiod = 1;
    private long number = 0;
    private long totalvalue = 1;
    private long timecount;// 上传数据的所用时间
    private int haveStarted = 0;// 是否是第一次启动服务
    private Boolean isCall = false;// 是否接收到打电话的broadcast
    private Boolean isTrue = true;// 是否进行测量
    private Boolean isUpload = false;// 是否正在上传数据
    // private boolean isLocationChange = false;// 是否在同一地点

    private String locationmsg = "";
    // private String statement = "";
    // private String avgdB = "";
    private StringBuilder detaildata = new StringBuilder();

    private ArrayList<Integer> results = new ArrayList<>();// 详细数据
    private ArrayList<HashMap<String, Object>> datalist = new ArrayList<>();// 上传的数据行
    private Handler myhandler = null;
    private static final String url4 = "http://115.157.200.85:8080/ServerDataBase/servlet/StoreDatatoServer";// IPv4
    private static final String url6 = "http://[2001:250:4402:2002:115:157:200:85]:8080/ServerDataBase/servlet/StoreDatatoServer";// IPv6

    // private static final String url0 =
    // "http://10.63.255.86:8080/ServerDataBase/servlet/StoreDatatoServer";// 本地
    // aliyun服务器路径"http://120.27.107.206:8080/ServerDataBase/servlet/StoreDatatoServer"

    @Override
    public void onCreate() {
        super.onCreate();
        // System.out.println("Service onCreate + isTrue=" + isTrue);
        Toast.makeText(UploaddBData.this, "Service start", Toast.LENGTH_SHORT)
                .show();


        // 实现来电状态监听

        TelephonyManager telMgr = (TelephonyManager) UploaddBData.this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telMgr.listen(new TelListener(), PhoneStateListener.LISTEN_CALL_STATE);

        locationmanager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        loctListener = new getLocationListener();
        placeparsehelper = new PlaceParseHelper();

        // 获取GPS坐标并显示
        // System.out.println("isLocationChange-->1" + isLocationChange);
        String provider = getLocation();
        getLoactionUpdate(provider);
        // test_location.setText(locationmsg);
        // testresult.setText(statement + avgdB);
        // System.out.println("isLocationChange-->2" + isLocationChange);

        phonemodel = android.os.Build.MODEL;
        if (phonemodel == null) {
            phonemodel = "";
        }
        // System.out.println("phone-->" + phonemodel);
        IMEI = telMgr.getDeviceId();
        if (IMEI == null) {
            IMEI = "";
        }
        System.out.println("IMEI-->" + IMEI);

        // 初始化日志配置
        LogToFile.init(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Toast.makeText(UploaddBData.this, "Service stop", Toast.LENGTH_SHORT)
                .show();
        isTrue = false;
        locationmanager.removeUpdates(loctListener);
        haveStarted = 0;
        results.clear();
        datalist.clear();
        results = null;
        datalist = null;
        bufferSize = 100;
        audioRecord.stop();
        audioRecord.release();
        locationmanager = null;
        placeparsehelper = null;
        myhandler = null;
        // System.out.println("Service onDestroy + isTrue=" + isTrue);
        android.os.Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 启动一次服务，开始测量噪声的线程wktt
        haveStarted = haveStarted + 1;

        if (haveStarted == 1) {
            isTrue = true;
            // System.out.println("Service onStartCommand + isTrue=" + isTrue
            // + "haveStarted=" + haveStarted);
            WorkThread wktt = new WorkThread();
            wktt.start();

            // 开启线程上传数据
            myhandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    // System.out.println("upload handler");
                    if (!isUpload) {
                        isUpload = true;// 开始上传数据。锁上isUpload,保证最多只有一个UpThread线程在工作
                        new UpThread(datalist).start();
                    }
                }
            };
        }

        // 接收Activity发过来的Intent（菜单按钮触发）
        if (intent != null) {
            Bundle e = intent.getExtras();

            if (e != null) {
                if (e.containsKey("menu")) {
                    int menuId = e.getInt("menu");
                    switch (menuId) {
                        case 0:
                            getLoactionUpdate(getLocation());
                            break;
                        case 1:
                            if (settingtimes == 5) {
                                settingtimes = 1;
                                resetSystem();
                                Toast.makeText(getApplicationContext(),
                                        "切换至每1分钟统计一次平均值。", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case 2:
                            if (settingtimes == 1) {
                                settingtimes = 5;
                                resetSystem();
                                Toast.makeText(getApplicationContext(),
                                        "切换至每5分钟统计一次平均值。", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case 4:
                            if (!isUpload) {
                                isUpload = true;// 开始上传数据。锁上isupload,保证最多只有一个UpThread线程在工作
                                new UpThread(datalist).start();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "系统繁忙，请重试。", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 5:
                            new PerformanceTestingThread().start();
                            break;
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 工作线程，采集信息
    private class WorkThread extends Thread {

        public WorkThread() {
            int SAMPLE_RATE_IN_HZ = 44100;
            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        }

        @Override
        public void run() {

            try {

                // System.out.println("WorkThread run.");
                audioRecord.startRecording(); // 开始监听声音
                isTrue = true;
                // 用于读取的 buffer
                short[] buffer = new short[bufferSize];

                int cou = 0;
                while (isTrue) {
                    if (!isCall) {
                        int dB = 0;
                        while (isTrue) {
                            ++number;
                            // System.out.println("number=" + number);
                            // sleep(5);

                            long timecurrent = System.currentTimeMillis();  // 开始测量时间

                            long r = audioRecord.read(buffer, 0, bufferSize);

                            long v = 0;
                            for (int i = 0; i < bufferSize; i++) {
                                v += (buffer[i] * buffer[i]);
                            }
                            if (r != 0) {
                                long value = Math.abs(v / r);
                                totalvalue += value;
                            }

                            long endtime = System.currentTimeMillis();  // 结束测量时间
                            timeperiod = timeperiod + (endtime - timecurrent); // 间隔时间

                            if (timeperiod >= 960) {
                                totalvalue = totalvalue / number;
                                dB = (int) (10 * Math.log10(totalvalue)); // 分贝
                                cou = cou % (DURATION * settingtimes);// 次数

                                // 重新获取GPS的时候该分钟重新开始测量
                                if (cou != results.size()) {
                                    cou = results.size();
                                }

                                ++cou;
                                // ++count;
                                timeperiod = 1;
                                number = 0;
                                totalvalue = 1;
                                buffer = null;
                                buffer = new short[bufferSize];
                                // sleep(10);
                                break;
                            }

                        }

                        // 发送广播
                        Intent intent = new Intent();
                        intent.setAction("Info.update.Broadcast");
                        // 实时显示噪声分贝
                        String currentdBs = "当前分贝: " + dB
                                + "dB";
                        // 显示当前时间
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                "HH:mm:ss", Locale.CHINA);// update
                        String dcurrent = simpleDateFormat.format(date);
                        String time_current = "当前时间: " + dcurrent;

                        // 获取工作线程的测量结果
                        if ((results != null) && (cou != 0)) {
                            results.add(dB);

                            // System.out.println(" dB: " + dB + "cou: " + cou);

                            detaildata.append("  第").append(cou).append("秒:   ").append(
                                    results.get(cou - 1)).append("dB  ");
                        }
                        if (cou % 3 == 0) { // 每行显示三个数据
                            detaildata.append("\n");
                        }
                        // 每10秒钟打印一次详细数据表
                        if (cou % 10 == 0) {
                            intent.putExtra("detaildata", detaildata.toString());// 详细数据表
                            detaildata.setLength(0); // 清空Stringbuilder
                        }

                        // 每一次测量的起点位置是第3秒，终点是第60秒。前后GPS不变认为是同一地点
                        if (cou % (DURATION * settingtimes) == 3) {
                            getLocation();// 获取起点位置dlocationStart
                            // isLocationChange = false;
                            // System.out.println("isLocationChange 2 = "+isLocationChange);
                        }


                        if ((dlocation != null) // 有打开GPS，能获取到位置信息
                                && (dB > 0) && (isInHNU())) { // 在HNU范围之内，分贝值不为0
                            Double dlongitude = dlocation.getLongitude();
                            Double dlatitude = dlocation.getLatitude();

                            // 显示面板更新
                            /*
                            avgdB = "\n经度：" + dlongitude + "  纬度："
                                    + dlatitude + "  噪声：" + dB
                                    + "dB";

                            intent.putExtra("avgdB", avgdB);// 采集表
                            */
                            // 把数据存储到datalist
                            HashMap<String, Object> map1 = new HashMap<>();
                            map1.put("longitude", dlongitude);
                            map1.put("latitude", dlatitude);
                            map1.put("currenttime", dcurrent);
                            map1.put("dBavg", dB);
                            datalist.add(map1);
                            //LogToFile.d(dcurrent,dB + "dB");
                        }

                        System.out.println("datalist.size = "
                                + datalist.size());

                        if ((datalist.size() > 0)
                                && ((datalist.size() % 100) == 0)) { // 当手机缓存数据行达到100条的倍数时候自动上传服务器
                            myhandler.sendEmptyMessage(0);
                        }


                        intent.putExtra("currentdBs", currentdBs);// 实时分贝值
                        intent.putExtra("time_current", time_current);// 时间
                        intent.putExtra("locationmsg", locationmsg);// 地点信息

                        sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /*
         * 判断是否在HNU范围内，在范围内的数据才会可能是有效数据上传
         * 112.933331 28.165421 十进制度，↙
         * 112.933331 28.185870 十进制度，↖
         * 112.948435 28.185870 十进制度，↗
         * 112.948435 28.165421 十进制度，↘
         */
        private boolean isInHNU() {
            if ((dlocation.getLongitude() > 112.933331d)
                    && (dlocation.getLongitude() < 112.948435d)
                    && (dlocation.getLatitude() > 28.165421d)
                    && (dlocation.getLatitude() < 28.185870d)) {
                return true;
            }
            return false;
        }
    }

    /*
     * 监听是否有电话打进来，监听打电话状态发生的变化
     */
    private class TelListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 没有在打电话
                    System.out.println("NO Call.");
                    isCall = false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:// 电话来电铃声响起
                    System.out.println("Ringing.");
                    isCall = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接电话
                    System.out.println("Hold on.");
                    isCall = true;
                    break;
            }
        }

    }

    /*
     * 获取当前GPS坐标
     */
    private String getLocation() {
        // System.out.println("getLocation");
        String provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Location location = locationmanager.getLastKnownLocation(provider);

        System.out.println("Provider: "
                + locationmanager.getAllProviders().toString());
        Criteria cri = new Criteria();
        cri.setAltitudeRequired(false);
        cri.setSpeedRequired(false);
        System.out.println("Best Provider: "
                + locationmanager.getBestProvider(cri, true));

        if (location != null) {
            getLocationFoo(location, provider);
        } else {
            provider = LocationManager.NETWORK_PROVIDER;
            location = locationmanager.getLastKnownLocation(provider);
            if (location != null) {
                getLocationFoo(location, provider);
            } else {
                locationmsg = "Make Sure Your GPS/NETWORK is Available.";
            }
        }
        return provider;
    }

    private void getLocationFoo(Location location, String provider) {
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        dlocation = location;
        int placeId = placeparsehelper.getPlaceIdByPlaceInfo(lng, lat);
        if (placeId < 0) {
            locationmsg = "当前位置:经度:" + lng
                    + "\n　　　　 纬度:" + lat;
        } else {
            locationmsg = "当前位置:经度:" + lng
                    + "\n　　　　 纬度:" + lat;
        }
    }

    /*
     * 实时获取最新GPS坐标
     */
    private void getLoactionUpdate(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationmanager.requestLocationUpdates(provider, 5000, PER_LENGTH,
                loctListener);
    }

    /*
     * 监听器，监听当前位置变化和provider变化，并更新UI
     */
    class getLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // isLocationChange = true;
            getLastGPSAndReset();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            String providerStatus = "";
            switch (status) {
                // GPS状态为可用时
                case LocationProvider.AVAILABLE:
                    locationmsg = "GPS/NETWORK Statue Changed.\n" + provider
                            + " is " + providerStatus
                            + "AVAILABLE. Please Refresh.";
                    getLocation(); // 更新界面
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    // isLocationChange = true; // 位置或provider改变导致本数据无效
                    locationmsg = "GPS/NETWORK Statue Changed.\n" + provider
                            + " is " + providerStatus
                            + "OUT_OF_SERVICE. Please Check.";
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    // isLocationChange = true; // 位置或provider改变导致本数据无效
                    locationmsg = "GPS/NETWORK Statue Changed.\n" + provider
                            + " is " + providerStatus
                            + "TEMPORARILY_UNAVAILABLE. Please Check.";
                    break;
            }

            // System.out.println("onStatusChanged");
            // test_location.setText(locationmsg);
        }

        @Override
        public void onProviderEnabled(String provider) {
            locationmsg = "GPS/NETWORK Statue Enabled,Please Refresh.\n"
                    + "GPSProvider:" + provider;
            // System.out.println("onProviderEnabled");
            // isLocationChange = true; // 位置或provider改变导致本数据无效
            getLastGPSAndReset(); // 重新开始一分钟计时测量
            // test_location.setText(locationmsg);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // isLocationChange = true; // 位置或provider改变导致本数据无效
            locationmsg = "GPS/NETWORK: " + provider
                    + " Statue Disabled,Please Check.";
            // System.out.println("onProviderDisabled");
            // test_location.setText(locationmsg);
        }

    }

    /*
     * 线程：上传数据
     */
    class UpThread extends Thread {

        private ArrayList<HashMap<String, Object>> tempdatalist = new ArrayList<>();

        public UpThread(ArrayList<HashMap<String, Object>> temp) {
            super();
            this.tempdatalist.addAll(temp);
        }

        /*
         * 用于上传数据的线程，主要算法
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            super.run();

            int size = tempdatalist.size();// 数据行的数量
            if (size > 0) {
                Looper.prepare();
                // 准备阶段
                long timecurrent1 = System.currentTimeMillis(); // 开始上传数据的时间点
                // System.out.println("上传开始时间:" + timecurrent1);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                NameValuePair pair0 = new BasicNameValuePair("size", "" + size);
                pairs.add(pair0);
                for (int i = 1; i <= size; i++) {
                    Double longitude = (Double) tempdatalist.get(i - 1).get(
                            "longitude");
                    NameValuePair pair1 = new BasicNameValuePair("longitude"
                            + i, longitude.toString());

                    Double latitude = (Double) tempdatalist.get(i - 1).get(
                            "latitude");
                    NameValuePair pair2 = new BasicNameValuePair(
                            "latitude" + i, latitude.toString());

                    String currenttime = (String) tempdatalist.get(i - 1).get(
                            "currenttime");
                    NameValuePair pair3 = new BasicNameValuePair("currenttime"
                            + i, currenttime);

                    Date date3 = new Date();
                    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", Locale.CHINA);// update
                    String uploadtime = simpleDateFormat3.format(date3);
                    NameValuePair pair4 = new BasicNameValuePair("uploadtime"
                            + i, "" + uploadtime);

                    int dBavg = (Integer) tempdatalist.get(i - 1).get("dBavg");
                    NameValuePair pair5 = new BasicNameValuePair("dBavg" + i,
                            "" + dBavg);

                    pairs.add(pair1);// 经度
                    pairs.add(pair2);// 纬度
                    pairs.add(pair3);// 测量时间
                    pairs.add(pair4);// 上传时间
                    pairs.add(pair5);// 噪声分贝
                }
                NameValuePair pair7 = new BasicNameValuePair("phonemodel",
                        phonemodel);// 手机型号
                pairs.add(pair7);
                NameValuePair pair8 = new BasicNameValuePair("IMEI", IMEI);
                pairs.add(pair8);
                // System.out.println("pairs--->" + pairs.toString());

                // 上传阶段
                // System.out.println("before---upload");
                HTTP_Post http_Post = new HTTP_Post();
                // 上传数据开始
                // 默认IPv6方式
                NameValuePair pair9 = new BasicNameValuePair("protocol", "IPv6");
                pairs.add(pair9);
                ArrayList<String> resList = (ArrayList<String>) http_Post.uploadDataByHttp_Post(url6, pairs);// 返回值，0是失败，size是成功，表示上传的数量
                int res = 0; // 服务器收到的数据条数
                if (resList != null && resList.size() != 0) {// IPv6成功
                    //res = 6;
                    // System.out.println("after---upload. IPv6");
                    res = Integer.parseInt(resList.get(0));
                    Toast.makeText(getApplicationContext(),
                            "IPv6方式成功上传" + res + "条数据", Toast.LENGTH_LONG).show();

                } else {// IPv6失败，自动转IPv4传输
                    pairs.remove(pair9);
                    NameValuePair pair10 = new BasicNameValuePair("protocol",
                            "IPv4");
                    pairs.add(pair10);
                    resList = (ArrayList<String>) http_Post.uploadDataByHttp_Post(url4, pairs);// 返回值，0是失败，size是成功，表示上传的数量
                    //res = Integer.parseInt(resList.get(0));
                    if (resList != null && resList.size() != 0) {
                        //res = 4;
                        // System.out.println("after---upload. IPv4");
                        // Looper.prepare();
                        res = Integer.parseInt(resList.get(0));
                        Toast.makeText(
                                getApplicationContext(),
                                "IPv4方式成功上传"
                                        + res
                                        + "条数据\n\nWarning\n由于您未连接无线HNU网络或由于网络故障，本次上传已自动为您转为通过IPv4协议传输！请及时检查网络。",
                                Toast.LENGTH_LONG).show();
                        // Looper.loop();
                    } else {
                        res = 0;
                        // System.out.println("upload Fail.");
                        // Looper.prepare();
                        Toast.makeText(getApplicationContext(),
                                "上传失败\n请确保已连接HNU无线网络", Toast.LENGTH_LONG)
                                .show();
                        // Looper.loop();
                    }
                }
                tempdatalist.clear();
                tempdatalist = null;
                // System.out.println("after---upload. res = " + res);
                long timecurrent2 = System.currentTimeMillis(); // 结束上传数据的时间点
                // System.out.println("上传结束时间:" + timecurrent2);

                ArrayList<HashMap<String, Object>> lastFailList = null;
                if (res < size && resList != null) {  // 服务器没有完全接收app上传的数据
                    String[] failId = resList.get(1).split(",");
                    int index = 0;
                    lastFailList = new ArrayList<>(size - res);
                    for (int i = 0; i < datalist.size(); i++) {
                        if ((i + 1) == Integer.parseInt(failId[index])) {
                            lastFailList.add(datalist.get(i));
                            index++;
                        }
                    }
                }
                // 上传成功时删除已上传数据行
                if (res > 0) { // 最近一次上传成功时时间消耗
                    timecount = (timecurrent2 - timecurrent1 - 2000) / size;
                    // System.out.println("timecount-------->" + timecount);//
                    // 158--1
                    // 233--10
                    // 上传过程中没有新增数据，删除已上传数据行
                    int datalist_size = datalist.size();
                    if (size == datalist_size) {
                        datalist.clear();
                    }
                    // 上传成功时和刚上传时相比，在上传的过程手机缓存了更多的数据行，则删除已上传数据行，保存新缓存未上传的数据行
                    else if (size < datalist_size) {
                        ArrayList<HashMap<String, Object>> temp =
                                new ArrayList<>(datalist.subList(size, datalist_size));
                        datalist.clear();
                        datalist.addAll(temp);
                        temp = null;
                    }

                    if (lastFailList != null && lastFailList.size() != 0) {
                        datalist.addAll(lastFailList);
                    }
                }
                isUpload = false;// 上传结束，锁打开
                Looper.loop();
            } else {
                isUpload = false;// 没有数据上传，锁打开
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "您还未测量新数据，请先测量！",
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }
    }

    /*
     * 重新获取最新GPS坐标的之后，该分钟的噪声测量重新开始，之前本分钟测量的数据删除，即从最新的位置重新测量分贝值。
     */
    private void getLastGPSAndReset() {
        if (results != null) {
            results.clear();
        }
        detaildata.append("\n");
    }

    /*
     * 重置数据，重新开始测量记录
     */
    private void resetSystem() {
        timeperiod = 1;
        number = 0;
        totalvalue = 1;
        results.clear();
        isTrue = true;
        detaildata.append("详细数据表(每10秒钟刷新一次)\n");
        // testresultsec.setText(detaildata);

        // statement = "当前设置是：\n每1秒钟测试一次实时分贝值。\n每" + settingtimes + "分钟统计一次平均分贝值。";
        // avgdB = "\n\n采集数据表(每" + settingtimes + "分钟刷新一次)";
        // test_location.setText(locationmsg);
        // testresult.setText(statement + avgdB);
    }

    // 获得CPU信息
    private String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            Log.e("getCpuInfo", "getCpuInfo occur error");
        }
        return "CPU型号:" + cpuInfo[0] /*+ "\nCPU频率:" + cpuInfo[1] +*/;
    }


    // 由系统总CPU使用时间和应用占用的CPU时间计算CPU利用率
    private static float getProcessCpuRate() {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(500);

        } catch (Exception e) {
            Log.e("getProcessCpuRate", "getProcessCpuRate occur error");
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        return (float) 8 * 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
    }

    private static long getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
    }

    private static long getAppCpuTime() { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
    }

    class PerformanceTestingThread extends Thread {

        @Override
        public void run() {
            super.run();
            // CPU信息
            String CPUInfo = getCpuInfo();
            // System.out.println(CPUInfo);

            // CPU利用率
            String CPURate = "";
            if ((getProcessCpuRate() - 0.01) > 0) {
                CPURate = "CPU利用率:" + getProcessCpuRate() + "%";
                // System.out.println(CPURate);
            } else {
                CPURate = "CPU利用率极低";
            }

            // 上传消耗时间
            String timecountInfo = "\n上传数据消耗时间:" + timecount + "毫秒/条";
            Looper.prepare();
            Toast.makeText(getApplicationContext(),
                    CPUInfo + CPURate + timecountInfo, Toast.LENGTH_LONG)
                    .show();
            Looper.loop();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // System.out.println("Service bind.");
        return null;
    }
}
