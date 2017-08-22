package hyj.weixin_008;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.acvitity.ApiSettingActivity;
import hyj.weixin_008.acvitity.SettingActivity;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.GetPermissionUtil;
import hyj.weixin_008.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private EditText wxPwd;
    CheckBox zc1;
    CheckBox zc2;
    CheckBox zc3;
    CheckBox yh;
    CheckBox get008Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyWindowManager.createSmallWindow(getApplicationContext());
        MyWindowManager.createSmallWindow2(getApplicationContext());
        GetPermissionUtil.getReadAndWriteContactPermision(this,MainActivity.this);
        setContentView(R.layout.activity_main);
        sharedPreferences = GlobalApplication.getContext().getSharedPreferences("url",MODE_PRIVATE);

      /*  System.out.println("--IP1--->"+AutoUtil.getIPAddress(getApplicationContext()));
        AutoUtil.opentActivity(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        AutoUtil.sleep(3000);
        AutoUtil.clickXY(952,262);
        AutoUtil.sleep(3000);
        AutoUtil.clickXY(952,262);
        AutoUtil.sleep(10000);
        System.out.println("--IP1--->"+AutoUtil.getIPAddress(getApplicationContext()));
*/

        Button apiSettingBtn = (Button)this.findViewById(R.id.api_setting);
        Button openAssit = (Button)this.findViewById(R.id.open_assist);
        Button yhSetting = (Button)this.findViewById(R.id.yh_setting);
        Button vpnSetting = (Button)this.findViewById(R.id.vpn_setting);

        zc1 = (CheckBox)this.findViewById(R.id.zc1);
        zc2 = (CheckBox)this.findViewById(R.id.zc2);
        zc3 = (CheckBox)this.findViewById(R.id.zc3);
        yh = (CheckBox)this.findViewById(R.id.yh);
        get008Data = (CheckBox)this.findViewById(R.id.get008Data);
        wxPwd = (EditText)findViewById(R.id.zc_pwd);

        wxPwd.setText(sharedPreferences.getString("wxPwd","").trim().equals("")?"www12345":sharedPreferences.getString("wxPwd",""));
        zc1.setChecked(sharedPreferences.getString("zc1","").equals("true")?true:false);
        zc2.setChecked(sharedPreferences.getString("zc2","").equals("true")?true:false);
        zc3.setChecked(sharedPreferences.getString("zc3","").equals("true")?true:false);
        yh.setChecked(sharedPreferences.getString("yh","").equals("true")?true:false);
        get008Data.setChecked(sharedPreferences.getString("get008Data","").equals("true")?true:false);

        apiSettingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ApiSettingActivity.class));
            }
        });
        openAssit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!zc1.isChecked()&&!yh.isChecked()){
                    Toast.makeText(MainActivity.this, "注册？养号？请勾选", Toast.LENGTH_LONG).show();
                    return;
                }
                if(zc1.isChecked()&&yh.isChecked()){
                    Toast.makeText(MainActivity.this, "不能同时勾注册和养号", Toast.LENGTH_LONG).show();
                    return;
                }
                if(zc1.isChecked()){
                    String apiId = sharedPreferences.getString("apiId","");
                    String apiPwd = sharedPreferences.getString("apiPwd","");
                    String apiPjId = sharedPreferences.getString("apiPjId","");
                    if("".equals(apiId.trim())||"".equals(apiPwd)||"".equals(apiPjId)){
                        Toast.makeText(MainActivity.this, "api对接设置参数为空", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(wxPwd.getText()==null||"".equals(wxPwd.getText().toString().trim())){
                        Toast.makeText(MainActivity.this, "微信注册密码未设置", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                Toast.makeText(MainActivity.this, "打开启权限，才能运行", Toast.LENGTH_LONG).show();
            }
        });
        yhSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                save2Db();
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
            }
        });
        vpnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
            }
        });

    }

    private void saveParams(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("wxPwd",wxPwd.getText()+"");
        editor.putString("zc1",zc1.isChecked()+"");
        editor.putString("zc2",zc2.isChecked()+"");
        editor.putString("zc3",zc3.isChecked()+"");
        editor.putString("yh",yh.isChecked()+"");
        editor.putString("get008Data",get008Data.isChecked()+"");
        editor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("save--->onPause");
        saveParams();
    }
    private List<String[]> get008Datas(){
        List<String> list =  FileUtil.read008Data("/sdcard/A_hyj_008data/008data.txt");
        List<String[]> newList = new ArrayList<String[]>();
        for(String s:list){
            newList.add(JSONObject.parseObject(s,String[].class));
        }
        return newList;
    }
    private Map<String,String>  getWxAccounts(){
        Map<String,String> accounts = new HashMap<String,String>();
        List<String[]> list =   FileUtil.readConfFile("/sdcard/A_hyj_008data/wxAccounts.txt");
        for(String[] str:list){
            accounts.put(str[0],str[1]);
        }
        System.out.println("currentAccount-->"+accounts);
        return accounts;
    }
    private void save2Db(){
        List<String[]> strs008 = get008Datas();
        System.out.println("strs008-->"+strs008.size());
        Map<String,String> accounts = getWxAccounts();


       /* for(String[] str:strs008){
            Wx008Data wx008Data = new Wx008Data();
            wx008Data.setDatas(JSON.toJSONString(str));
            wx008Data.setPhone(str[str.length-1]);
            wx008Data.setCreateTime(new Date());
            if(str[1].equals("序列号")){
                wx008Data.setWxPwd(str[str.length-2]);
            }else{
                String pwd =accounts.get(str[str.length-1]);
                wx008Data.setWxPwd(pwd);
            }
            wx008Data.save();
            AutoUtil.sleep(100);
            System.out.println("--->"+str[str.length-1]);
        }*/
        List<Wx008Data> datas = DataSupport.findAll(Wx008Data.class);
        for(Wx008Data da:datas){
            System.out.println("--->"+JSON.toJSONString(da));
        }
        System.out.println("datas size--->"+datas.size());
        //System.out.println("datas--->"+JSON.toJSONString(datas));
    }
}
