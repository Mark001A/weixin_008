package hyj.weixin_008.flowWindow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import hyj.weixin_008.GlobalApplication;
import hyj.weixin_008.MainActivity;
import hyj.weixin_008.R;
import hyj.weixin_008.SettingActivity;
import hyj.weixin_008.common.WeixinAutoHandler;


/**
 * Created by Administrator on 2017/6/30.
 */

public class FloatWindowBigView extends LinearLayout {

    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;
    Button pause;
    Button save008;
    TextView statusText;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button back = (Button) findViewById(R.id.back);
        Button openAssist = (Button)findViewById(R.id.open_assist);
        Button nextOne = (Button)findViewById(R.id.nextOne);
        Button openSetting = (Button)findViewById(R.id.open_setting);
        save008 = (Button)findViewById(R.id.save_008);
        statusText = (TextView)findViewById(R.id.status_text);
        pause = (Button) findViewById(R.id.pause);
        String status = "";
        if(WeixinAutoHandler.IS_START_SERVICE){
            status = WeixinAutoHandler.IS_PAUSE==false?"当前状态：已经开启":"当前状态：已经暂停";
        }else {
            status = "当前状态：权限未开启";
        }
        pause.setText(WeixinAutoHandler.IS_PAUSE==false?"暂停":"开始");
        statusText.setText(status);

        nextOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                WeixinAutoHandler.IS_NEXT_NONE=true;
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (WeixinAutoHandler.IS_PAUSE==false){
                    WeixinAutoHandler.IS_PAUSE=true;
                    pause.setText("开始");
                    statusText.setText("当前状态：已经暂停");
                }else{
                    WeixinAutoHandler.IS_PAUSE = false;
                    pause.setText("暂停");
                    statusText.setText("当前状态：已经开启");
                }
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        save008.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WeixinAutoHandler.IS_SAVE_008==false){
                    WeixinAutoHandler.IS_SAVE_008=true;
                    pause.setText("开启保存008数据");
                }else{
                    WeixinAutoHandler.IS_SAVE_008 = false;
                    pause.setText("关闭保存008数据");
                }
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        openAssist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                GlobalApplication.getContext().startActivity(intent);
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });

        openSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GlobalApplication.getContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                GlobalApplication.getContext().startActivity(intent);
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }
}
