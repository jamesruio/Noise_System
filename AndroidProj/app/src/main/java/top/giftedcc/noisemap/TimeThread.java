package top.giftedcc.noisemap;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chang on 2018/11/8.
 * Class:  实时更新时间的线程
 * describe:  生成hh:mm:ss的格式的时间
 *
 */

public class TimeThread extends Thread {

    public TextView tvDate;
    private int msgKey1 = 22;
    public TimeThread(TextView tvDate) {
        this.tvDate = tvDate;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.what = msgKey1;
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);

    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 22:
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String date = sdf.format(new Date());
                    tvDate.setText(date);
                    break;
                default:
                    break;
            }
        }

    };

}
