package top.giftedcc.noisemap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by chang on 2018/11/8.
 * Class:  实现“我的”的Activity
 * describe:
 *
 */
public class MenuActivity extends AppCompatActivity {

    /**
     * 创建“我的”活动，打开按键监听
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_menu);

        //bottomNavigationView Item 选择监听
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.navigation_search:
                        intent = new Intent(MenuActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_home:
                        intent = new Intent(MenuActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}
