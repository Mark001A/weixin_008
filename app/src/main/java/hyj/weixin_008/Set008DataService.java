package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.service.ADBClickService;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by Administrator on 2017/8/4.
 */

public class Set008DataService implements Runnable{
    static List<String[]> datas;
    static Map<String,String> accounts;
    String currentAccount;
    AccessibilityService context;
    Map<String,String> record;
    ADBClickService adbService;
    public Set008DataService(AccessibilityService context, Map<String,String> record){
        this.context = context;
        this.record = record;
        adbService = new ADBClickService(context,record);
        datas = get008Datas();
        accounts = getWxAccounts();
        System.out.println("datas-->"+JSON.toJSONString(datas));
        System.out.println("accounts-->"+accounts);
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(1000);
            LogUtil.d("myService","-->写入008数据线程..."+Thread.currentThread().getName()+record);
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->root is null");
                AutoUtil.sleep(500);
                continue;
            }
            do008(root);
            doWxLogin(root);
        }
    }

    private void doWxLogin(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"写入数据")){
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动微信");
            AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            AutoUtil.sleep(5000);
        }
        adbService.clickXYByWindow("登录&注册",164,1192,"点击登录1",500);
        if(!AutoUtil.checkAction(record,"输入手机号")){
            adbService.setTextByWindow("用微信号/QQ号/邮箱登录",387,490,currentAccount,"输入手机号",0);
        }
        if(AutoUtil.findNodeInfosByText(root,currentAccount)!=null)
            adbService.clickXYByWindow("用微信号/QQ号/邮箱登录",361,751,"下一步",1000);
        System.out.println("密码---》"+accounts.get(currentAccount));
        if(AutoUtil.checkAction(record,"下一步"))
            adbService.setTextByWindow("用短信验证码登录",387,490,accounts.get(currentAccount),"输入密码",2000);
        adbService.clickXYByWindow("用短信验证码登录",361,751,"登录2",5000);
        adbService.clickXYByWindow("是&否",416,793,"不推荐通讯录",3000);
        if(AutoUtil.checkAction(record,"不推荐通讯录")){
            AutoUtil.clickXY(272,1228);
        }
        List<AccessibilityNodeInfo> node1 = root.findAccessibilityNodeInfosByText("微信团队");
        List<AccessibilityNodeInfo> node2 = root.findAccessibilityNodeInfosByText("腾讯新闻");
        if(node1!=null&&!node1.isEmpty()){
            AutoUtil.showToastByRunnable(context,"登录成功");
            AutoUtil.recordAndLog(record,"登录成功");
            AutoUtil.sleep(3000);
        }
    }

    private void do008(AccessibilityNodeInfo root){

        adbService.clickXYByWindow("工具箱",373,422,"点击图片",1000);
        if(AutoUtil.checkAction(record,"点击图片")||AutoUtil.checkAction(record,"清除数据")) {
            set008Data(root);
        }
        if(AutoUtil.checkAction(record,"登录成功")){
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动008");
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            AutoUtil.recordAndLog(record,"wx-启动008");
            AutoUtil.sleep(2000);
        }
        if(AutoUtil.checkAction(record,"wx-启动008")){
            AutoUtil.clickXY(50,1226);
            AutoUtil.sleep(1500);
            AutoUtil.clickXY(376,622);
            AutoUtil.recordAndLog(record,"清除数据");
            AutoUtil.sleep(4000);
        }
    }
    int currentIndex=2;
    int getIndex=2;
    private void set008Data(AccessibilityNodeInfo root){
        System.out.println("--->json-->"+JSON.toJSONString(datas.get(currentIndex)));
        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
        AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"总共:"+datas.size()+"  当前序号:"+currentIndex+" "+getIndex);
        if(list!=null&&list.getChildCount()>88){
            for(int i=1;i<91;i++){
                if(list.getChild(i).isEditable()&&getIndex<datas.get(currentIndex).length-1){
                    if(i==24||i==26||i==28||i==32||i==34) continue;
                    String data  = datas.get(currentIndex)[getIndex];
                    System.out.println("--test->"+list.getChild(i).getText()+"  "+i+"----"+data);
                    AutoUtil.performSetText(list.getChild(i),data,record,"写入"+i+" "+data);
                    getIndex = getIndex+1;
                }
            }
            getIndex=2;
            AutoUtil.sleep(500);
            AutoUtil.clickXY(90,359);
            currentAccount = datas.get(currentIndex)[0];
            if(currentIndex<datas.size()-1){
                currentIndex = currentIndex+1;
            }else {

            }
            AutoUtil.recordAndLog(record,"写入数据");
        }
    }

    /*private List<String[]> get008Datas(){
        List<String> list =  FileUtil.read008Data("/sdcard/A_hyj_008data/008data.txt");
        List<String[]> newList = new ArrayList<String[]>();
        for(String s:list){
            newList.add(JSONObject.parseObject(s,String[].class));
        }
       return newList;
    }*/
    private List<String[]> get008Datas(){
        List<String> list =  FileUtil.read008Data("/sdcard/微信号记录.txt");
        List<String[]> newList = new ArrayList<String[]>();
        for(String s:list){
            newList.add(s.split("----"));
        }
        return newList;
    }
    private Map<String,String>  getWxAccounts(){
        Map<String,String> accounts = new HashMap<String,String>();
        List<String> list =  FileUtil.read008Data("/sdcard/微信号记录.txt");
        for(String str:list){
            String[] ac = str.split("----");
            accounts.put(ac[0],ac[1]);
        }
        System.out.println("currentAccount-->"+accounts);
        return accounts;
    }
    /*private Map<String,String>  getWxAccounts(){
        Map<String,String> accounts = new HashMap<String,String>();
        List<String[]> list =   FileUtil.readConfFile("/sdcard/wxAccounts.txt");
        for(String[] str:list){
            accounts.put(str[0],str[1]);
        }
        System.out.println("currentAccount-->"+accounts);
        return accounts;
    }*/
    private int getStartIndex(){
        List<String[]> datas = get008Datas();
        return datas.indexOf("序列号");
    }
}
