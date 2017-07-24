package hyj.weixin_008;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    EditText startLoginAccount;
    EditText vpnIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = GlobalApplication.getContext().getSharedPreferences("url",MODE_PRIVATE);
        startLoginAccount =  (EditText)findViewById(R.id.startLoginAccount);
        startLoginAccount.setText(sharedPreferences.getString("startLoginAccount",""));

        vpnIndex =  (EditText)findViewById(R.id.vpnIndex);
        String getVpn = sharedPreferences.getString("vpnIndex","");
        vpnIndex.setText(getVpn==null||"".equals(getVpn)?"1":getVpn);

    }
    private void saveParams(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("startLoginAccount",startLoginAccount.getText()+"");
        editor.putString("vpnIndex",vpnIndex.getText()+"");
        editor.commit();
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
