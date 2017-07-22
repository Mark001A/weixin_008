package hyj.weixin_008;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.GetPermissionUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetPermissionUtil.getReadAndWriteContactPermision(this,MainActivity.this);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "开启权限", Toast.LENGTH_LONG).show();
    }
}
