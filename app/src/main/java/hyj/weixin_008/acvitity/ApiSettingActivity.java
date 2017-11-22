package hyj.weixin_008.acvitity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.GlobalApplication;
import hyj.weixin_008.MainActivity;
import hyj.weixin_008.R;
import hyj.weixin_008.service.PhoneNumberAPIService;
import hyj.weixin_008.service.XmhPhoneNumberAPIService;

public class ApiSettingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private EditText apiId;
    private EditText apiPwd;
    private EditText apiPjId;
    private EditText api_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = GlobalApplication.getContext().getSharedPreferences("url",MODE_PRIVATE);

        setContentView(R.layout.activity_api_setting);
        apiId = (EditText)findViewById(R.id.api_id);
        apiPwd = (EditText)findViewById(R.id.api_pwd);
        apiPjId = (EditText)findViewById(R.id.api_project_id);
        api_type = (EditText)findViewById(R.id.api_type);
        apiId.setText(sharedPreferences.getString("apiId",""));
        apiPwd.setText(sharedPreferences.getString("apiPwd",""));
        apiPjId.setText(sharedPreferences.getString("apiPjId",""));
        api_type.setText(sharedPreferences.getString("api_type",""));


        Button testBtn = (Button)findViewById(R.id.api_test);
        Button backBtn = (Button)findViewById(R.id.api_back);
        testBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveParams();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //PhoneNumberAPIService service = new PhoneNumberAPIService();
                        String mainUrl ="http://www.ximahuang.com/alz/api";
                        XmhPhoneNumberAPIService service = new XmhPhoneNumberAPIService(mainUrl);
                        String token = service.login(apiId.getText()+"",apiPwd.getText()+"");
                       /* String msg = "";
                        if(!"".equals(token)){
                            msg = "测试连接成功："+token;
                        }else{
                            msg = "失败";
                        }*/
                        AutoUtil.showToastByRunnable(ApiSettingActivity.this,token);
                    }
                }).start();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveParams();
                finish();
            }
        });

    }
    private void saveParams(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("apiId",apiId.getText()+"");
        editor.putString("apiPwd",apiPwd.getText()+"");
        editor.putString("apiPjId",apiPjId.getText()+"");
        editor.putString("api_type",api_type.getText()+"");
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("save api--->onPause");
        saveParams();
    }
}
