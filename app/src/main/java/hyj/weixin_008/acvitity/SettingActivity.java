package hyj.weixin_008.acvitity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hyj.weixin_008.GlobalApplication;
import hyj.weixin_008.MainActivity;
import hyj.weixin_008.R;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.util.DaoUtil;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    //EditText startLoginAccount;
    EditText vpnIndex;

    private String[] m;
    private TextView startLoginAccount;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    CheckBox addSpFr;
    CheckBox airplane;
    EditText airplaneChangeIpNumEdit;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m = getAllPhoneList();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = GlobalApplication.getContext().getSharedPreferences("url", MODE_PRIVATE);
        //startLoginAccount =  (EditText)findViewById(R.id.startLoginAccount);


        vpnIndex = (EditText) findViewById(R.id.vpnIndex);
        String getVpn = sharedPreferences.getString("vpnIndex", "");
        vpnIndex.setText(getVpn == null || "".equals(getVpn) ? "1" : getVpn);

        startLoginAccount = (TextView) findViewById(R.id.spinnerText);
        startLoginAccount.setText(sharedPreferences.getString("startLoginAccount", ""));
        spinner = (Spinner) findViewById(R.id.Spinner01);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        spinner.setVisibility(View.VISIBLE);

        addSpFr = (CheckBox) this.findViewById(R.id.addSpFr);
        airplane = (CheckBox) this.findViewById(R.id.airplane);
        addSpFr.setChecked(sharedPreferences.getString("addSpFr", "").equals("true") ? true : false);
        airplane.setChecked(sharedPreferences.getString("airplane", "").equals("true") ? true : false);

        airplaneChangeIpNumEdit = (EditText) findViewById(R.id.airplaneChangeIpNum);
        String getAirplaneChangeIpNum = sharedPreferences.getString("airplaneChangeIpNum", "1");
        airplaneChangeIpNumEdit.setText(getAirplaneChangeIpNum.equals("") ? "1" : getAirplaneChangeIpNum);

        Button export = (Button) this.findViewById(R.id.export);
        Button importBakData = (Button) this.findViewById(R.id.importBakData);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Wx008Data> datas = DataSupport.findAll(Wx008Data.class);
                if (datas != null && datas.size() > 0) {
                    LogUtil.export("/sdcard/A_hyj_008data/", JSON.toJSONString(datas));
                    Toast.makeText(SettingActivity.this, "已导出数据：" + datas.size() + "条", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingActivity.this, "没有可导出数据", Toast.LENGTH_LONG).show();
                }
            }
        });
        importBakData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File("/sdcard/A_hyj_008data");
                File[] files = file.listFiles();
                String imporFileName = files[files.length - 1].getPath();//获取最新文件名
                System.out.println("--->导入文件：" + imporFileName);

                String str = FileUtil.readAll(imporFileName);
                System.out.println("str-->" + str);
                List<Wx008Data> datas = JSON.parseArray(str, Wx008Data.class);
                int successCount = 0;
                for (Wx008Data data : datas) {
                    List<Wx008Data> getData = DataSupport.where("phone=?", data.getPhone()).find(Wx008Data.class);
                    if (getData == null || getData.size() == 0) {
                        if (data.save()) {
                            MyWindowManager.updateFlowMsg("已导入数据条数：" + successCount);
                            successCount = successCount + 1;
                        }
                    }
                }
                Toast.makeText(SettingActivity.this, "已导入数据：" + successCount + "条", Toast.LENGTH_LONG).show();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void saveParams() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("startLoginAccount", startLoginAccount.getText() + "");
        editor.putString("vpnIndex", vpnIndex.getText() + "");
        editor.putString("addSpFr", addSpFr.isChecked() + "");
        editor.putString("airplane", airplane.isChecked() + "");
        editor.putString("airplaneChangeIpNum", airplaneChangeIpNumEdit.getText() + "");
        editor.commit();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Setting Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            startLoginAccount.setText(m[arg2]);
            TextView tv = (TextView) arg1;
            tv.setTextSize(20);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("startLoginIndex", m[arg2]);
            editor.commit();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private String[] getAllPhoneList() {

        //List<Wx008Data> wx008Datas = DataSupport.where("expMsg  not like ? or expMsg is null","%被限制登录%").order("createTime asc").find(Wx008Data.class);
        List<Wx008Data> wx008Datas = DaoUtil.getWx008Datas();

        List<String> datas = new ArrayList<String>();
        for (int i = 0, l = wx008Datas.size(); i < l; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-HH");
            String time = sdf.format(wx008Datas.get(i).getCreateTime());
            String lastLoginTime = sdf.format(wx008Datas.get(i).getCreateTime());
            String cn = wx008Datas.get(i).getCnNum();
            //序号-账号-ac时间 国家 上次登录时间
            datas.add(i + "-" + wx008Datas.get(i).getPhone() + " " + time + " " + (cn == null ? "86" : cn) + " \n" + lastLoginTime);
        }

        return datas.toArray(new String[datas.size()]);
    }

    @Override
    protected void onStop() {
        saveParams();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        saveParams();
        super.onDestroy();
    }
}
