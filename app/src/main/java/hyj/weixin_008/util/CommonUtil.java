package hyj.weixin_008.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.GlobalApplication;

/**
 * Created by asus on 2017/11/19.
 */

public class CommonUtil {
    //处理不在应在的界面
    public static void doNotInCurrentView(AccessibilityNodeInfo root,Map<String,String> record){
        if((record.get("recordAction").contains("wx")||record.get("recordAction").contains("pyq"))&&root.getPackageName().toString().indexOf("tencent")==-1){
            AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            System.out.println("-->【不在微信界面，启动】");
            AutoUtil.sleep(1000);
        }else if(record.get("recordAction").contains("008")&&root.getPackageName().toString().indexOf("008")==-1){
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            System.out.println("-->【不在008界面，启动】");
            AutoUtil.sleep(1000);
        }else if(record.get("recordAction").contains("st")&&root.getPackageName().toString().indexOf("android")==-1){
            AutoUtil.opentActivity(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            System.out.println("-->【不在AIRPLANE_MODE界面，启动】");
            AutoUtil.sleep(1000);
        }

    }

    public static Integer getNetWorkType(){
        Integer type = null;
        ConnectivityManager connectMgr = (ConnectivityManager) GlobalApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
       if(info!=null){
           type = info.getType();
       }
       return type;
    }
}
