package hyj.weixin_008;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import hyj.weixin_008.util.FileUtil;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    //EditText startLoginAccount;
    EditText vpnIndex;

    private String[] m;
    private TextView startLoginAccount ;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m = getAllPhoneList();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = GlobalApplication.getContext().getSharedPreferences("url",MODE_PRIVATE);
        //startLoginAccount =  (EditText)findViewById(R.id.startLoginAccount);


        vpnIndex =  (EditText)findViewById(R.id.vpnIndex);
        String getVpn = sharedPreferences.getString("vpnIndex","");
        vpnIndex.setText(getVpn==null||"".equals(getVpn)?"1":getVpn);

        startLoginAccount = (TextView) findViewById(R.id.spinnerText);
        startLoginAccount.setText(sharedPreferences.getString("startLoginAccount",""));
        spinner = (Spinner) findViewById(R.id.Spinner01);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        spinner.setVisibility(View.VISIBLE);

    }
    private void saveParams(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("startLoginAccount",startLoginAccount.getText()+"");
        editor.putString("vpnIndex",vpnIndex.getText()+"");
        editor.commit();
    }
    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            startLoginAccount.setText(m[arg2]);

            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString("qNum",m[arg2]);
            editor.commit();
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
    private String[] getAllPhoneList(){
        List<String> phones = new ArrayList<String>();
        List<String[]> list =  FileUtil.readConfFile("/sdcard/注册成功微信号.txt");
        for(String[] str:list){
            phones.add(str[0]);
        }
        return phones.toArray(new String[phones.size()]);
    }

    @Override
    protected void onStop() {
        saveParams();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveParams();
        super.onDestroy();
    }
}
