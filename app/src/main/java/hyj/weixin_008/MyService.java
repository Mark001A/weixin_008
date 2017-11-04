package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import hyj.weixin_008.common.ConstantWxId;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.model.Get008Data;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.model.RegObj;
import hyj.weixin_008.service.ADBClickService;
import hyj.weixin_008.thread.AddFriendThread;
import hyj.weixin_008.thread.AutoChatThread;
import hyj.weixin_008.thread.Get008DataThread;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.ParseRootUtil;

import static hyj.weixin_008.GlobalApplication.getContext;

public class MyService extends AccessibilityService {
    public MyService() {
        //new Thread(new MyThread()).start();
    }
    Map<String,String> record = new HashMap<String,String>();
    //List<String[]> str;
    String vpnIndex;
    String ipAddress;
    String zc1;
    String zc2;
    String zc3;
    String yh;
    RegObj regObj;
    @Override
    protected void onServiceConnected() {
        LogUtil.d("myService","开启服务...");
        ipAddress = AutoUtil.getIPAddress(this);
        AutoUtil.showToastByRunnable(this,"当前IP："+ipAddress);
        AutoUtil.sleep(1000);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("url",MODE_PRIVATE);
        String startLoginAccount = sharedPreferences.getString("startLoginAccount","");
        vpnIndex = sharedPreferences.getString("vpnIndex","");
        if(vpnIndex==null||"".equals(vpnIndex)){
            vpnIndex = "1";
        }
        /*if(startLoginAccount!=null&&!"".equals(startLoginAccount)&&!"null".equals(startLoginAccount)){
            str = removeAct(startLoginAccount);
        }*/
        AutoUtil.recordAndLog(WeixinAutoHandler.record,Constants.CHAT_LISTENING);
        super.onServiceConnected();


        String apiId = sharedPreferences.getString("apiId","");
        String apiPwd = sharedPreferences.getString("apiPwd","");
        String apiPjId = sharedPreferences.getString("apiPjId","");
        String zcPwd = sharedPreferences.getString("wxPwd","");
        String addSpFr = sharedPreferences.getString("addSpFr","");
        String airplane = sharedPreferences.getString("airplane","");
        String get008Data = sharedPreferences.getString("get008Data","");
        int airplaneChangeIpNum = Integer.parseInt(sharedPreferences.getString("airplaneChangeIpNum","1"));
        zc1 = sharedPreferences.getString("zc1","");
        zc2 = sharedPreferences.getString("zc2","");
        zc3 = sharedPreferences.getString("zc3","");
        yh = sharedPreferences.getString("yh","");

        PhoneApi pa = new PhoneApi(apiId,apiPwd,apiPjId,zcPwd);
        //List<Wx008Data> wx008Datas = DataSupport.findAll(Wx008Data.class);
        List<Wx008Data> wx008Datas = DataSupport.where("expMsg  not like ? or expMsg is null","%被限制登录%").order("createTime asc").find(Wx008Data.class);
        LogUtil.d("008data","读取数据库信息成功，总长度："+wx008Datas.size());
        regObj = new RegObj(airplane,zc2,zc3,addSpFr,wx008Datas,airplaneChangeIpNum);

    if("true".equals(zc1)){
            new Thread(new RegisterService(this,WeixinAutoHandler.record,pa,regObj)).start();
            new Thread(new GetPhoneAndValidCodeThread(pa)).start();
        }else if("true".equals(yh)){
            new Thread(new Set008DataService(this,WeixinAutoHandler.record,regObj)).start();
        }else if("true".equals(get008Data)){
           new Thread(new Get008DataThread(this,new Get008Data())).start();
       }

        AutoUtil.showToastByRunnable(getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        AutoUtil.sleep(1000);
        //new Thread(new GetPhoneAndValidCodeThread(pa)).start();
    }
    private String getFlowMsg(RegObj regObj,Map<String,String> record){
        String msg = "总数："+regObj.getTotalNum()+" 序号："+regObj.getCurrentIndex()
                    +"\n"+record.get("recordAction");
        return msg;
    }

    ADBClickService adbService  = new ADBClickService(this,record);
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        MyWindowManager.updateFlowMsg(getFlowMsg(regObj,WeixinAutoHandler.record));
        if(WeixinAutoHandler.IS_PAUSE) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null){
            System.out.println("--->main root is null");
            return;
        }
        System.out.println("--->"+event.getEventType());

        //---test start---------

        //ParseRootUtil.debugRoot(root);
        //---test end ----------

        if(AutoUtil.checkAction(WeixinAutoHandler.record,"wx注册成功")||AutoUtil.checkAction(WeixinAutoHandler.record,"wx登录成功")){
            if("true".equals(zc2)){//写个性签名
                AutoUtil.recordAndLog(WeixinAutoHandler.record,"qm");
            }else if("true".equals(zc3)){//发朋友圈
                AutoUtil.recordAndLog(WeixinAutoHandler.record,"pyq");
            }

        }
        setQm(root);
        sentFr(root);

    }


    private void sentFr(AccessibilityNodeInfo root){
        if("true".equals(zc3)&&AutoUtil.actionContains(WeixinAutoHandler.record,"pyq")){
            AccessibilityNodeInfo node5 = AutoUtil.findNodeInfosByText(root,"朋友圈");
            AccessibilityNodeInfo node6 = AutoUtil.findNodeInfosById(root,ConstantWxId.ID_SENDFR);
            AccessibilityNodeInfo node7 = AutoUtil.findNodeInfosByText(root,"发送");
            AccessibilityNodeInfo node11 = AutoUtil.findNodeInfosByText(root,"我知道了");
            AccessibilityNodeInfo node10 = AutoUtil.findNodeInfosById(root,ConstantWxId.REGID4);
            AutoUtil.performClick(node5,WeixinAutoHandler.record,"pyq朋友圈",1000);
            AutoUtil.performClick(node11,WeixinAutoHandler.record,"pyq我知道了",1000);
            if(node6!=null&&AutoUtil.checkAction(WeixinAutoHandler.record,"pyq朋友圈")){
                node6.getParent().performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            }
            AutoUtil.performSetText(node10,System.currentTimeMillis()+"",WeixinAutoHandler.record,"pyq输入朋友圈内容");
            AutoUtil.performClick(node7,WeixinAutoHandler.record,"pyq发送",1000);
            if(AutoUtil.checkAction(WeixinAutoHandler.record,"pyq发送")){
                AutoUtil.sleep(2000);
                AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosById(root,ConstantWxId.REGID3);
                AutoUtil.performClick(node8,WeixinAutoHandler.record,"pyq点赞1");
            }
            AccessibilityNodeInfo node9 = AutoUtil.findNodeInfosByText(root,"赞");
            AutoUtil.performClick(node9,WeixinAutoHandler.record,"pyq点赞2");
            if(AutoUtil.checkAction(WeixinAutoHandler.record,"pyq点赞2")){
                AutoUtil.sleep(2500);
                AutoUtil.recordAndLog(WeixinAutoHandler.record,"008注册处理完成");
            }
        }
    }
    private void setQm(AccessibilityNodeInfo root){
        //wx注册成功
        if("true".equals(zc2)&&AutoUtil.actionContains(WeixinAutoHandler.record,"qm")){
            AccessibilityNodeInfo node = AutoUtil.findNodeInfosById(root,ConstantWxId.REGID1);
            AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosByText(root,"个性签名");
            AccessibilityNodeInfo node4 = AutoUtil.findNodeInfosByText(root,"保存");

            if(AutoUtil.checkAction(WeixinAutoHandler.record,"qm个性签名")&&node4!=null){
                adbService.setTextByWindow("保存",469,311,System.currentTimeMillis()+"","qm输入签名",1500);
                AutoUtil.recordAndLog(WeixinAutoHandler.record,"qm输入签名");
            }
            if(AutoUtil.checkAction(WeixinAutoHandler.record,"qm输入签名")){
                AutoUtil.performClick(node4,WeixinAutoHandler.record,"qm保存个性签名",500);
            }

            if(AutoUtil.checkAction(WeixinAutoHandler.record,"qm保存个性签名")||AutoUtil.checkAction(WeixinAutoHandler.record,"qm返回")){
                if(AutoUtil.findNodeInfosByText(root,"我")!=null){
                    AutoUtil.recordAndLog(WeixinAutoHandler.record,"pyq发朋友圈");
                }
                AutoUtil.performClick(AutoUtil.findNodeInfosById(root,ConstantWxId.BACK),WeixinAutoHandler.record,"qm返回");
                return;
            }
            if(node!=null){
                AutoUtil.performClick(node,WeixinAutoHandler.record,"qm点击头像",500);
            }
            if(AutoUtil.checkAction(WeixinAutoHandler.record,"qm点击头像")){
                AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"更多");
                System.out.println("--tt-->"+node1);
                if(node1!=null&&(node1.getContentDescription()==null||"null".equals(node1.getContentDescription())))
                    AutoUtil.performClick(node1,WeixinAutoHandler.record,"qm更多",500);
            }
            if(!AutoUtil.checkAction(WeixinAutoHandler.record,"qm保存个性签名")){
                AutoUtil.performClick(node2,WeixinAutoHandler.record,"qm个性签名",500);
            }
           /* if(AutoUtil.checkAction(WeixinAutoHandler.record,"qm个性签名")){
                AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosById(root,ConstantWxId.REGID2);
                if(node3!=null&&node3.getText()==null&&node3.getContentDescription()==null){
                    AutoUtil.performSetText(node3,"456",WeixinAutoHandler.record,"qm输入签名");
                }
            }*/
        }
    }
    public  static void getChild(AccessibilityNodeInfo node){
        System.out.println("-----------start---------");
        if(node!=null){
            int count = node.getChildCount();
            System.out.println("child count"+count+"node text-->"+node.getText()+"  node clsName-->"+node.getClassName()+" desc"+node.getContentDescription());
            if(count>0){
                for(int i=0,l=count;i<l;i++){
                    AccessibilityNodeInfo child = node.getChild(i);
                    //getChild(child);
                    System.out.println(i+" child text-->"+child.getText()+" child clsName-->"+child.getClassName()+" desc"+node.getContentDescription());
                }
            }
        }
        System.out.println("-----------end---------");
    }

    static String[] account={"12345608111","3333"};
    static String get008Phone="";
   /* class MyThread implements Runnable{
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
    }*/
    private void save008Data(AccessibilityNodeInfo root){
        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
        String[] str = new String[93];
        if(list!=null&&list.getChildCount()==92){
            for(int i=0;i<92;i++){
                str[i]=list.getChild(i).getText()+"";
                System.out.println("-->"+i+" "+str[i]);
            }
            str[92]= get008Phone;
        }
        //LogUtil.log008(JSON.toJSONString(str));
        System.out.println("save 008data-->"+JSON.toJSONString(str));
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
}
