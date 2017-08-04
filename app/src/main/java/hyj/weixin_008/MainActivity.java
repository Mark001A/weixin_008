package hyj.weixin_008;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.GetPermissionUtil;
import hyj.weixin_008.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyWindowManager.createSmallWindow(getApplicationContext());
        GetPermissionUtil.getReadAndWriteContactPermision(this,MainActivity.this);
        setContentView(R.layout.activity_main);
        AutoUtil.wakeAndUnlock();

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        //Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
        //AutoUtil.startAppByPackName("com.android.settings","com.android.settings");
        Toast.makeText(MainActivity.this, "开启权限", Toast.LENGTH_LONG).show();
        List<String> list  = FileUtil.read008Data("/sdcard/A_hyj_008data/008data.txt");
        for(String s :list){
            System.out.println("-->"+s);
        }
        System.out.println("read008 data-->"+JSON.toJSON(list));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
