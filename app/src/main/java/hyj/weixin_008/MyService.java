package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.WxUtil;

import static hyj.weixin_008.GlobalApplication.getContext;

public class MyService extends AccessibilityService {
    public MyService() {
        //new Thread(new MyThread()).start();
        new Thread(new RegisterThread()).start();
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
        AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
        super.onServiceConnected();
       /* AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        AutoUtil.sleep(1000);*/
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
        System.out.println("--event-->"+event.getEventType());
        AccessibilityNodeInfo root = getRootInActiveWindow();

    }
    class RegisterThread implements Runnable{
        @Override
        public void run() {
            while (true){
                AutoUtil.sleep(1000);
                LogUtil.d("myService","-->开启008线程..."+Thread.currentThread().getName()+record);
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if(root==null){
                    LogUtil.d("myService","register is null");
                    continue;
                }
                WxUtil.clickRegisterBtn1(root,record);
                WxUtil.setNamePhoneAndPwd(root,record);
                AccessibilityNodeInfo registerNode3 = AutoUtil.findNodeInfosByText(root,"确认手机号码");
                AccessibilityNodeInfo registerNode4 = AutoUtil.findNodeInfosByText(root,"验证手机号");
                AccessibilityNodeInfo registerNode5 = AutoUtil.findNodeInfosByText(root,"下一步");

                if(registerNode3!=null)
                    System.out.println("registerNode3-->"+registerNode3.getText());
                if(registerNode4!=null)
                    System.out.println("registerNode4-->"+registerNode4.getText());
                if(registerNode5!=null)
                    System.out.println("registerNode5-->"+registerNode5.getText());

            }
        }
    }

    class TestThread implements Runnable{
        @Override
        public void run() {
            while (true){
                AutoUtil.sleep(1000);
                doVPN();
                //AutoUtil.recordAndLog(record,"设置VPN");
            }
        }
    }

    private void doVPN(){
        //AutoUtil.sleep(500);
        AccessibilityNodeInfo linking =AutoUtil.findNodeInfosByText(getRootInActiveWindow(),"正在连接...");
        if(linking!=null){
            AutoUtil.showToastByRunnable(MyService.this,"正在连接...");
            AutoUtil.sleep(1000);
            return;
        }
        if(AutoUtil.checkAction(record,"点击连接")){
            AccessibilityNodeInfo link =AutoUtil.findNodeInfosByText(getRootInActiveWindow(),"已连接");
            if(link!=null){
                String newIP = AutoUtil.getIPAddress(this);
                AutoUtil.showToastByRunnable(getApplicationContext(),"连接成功!\n当前IP："+newIP+"\n上次IP："+ipAddress);
                ipAddress = newIP;
                AutoUtil.recordAndLog(record,"连接成功");
                AutoUtil.showToastByRunnable(getApplicationContext(),"启动微信");
                AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                AutoUtil.sleep(5000);
                return;
            }
        }


        clickTextXY1(700,600,"其他连接方式","miui:id/action_bar_title","设置",100);
        clickTextXY1(700,300,"点击VPN","miui:id/action_bar_title","其他连接方式",100);


        clickTextXY1(557,1158,"断开连接","miui:id/alertTitle","已连接 VPN",1500);

        if("1".equals(vpnIndex)){
            clickTextXY1(500,400,"点击area","miui:id/action_bar_title","VPN",100);
        }else if("2".equals(vpnIndex)){
            clickTextXY1(500,514,"点击area","miui:id/action_bar_title","VPN",100);
        }else if("3".equals(vpnIndex)){
            clickTextXY1(500,603,"点击area","miui:id/action_bar_title","VPN",100);
        }else if("4".equals(vpnIndex)){
            clickTextXY1(500,706,"点击area","miui:id/action_bar_title","VPN",100);
        }

        clickTextXY1(557,1158,"点击连接","miui:id/alertTitle","连接到",2000);
        if(AutoUtil.checkAction(record,"点击连接")){
            AccessibilityNodeInfo linking1 =AutoUtil.findNodeInfosByText(getRootInActiveWindow(),"正在连接...");
            if(linking1!=null){
                AutoUtil.showToastByRunnable(MyService.this,"正在连接...");
                return;
            }
            AccessibilityNodeInfo link =AutoUtil.findNodeInfosByText(getRootInActiveWindow(),"已连接");
            if(link!=null){
                String newIP = AutoUtil.getIPAddress(this);
                AutoUtil.showToastByRunnable(getApplicationContext(),"连接成功!\n当前IP："+newIP+"\n上次IP："+ipAddress);
                ipAddress = newIP;
                AutoUtil.recordAndLog(record,"连接成功");
                AutoUtil.showToastByRunnable(getApplicationContext(),"启动微信");
                AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                AutoUtil.sleep(5000);
            }else {
                AutoUtil.showToastByRunnable(MyService.this,"尝试重新连接...");

                //clickTextXY1(500,400,"点击area","miui:id/action_bar_title","VPN",100);

                if("1".equals(vpnIndex)){
                    clickTextXY1(500,400,"点击area","miui:id/action_bar_title","VPN",100);
                }else if("2".equals(vpnIndex)){
                    clickTextXY1(500,514,"点击area","miui:id/action_bar_title","VPN",100);
                }else if("3".equals(vpnIndex)){
                    clickTextXY1(500,603,"点击area","miui:id/action_bar_title","VPN",100);
                }else if("4".equals(vpnIndex)){
                    clickTextXY1(500,706,"点击area","miui:id/action_bar_title","VPN",100);
                }

                clickTextXY1(557,1158,"点击连接","miui:id/alertTitle","连接到",100);
            }
        }
    }
    //先判断所在页面，在点击操作
    private void clickTextXY1(int x,int y,String action,String titleId,String title,int milliSeconds){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null){
            LogUtil.d("myService",title+"is null");
            return;
        }
        AccessibilityNodeInfo titleNode = AutoUtil.findNodeInfosById(root,titleId);
        if(titleNode!=null&&titleNode.getText().toString().contains(title)){
            AutoUtil.execShell("input tap "+x+" "+y);
            AutoUtil.recordAndLog(record,action);
            AutoUtil.sleep(milliSeconds);
        }
    }

    private void clickIdXY(int x,int y,String id,String action,String lastAction,int milliSeconds){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null){
            LogUtil.d("myService",id+"is null");
            return;
        }
        if(AutoUtil.checkAction(record,lastAction)){
            AccessibilityNodeInfo area =AutoUtil.findNodeInfosById(root,id);
            if(area!=null){
                AutoUtil.execShell("input tap "+x+" "+y);
                AutoUtil.recordAndLog(record,action);
                AutoUtil.sleep(milliSeconds);
            }
        }

    }

    private void clickTextXY(int x,int y,String text,String action,String lastAction,int milliSeconds){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null){
            LogUtil.d("myService",text+"is null");
            return;
        }
        if(AutoUtil.checkAction(record,lastAction)){
            AccessibilityNodeInfo area =AutoUtil.findNodeInfosByText(root,text);
            if(area!=null){
                AutoUtil.execShell("input tap "+x+" "+y);
                AutoUtil.recordAndLog(record,action);
                AutoUtil.sleep(milliSeconds);
            }
        }

    }
    static String[] account={"12345608111","3333"};
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

                clickIdMode(root,"com.soft.apk008v:id/textView_tools",Constants.CHAT_LISTENING,"点击工具箱");
                clickIdMode(root,"com.soft.apk008v:id/textView_tools","登录成功","点击工具箱");

                clickTextMode(root,"快捷操作","点击工具箱");
                clickTextMode(root,"一键操作","快捷操作");
                if(AutoUtil.checkAction(record,"一键操作")){
                    AutoUtil.sleep(3000);
                    AutoUtil.performBack(MyService.this,record,"返回上一级");
                    AutoUtil.sleep(500);
                    AutoUtil.performBack(MyService.this,record,"返回008首页");
                }

                //点击008首页图片
                click008MainImage(root);
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
                    AutoUtil.showToastByRunnable(getApplicationContext(),"获取登录账号："+phone.getText());
                    AutoUtil.performClick(phone,record,"点击号码",400);
                    continue;
                }
                if(AutoUtil.checkAction(record,"点击号码")){
                    AccessibilityNodeInfo history = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/button_restore");
                    AutoUtil.performClick(history,record,"点击保存");
                    AutoUtil.performBack(MyService.this,record,"返回008首页");
                    AutoUtil.recordAndLog(record,"设置VPN");
                    AutoUtil.showToastByRunnable(getApplicationContext(),"设置VPN--"+vpnIndex);
                    AutoUtil.startSysSetting();
                    //new Thread(new VPNThread()).start();
                    AutoUtil.sleep(3000);
                }
                //if(AutoUtil.checkAction(record,"设置VPN"))
                    doVPN();
                //if(AutoUtil.checkAction(record,"连接成功"))
                    doWX();

        }
        }
    }
    private void click008MainImage(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"点击图片")){
            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/main_centerImg");
            if(node1!=null){
                LogUtil.d("myService","替补点击图片");
                AutoUtil.execShell("input tap 384 398");
                AutoUtil.recordAndLog(record,"点击图片");
            }
        }
        if(AutoUtil.checkAction(record,"返回008首页")){
            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/main_centerImg");
            if(node1!=null){
                AutoUtil.execShell("input tap 384 398");
                AutoUtil.recordAndLog(record,"点击图片");
            }
        }
    }
    private void doWX(){
        AutoUtil.sleep(300);
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null){
            System.out.println("-->Wx root is null");
            AutoUtil.sleep(500);
            return;
        }
        //点击登录按钮
        clickTextMode(root,"登录","连接成功");

        if(AutoUtil.checkAction(record,"登录")){
            String tempIp = AutoUtil.getIPAddress(this);
            if(!ipAddress.equals(tempIp)){
                String msg = "ip发生变化:"+tempIp;
                AutoUtil.showToastByRunnable(this,msg);
                LogUtil.d("myService",msg);
            }
        }
        //输入账号
        setAccount(root);
        //下一步
        clickTextMode(root,"下一步","输入账号");
        //输入密码
        setPwd(root);
        //点击登录
        clickIdMode(root,"com.tencent.mm:id/adj","输入密码","登录2");
        //弹出是否推荐通讯录、其他设备登录提示 退出,判断登录成功启动008
        clickIdModeDeny(1,"登录2","退出弹出框");
    }

    private void clickTextMode(AccessibilityNodeInfo root,String text,String currentAction){
        if(AutoUtil.checkAction(record,currentAction)){
            AccessibilityNodeInfo loginNode = AutoUtil.findNodeInfosByText(root,text);
            AutoUtil.performClick(loginNode,record,text);
        }
    }
    private void clickIdMode(AccessibilityNodeInfo root,String id,String currentAction,String action){
        if(AutoUtil.checkAction(record,currentAction)){
            AccessibilityNodeInfo phoneNode = AutoUtil.findNodeInfosById(root,id);
            AutoUtil.performClick(phoneNode,record,action);
        }
    }
    private void clickIdModeDeny(int waitCount,String currentAction,String action){
        if(waitCount==10){
            AutoUtil.showToastByRunnable(getApplicationContext(),"登录失败!");
            LogUtil.login("fail",JSON.toJSONString(account));
            AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
            AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            return;
        }
        if(AutoUtil.checkAction(record,currentAction)||AutoUtil.checkAction(record,action)){
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if(root==null){
                LogUtil.d("login","root is null"+waitCount);
                AutoUtil.sleep(2000);
                clickIdModeDeny(waitCount+1,currentAction,action);
            }

            AccessibilityNodeInfo tip = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/bvs");
            AccessibilityNodeInfo wxList = AutoUtil.findNodeInfosById(getRootInActiveWindow(),"com.tencent.mm:id/bpl");
            if(tip!=null){
                LogUtil.d("myService",tip.getText()+"");
                if(tip.getText().toString().contains("限制登录")||tip.getText().toString().contains("登录环境异常")){
                    LogUtil.login("fail",JSON.toJSONString(account)+"--"+tip.getText());
                    AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
                    AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
                    AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
                    return;
                }else if(tip.getText().toString().contains("看看手机通讯录里")){
                    AutoUtil.performBack(this,record,action);
                }else if(tip.getText().toString().contains("有人正通过微信密码在")){
                    AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"忽略"),record,action);
                }
            }else if(wxList!=null){
                LogUtil.login("success",JSON.toJSONString(account));
                AutoUtil.showToastByRunnable(getApplicationContext(),"登录成功");
                AutoUtil.recordAndLog(record,"登录成功");
                AutoUtil.sleep(3000);
                AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
                AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            }else {
                AutoUtil.sleep(3000);
                LogUtil.d("login","登录等待成功"+waitCount);
                clickIdModeDeny(waitCount+1,currentAction,action);
            }
        }
    }
    private void setAccount(AccessibilityNodeInfo root){
        AutoUtil.sleep(1000);
        if(AutoUtil.checkAction(record,"登录")){
            AccessibilityNodeInfo phoneNode = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/h2");
            AutoUtil.createPasteInHandler(phoneNode,account[0],record,"输入账号");
            AutoUtil.sleep(300);
        }
    }
    private void setPwd(AccessibilityNodeInfo root){
        AutoUtil.sleep(600);
        if(AutoUtil.checkAction(record,"下一步")){
            List<AccessibilityNodeInfo> pwdNode =  root.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bz4");
            if(pwdNode!=null&&pwdNode.size()==1){
                AutoUtil.createPasteInHandler(pwdNode.get(0).getChild(1),account[1],record,"输入密码");
                AutoUtil.sleep(600);
            }
        }
    }
    private void loginSuccessStart008(){
        if(AutoUtil.checkAction(record,"否通讯录")){
            AccessibilityNodeInfo node = AutoUtil.findNodeInfosById(getRootInActiveWindow(),"com.tencent.mm:id/bpl");
            if(node!=null){
                LogUtil.login("success",JSON.toJSONString(account));
                AutoUtil.showToastByRunnable(getApplicationContext(),"登录成功");
                AutoUtil.recordAndLog(record,"登录成功");
                AutoUtil.sleep(3000);
                AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
                AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            }
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
    public  static void getChild(AccessibilityNodeInfo node){
        System.out.println("-----------start---------");
        if(node!=null){
            int count = node.getChildCount();
            System.out.println("child count"+count+"node text-->"+node.getText()+"  node clsName-->"+node.getClassName()+" desc"+node.getContentDescription());
            if(count>0){
                for(int i=0,l=count;i<l;i++){
                    if(i==0){
                        System.out.println("第层 第0个子节点，兄弟节点数:"+count);
                    }
                    AccessibilityNodeInfo child = node.getChild(i);
                    getChild(child);
                    if(child!=null){
                        System.out.println(i+" child text-->"+child.getText()+" child clsName-->"+child.getClassName()+" desc"+node.getContentDescription());
                    }
                }
            }
        }
        System.out.println("-----------end---------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
