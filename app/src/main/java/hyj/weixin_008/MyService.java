package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

public class MyService extends AccessibilityService {
    public MyService() {
    }
    Map<String,String> record = new HashMap<String,String>();
    List<String[]> str;
    @Override
    protected void onServiceConnected() {
        LogUtil.d("myService","开启服务...");
        str= FileUtil.readConfFile("/sdcard/注册成功微信号.txt");
        LogUtil.d("myService","读取账号："+str);
        AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
        super.onServiceConnected();
        AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        AutoUtil.sleep(1000);
        new Thread(new MyThread()).start();
        //new Thread(new WxThread()).start();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("--event-->"+event.getEventType());

    }
    static String[] account={"12345608111","3333"};
    class MyThread implements Runnable{
        @Override
        public void run() {
            while (true){
                LogUtil.d("myService","-->开启线程..."+record);
                AutoUtil.sleep(300);
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if(root==null){
                    System.out.println("-->root is null");
                    AutoUtil.sleep(500);
                    continue;
                }
                //if(AutoUtil.checkAction(record,Constants.CHAT_LISTENING)){
                    AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/main_centerImg");
                    if(node1!=null){
                        AutoUtil.execShell("input tap 300 500");
                        AutoUtil.recordAndLog(record,"点击图片");
                        AutoUtil.sleep(1000);
                        continue;
                    }
                //}
                //点击历史记录
                clickIdMode(root,"com.soft.apk008v:id/button_history","点击图片","点击历史记录");
               /* if(AutoUtil.checkAction(record,"点击图片")){
                    AccessibilityNodeInfo history = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/button_history");
                    AutoUtil.performClick(history,record,"点击历史记录");
                    continue;
                }*/
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
                    AutoUtil.showToastByRunnable(getApplicationContext(),"启动微信");
                    AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                    new Thread(new WxThread()).start();
                    continue;
                }

                if(AutoUtil.checkAction(record,"登录成功")){
                    AutoUtil.sleep(1000);
                    AccessibilityNodeInfo save = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/button_restore");
                    if(save!=null){
                        AutoUtil.performBack(MyService.this,record,"返回008首页");
                    }
                    continue;
                }
                //点击工具箱
                clickIdMode(root,"com.soft.apk008v:id/textView_tools","返回008首页","点击工具箱");
                clickTextMode(root,"快捷操作","点击工具箱");
                clickTextMode(root,"一键操作","快捷操作");
                if(AutoUtil.checkAction(record,"一键操作")){
                    AutoUtil.sleep(2500);
                    AutoUtil.performBack(MyService.this,record,"返回上一级");
                    AutoUtil.performBack(MyService.this,record,"返回008首页");
                }
                                /*if(AutoUtil.checkAction(record,"返回008首页")){
                    AccessibilityNodeInfo tool = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/textView_tools");
                    AutoUtil.performClick(tool,record,"点击工具箱");
                }*/
        }
        }
    }
    class WxThread implements Runnable{
        @Override
        public void run() {
            while (true){
                LogUtil.d("myService","-->微信线程..."+record);
                AutoUtil.sleep(300);
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if(root==null){
                    System.out.println("-->Wx root is null");
                    AutoUtil.sleep(500);
                    continue;
                }
                //点击登录按钮
                clickTextMode(root,"登录","点击保存");
                //输入账号
                setAccount(root);
                //下一步
                clickTextMode(root,"下一步","输入账号");
                //输入密码
                setPwd(root);
                //点击登录
                clickIdMode(root,"com.tencent.mm:id/adj","输入密码","登录2");
                //clickTextMode(root,"登录2","否");
                //弹出是否推荐通讯录，点否
                clickIdMode(root,"com.tencent.mm:id/aer","登录2","否通讯录");
                //判断登录成功启动008
                loginSuccessStart008();

            }
        }
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
    private void setAccount(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"登录")){
            AutoUtil.sleep(500);
            AccessibilityNodeInfo phoneNode = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/h2");
            AutoUtil.createPasteInHandler(phoneNode,account[0],record,"输入账号");
        }
    }
    private void setPwd(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"下一步")){
            List<AccessibilityNodeInfo> pwdNode =  root.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bz4");
            if(pwdNode!=null&&pwdNode.size()==1){
                AutoUtil.createPasteInHandler(pwdNode.get(0).getChild(1),account[1],record,"输入密码");
            }
        }
    }
    private void loginSuccessStart008(){
        if(AutoUtil.checkAction(record,"否通讯录")){
            AccessibilityNodeInfo node = AutoUtil.findNodeInfosById(getRootInActiveWindow(),"com.tencent.mm:id/bpl");
            if(node!=null){
                AutoUtil.showToastByRunnable(getApplicationContext(),"登录成功");
                AutoUtil.recordAndLog(record,"登录成功");
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
}
