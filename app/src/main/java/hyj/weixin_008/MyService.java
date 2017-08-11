package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

import static hyj.weixin_008.GlobalApplication.getContext;

public class MyService extends AccessibilityService {
    public MyService() {
        //new Thread(new MyThread()).start();
    }
    Map<String,String> record = new HashMap<String,String>();
    List<String[]> str;
    String vpnIndex;
    String ipAddress;
    @Override
    protected void onServiceConnected() {
        LogUtil.d("myService","开启服务...");
        ipAddress = AutoUtil.getIPAddress(this);
        AutoUtil.showToastByRunnable(this,"当前IP："+ipAddress);
        AutoUtil.sleep(1000);
        str= FileUtil.readConfFile("/sdcard/注册成功微信号.txt");
        LogUtil.d("myService","读取账号："+str);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("url",MODE_PRIVATE);
        String startLoginAccount = sharedPreferences.getString("startLoginAccount","");
        vpnIndex = sharedPreferences.getString("vpnIndex","");
        if(vpnIndex==null||"".equals(vpnIndex)){
            vpnIndex = "1";
        }
        if(startLoginAccount!=null&&!"".equals(startLoginAccount)&&!"null".equals(startLoginAccount)){
            str = removeAct(startLoginAccount);
        }
        AutoUtil.recordAndLog(WeixinAutoHandler.record,Constants.CHAT_LISTENING);
        super.onServiceConnected();

        AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        AutoUtil.sleep(1000);

        new Thread(new Set008DataService(this,WeixinAutoHandler.record)).start();
        //new Timer().scheduleAtFixedRate(new RefreshFlowMsg(record), 0, 500);
    }
    private List<String[]> removeAct(String startLoginAccount){
        boolean flag = false;
        List<String[]> newStr = new ArrayList<String[]>();
        for(int i = str.size()-1;i>0;i--){
            if(flag||str.get(i)[0].equals(startLoginAccount)){
                newStr.add(str.get(i));
                flag = true;
            }
        }
        if(!flag){
            LogUtil.d("myservice","没有找到开始登录账号："+startLoginAccount);
            AutoUtil.showToastByRunnable(this,"没有找到开始登录账号："+startLoginAccount);
        }
        Collections.reverse(newStr);
        return newStr;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        MyWindowManager.updateFlowMsg(WeixinAutoHandler.record+"");
    }

    static String[] account={"12345608111","3333"};
    static String get008Phone="";
    class MyThread implements Runnable{
        @Override
        public void run() {
            while (true){
                LogUtil.d("myService","-->开启008线程..."+Thread.currentThread().getName()+record);
                if(WeixinAutoHandler.IS_PAUSE){
                    //AutoUtil.showToastByRunnable(MyService.this,"暂停服务..");
                    LogUtil.d("autoChat","暂停服务");
                    AutoUtil.sleep(3000);
                    continue;
                }
                if(WeixinAutoHandler.IS_NEXT_NONE){
                    System.out.println("----跳转下一个");
                    AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
                    AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
                    AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
                    WeixinAutoHandler.IS_NEXT_NONE = false;
                    continue;
                }
                AutoUtil.sleep(300);
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if(root==null){
                    System.out.println("-->root is null");
                    AutoUtil.sleep(500);
                    continue;
                }
                AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"工具箱");
                if(node1!=null){
                    AutoUtil.clickXY(373,422);
                    AutoUtil.recordAndLog(record,"点击图片");
                    AutoUtil.sleep(1000);
                    continue;
                }
                //点击历史记录
                clickIdMode(root,"com.soft.apk008v:id/button_history","点击图片","点击历史记录");
                if(AutoUtil.checkAction(record,"点击历史记录")){
                    List<AccessibilityNodeInfo> phoneList = root.findAccessibilityNodeInfosByViewId("com.soft.apk008v:id/listItem_tagName");
                    if(phoneList==null||phoneList.size()==0) continue;
                    String txtPhone = str.get(str.size()-1)[0];
                    AccessibilityNodeInfo phone = getNextPhoneByCurrentPhone(phoneList,txtPhone);
                    LogUtil.d("myservice","txtPhone--->"+txtPhone);
                    if(phone==null){
                        AutoUtil.performScroll(phoneList.get(0),record,"下滚");
                        AutoUtil.recordAndLog(record,"点击历史记录");
                        continue;
                    }
                    account = str.remove(str.size()-1);
                    LogUtil.d("myService","nodePhone-->"+phone.getText());
                    get008Phone = phone.getText()+"";
                    AutoUtil.showToastByRunnable(getApplicationContext(),"获取登录账号："+phone.getText());
                    AutoUtil.performClick(phone,record,"点击号码",400);
                    continue;
                }
                if(AutoUtil.checkAction(record,"点击号码")){
                    //AccessibilityNodeInfo history = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/button_restore");
                    AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
                    String[] str = new String[93];
                    if(list!=null&&list.getChildCount()==92){
                        for(int i=0;i<92;i++){
                            str[i]=list.getChild(i).getText()+"";
                        }
                        str[92]= get008Phone;
                    }
                    LogUtil.log008(JSON.toJSONString(str));
                    System.out.println("json-->"+JSON.toJSONString(str));
                    AutoUtil.recordAndLog(record,"点击图片");
                    AutoUtil.sleep(1000);
                }


        }
        }
    }

    private void clickIdMode(AccessibilityNodeInfo root,String id,String currentAction,String action){
        if(AutoUtil.checkAction(record,currentAction)){
            AccessibilityNodeInfo phoneNode = AutoUtil.findNodeInfosById(root,id);
            AutoUtil.performClick(phoneNode,record,action);
        }
    }
    private AccessibilityNodeInfo getNextPhoneByCurrentPhone(List<AccessibilityNodeInfo> phoneList,String phone){
        if(phoneList==null||phoneList.size()==0) return null;
        for(int i=0,l=phoneList.size();i<l;i++){
            if(phone.equals(phoneList.get(i).getText()+"")){
                return phoneList.get(i);
            }
        }
        return null;
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
