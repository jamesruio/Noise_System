package top.giftedcc.noisemap;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.BottomNavigationView;
import android.widget.ImageView;
import android.widget.TextView;

import top.giftedcc.features.UploaddBData;

/**
 * Created by chang on 2018/11/8.
 * Class:  实现“主页”的Activity
 * describe:  启动Service服务，实时获取噪声相关收据，实现噪声数据上传
 *
 */
public class MainActivity extends AppCompatActivity {

    private TextView test_currentresult = null;
    private TextView test_currenttime = null;
    private TextView test_location = null;
    //private TextView testresult = null;
    //private TextView testresultsec = null;
    private BroadcastDone receiver = null;
    //private SlidingMenu mSlidingMenu;
    //private TopRightMenu mTopRightMenu;
    private ImageView addBtn;

    /*
    //声明ViewPager
    private ViewPager mViewpager;

    //声明两个Tab
    private LinearLayout mTabData1;
    private LinearLayout mTabData2;

    //声明两个ImageButton
    private ImageButton mData1Img;
    private ImageButton mData2Img;

    //声明ViewPager的适配器
    private PagerAdapter mAdpater;
    //用于装载两个Tab的List
    private List<View> mTabs = new ArrayList<View>();
    */

    //位置权限需要临时获取
    private String[] perms = {  Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.PROCESS_OUTGOING_CALLS};
    private final int PERMS_REQUEST_CODE = 200;
    private TextView mTextMessage;

    /**
     * 创建“主页”活动，打开按键、麦克风监听，启动Service绑定，启动广播绑定，进入Application
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);

        //bottomNavigationView Item 选择监听
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_search:
                        intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_menu:
                        intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        // System.out.println("onCreate start");
        //初始化...
        //initToolbar();
        //initLeftSlidingMenu();
        //initTopRightMenu();
        /*
        initTabs();//初始化控件
        initDatas();//初始化数据
        initEvents();//初始化事件
        mData1Img.setImageResource(R.drawable.data1_press);
        */

        // 定位布局文件中的控件
        test_currentresult = (TextView) findViewById(R.id.test_currentresult);
        test_currenttime = (TextView) findViewById(R.id.test_currenttime);
        test_location = (TextView) findViewById(R.id.test_location);
        //testresultsec = (TextView) findViewById(R.id.test_result_sec);

        //
        test_location.setText("Make Sure Your GPS/NETWORK is Available.");
        //testresult.setText("采集数据表(每1分钟刷新一次)\n");
        //testresultsec.setText("详细数据表(每10秒钟刷新一次)\n");

        // SharedPreferences preferences = getSharedPreferences("first_pref",
        // MODE_PRIVATE);
        // Boolean isFirstIn = preferences.getBoolean("isFirstIn",
        // true);//默认为true
        // String appVersion = preferences.getString("preferences",
        // "NoiseMap1.0");//默认为1.0
        // if ((isFirstIn == true) && (appVersion.equals("NoiseMap1.0"))) {
        // // 首次启动，这里写配置数据库的代码
        // System.out.println("isFirstIn=" + isFirstIn);
        // //修改isFirstIn的值
        // Editor editor = preferences.edit();
        // editor.putBoolean("isFirstIn", false);
        // editor.commit();
        // System.out.println("isFirstIn=" + isFirstIn);
        // }


        // 动态方式注册广播
        receiver = new BroadcastDone();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Info.update.Broadcast");
        this.registerReceiver(receiver, filter);
        System.out.println("BroadcastDone register");

        //Android 6.0以上版本需要临时获取权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 &&
                PackageManager.PERMISSION_GRANTED != checkSelfPermission(perms[0])) {
            requestPermissions(perms, PERMS_REQUEST_CODE);
        } else {
            //启动测量服务
            Intent serviceintent = new Intent(MainActivity.this,UploaddBData.class);
            MainActivity.this.startService(serviceintent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /***
         * 第一个参数R.menu.menu:表示通过哪一个资源文件来创建选项菜单
         * 第二个参数menu:表示我们的菜单项将添加到哪个Menu对象中去；
         * ***/
        getMenuInflater().inflate(R.menu.control, menu);
        Log.e("onCreateOptionsMenu","is called");
        return true;
    }

    /**
     * 关闭麦克风监听；解除Service绑定；解除广播绑定；退出Application
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        System.out.println("onDestroy start");
        this.unregisterReceiver(receiver);
        System.out.println("BroadcastDone unregister");
        Intent service = new Intent(MainActivity.this, UploaddBData.class);
        MainActivity.this.stopService(service);
        System.out.println("Service stop");

        android.os.Debug.stopMethodTracing();// Stop method tracing that the activity started during onCreate()
        System.exit(0);
        System.out.println("onDestroy finish");

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case PERMS_REQUEST_CODE:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(storageAccepted){
                    //启动测量服务
                    Intent serviceintent = new Intent(MainActivity.this,UploaddBData.class);
                    MainActivity.this.startService(serviceintent);
                }
                break;

        }
    }

    /**
     * 接收Service发出的广播，更新界面
     */
    public class BroadcastDone extends BroadcastReceiver {

        public BroadcastDone() {
            super();
        }

        @Override
        public void onReceive(Context cont, Intent i) {
            Bundle extras = i.getExtras();
            if (extras != null) {
                    /* 这里获取UploaddBData.java发出的Info.update.Broadcast广播，更新UI面板 */
                if (extras.containsKey("currentdBs")) {
                    String currentdBs = (String) extras.get("currentdBs");//
                    test_currentresult.setText(currentdBs);
                }
                if (extras.containsKey("time_current")) {
                    String time_current = (String) extras.get("time_current");
                    test_currenttime.setText(time_current);
                }
                if (extras.containsKey("locationmsg")) {
                    String locationmsg = (String) extras.get("locationmsg");//
                    test_location.setText(locationmsg);
                }
                    /*
                    if (extras.containsKey("avgdB")) {
                        String avgdB = (String) extras.get("avgdB");
                        testresult.append(avgdB);
                    }
                    */
                if (extras.containsKey("detaildata")) {
                    //String detaildata = (String) extras.get("detaildata");//
                    //testresultsec.append(detaildata);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // System.out.println("onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // System.out.println("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // System.out.println("onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // System.out.println("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // System.out.println("onStop");
    }

}

