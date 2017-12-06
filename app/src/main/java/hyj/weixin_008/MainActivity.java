package hyj.weixin_008;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Dimension;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import hyj.weixin_008.util.CommonUtil;
import hyj.weixin_008.util.DragImageUtil;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.GetPermissionUtil;
import hyj.weixin_008.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private EditText wxPwd;
    private EditText cn_num;
    private EditText action;
    CheckBox zc1;
    CheckBox zc2;
    CheckBox zc3;
    CheckBox yh;
    CheckBox get008Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("-----tt----");
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
        cn_num = (EditText)findViewById(R.id.cn_num);
        action = (EditText)findViewById(R.id.action);

        wxPwd.setText(sharedPreferences.getString("wxPwd","").trim().equals("")?"www12345":sharedPreferences.getString("wxPwd",""));
        cn_num.setText(sharedPreferences.getString("cn_num","").trim().equals("")?"86":sharedPreferences.getString("cn_num",""));
        action.setText(sharedPreferences.getString("action","").trim().equals("")?"":sharedPreferences.getString("action",""));
        zc1.setChecked(sharedPreferences.getString("zc1","").equals("true")?true:false);
        zc2.setChecked(sharedPreferences.getString("zc2","").equals("true")?true:false);
        zc3.setChecked(sharedPreferences.getString("zc3","").equals("true")?true:false);
        yh.setChecked(sharedPreferences.getString("yh","").equals("true")?true:false);
        get008Data.setChecked(sharedPreferences.getString("get008Data","").equals("true")?true:false);

        apiSettingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //testPic();
                //selectAllData();
                startActivity(new Intent(MainActivity.this, ApiSettingActivity.class));
            }
        });
        openAssit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!zc1.isChecked()&&!yh.isChecked()&&!"0".equals(action.getText()+"")){
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
                //save2Db();
                 //del();
                //update();
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
            }
        });
        vpnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
            }
        });

        delScennShotPic();

    }


    private void saveParams(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("wxPwd",wxPwd.getText()+"");
        editor.putString("cn_num",cn_num.getText()+"");
        editor.putString("action",action.getText()+"");
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

        List<Wx008Data> datas = DataSupport.findAll(Wx008Data.class);
        System.out.println("datas size1--->"+datas.size());

       int index=0;
      /* for(String[] str:strs008){
           if(index>186){
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
           }
           index = index+1;
        }*/
        List<Wx008Data> datas2 = DataSupport.findAll(Wx008Data.class);
        System.out.println("datas size2--->"+datas2.size());
        for(Wx008Data wx:datas2){
            System.out.println("wx-->"+JSON.toJSONString(wx));
        }

    }
    private void del(){
        List<Wx008Data> datas = DataSupport.findAll(Wx008Data.class);
        System.out.println("datas size1--->"+datas.size());

        List<String> phones = new ArrayList<String>();
        phones.add("8970363980");




        for(String phone:phones){
            int cntDel = DataSupport.deleteAll(Wx008Data.class,"phone=?",phone);
            System.out.println("--->cntDel-->"+cntDel);
        }
    }
    private void update(){
       Wx008Data wx1 = new Wx008Data();

        wx1.setWxId("q222xx");
        System.out.println("update count--->"+wx1.updateAll("phone=?","17095281435"));
        wx1.setWxId("q333xx");
        System.out.println("update count--->"+wx1.updateAll("phone=?","17095303459"));
        wx1.setWxId("q666xc");
        System.out.println("update count--->"+wx1.updateAll("phone=?","17095281704"));

        //System.out.println("del--->"+DataSupport.deleteAll(Wx008Data.class,"phone=?","14747236374"));

    }
    private void selectAllData(){
        List<Wx008Data> datas = DataSupport.findAll(Wx008Data.class);
        for(Wx008Data wx:datas){
            System.out.println("wx-->"+JSON.toJSONString(wx));
        }
    }
    private void delScennShotPic(){
        String  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Screenshots";
        LogUtil.d("DrapImageThread","path-->"+path);
        //清空文件
        File file = new File(path);
        File[] files = file.listFiles();
        if(files!=null&&files.length>0) {
            LogUtil.d("DrapImageThread", "file length-->" + files.length);
            for (File f : files) {
                LogUtil.d("DrapImageThread", "删除：" + f.getName());
                f.delete();
                LogUtil.d("DrapImageThread", "删除完：" + f.getName());
            }
        }

        File file1 = new File("/sdcard");
        File[] files1 = file1.listFiles();
        if(files1!=null&&files1.length>0) {
            LogUtil.d("DrapImageThread", "file length-->" + files1.length);
            for (File f : files1) {
                if(f.isFile()&&f!=null&&f.getName()!=null&&(f.getName().indexOf(".apk")>-1||f.getName().indexOf("app2")>-1||f.getName().indexOf("app3")>-1|f.getName().indexOf("debug")>-1)){
                    LogUtil.d("DrapImageThread", "删除APk：" + f.getName());
                    f.delete();
                    LogUtil.d("DrapImageThread", "删除完APK：" + f.getName());
                }

            }
        }
    }

    public void testPic(){
        //AutoUtil.execShell("input keyevent 120");
       /* String  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Screenshots";
        System.out.println("path-->"+path);
        File file = new File(path);
        File[] files = file.listFiles();
        for(File f:files){
            int distance = DragImageUtil.getDragDistance(path+"/"+f.getName());
            System.out.println("name-->"+f.getName()+"  size:"+f.length()+"  distance:"+distance);
        }*/

    }
}
