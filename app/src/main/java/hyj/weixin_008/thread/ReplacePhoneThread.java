package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;
import hyj.weixin_008.util.ParseRootUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class ReplacePhoneThread implements Runnable {
    public static final String TAG = "ReplacePhoneThread";
    AccessibilityService context;
    Map<String,String> record;
    public ReplacePhoneThread(AccessibilityService context, Map<String,String> record){
        this.context = context;
        this.record = record;
    }
    String phone ="",code="";
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            if(!AutoUtil.actionContains(record,"ReplacePhoneThread")&&!AutoUtil.checkAction(record,"6")) continue;
            if ("".equals(phone)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String HOST = "120.78.134.230:80";
                        String url = "http://"+HOST+"/getValidCode";
                        LogUtil.d("url",url);
                        String str = OkHttpUtil.okHttpGet(url);
                        if(str.indexOf(",")>-1){
                            phone = str.substring(0,str.indexOf(","));
                        }
                        LogUtil.d("resBody",str);
                        LogUtil.d("phone",phone);
                    }
                }).start();

            }
            if ("".equals(code)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String HOST = "120.78.134.230:80";
                        String url = "http://"+HOST+"/getValidCode";
                        LogUtil.d("url",url);
                        String str = OkHttpUtil.okHttpGet(url);
                        if(str.indexOf(",")>-1&&str.indexOf(",")<str.length()-1){
                            code = str.substring(str.indexOf(",")+1);
                        }
                        LogUtil.d("resBody",str);
                        LogUtil.d("code",code);
                    }
                }).start();

            }

            LogUtil.d("ReplacePhoneThread","【ReplacePhoneThread...】"+Thread.currentThread().getName()+" phone:"+record.get("phone"));
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                LogUtil.d("ReplacePhoneThread","ReplacePhoneThread root is null");
                AutoUtil.sleep(500);
                continue;
            }
            ParseRootUtil.debugRoot(root);

            if(root.getPackageName().toString().indexOf("tencent")==-1) continue;

            if(AutoUtil.checkAction(record,"6")){
                AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosByText(root,"我");
                AutoUtil.performClick(node2,record,"ReplacePhoneThread点击我");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread点击我")){
                AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"设置");
                AutoUtil.performClick(node3,record,"ReplacePhoneThread设置");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread设置")){
                AccessibilityNodeInfo node4 = AutoUtil.findNodeInfosByText(root,"帐号与安全");
                AutoUtil.performClick(node4,record,"ReplacePhoneThread帐号与安全");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread帐号与安全")){
                AccessibilityNodeInfo node5 = AutoUtil.findNodeInfosByText(root,"手机号");
                AutoUtil.performClick(node5,record,"ReplacePhoneThread手机号");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread手机号")){
                AccessibilityNodeInfo node6 = AutoUtil.findNodeInfosByText(root,"更换手机号");
                AutoUtil.performClick(node6,record,"ReplacePhoneThread更换手机号");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread更换手机号")){
                AccessibilityNodeInfo node7 = ParseRootUtil.getNodePath(root,"0032");
                AutoUtil.performSetText(node7,phone,record,"ReplacePhoneThread输入手机号");
                continue;
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread输入手机号")){
                phone = "";
                AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosByText(root,"下一步");
                AutoUtil.performClick(node8,record,"ReplacePhoneThread下一步");
                continue;
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread下一步")||AutoUtil.checkAction(record,"ReplacePhoneThread验证码不正确确定")){
                if(AutoUtil.checkAction(record,"ReplacePhoneThread验证码不正确确定"))  AutoUtil.sleep(2000);
                AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosByText(root,"请输入验证码");
                if(node8!=null){
                    AccessibilityNodeInfo node7 = ParseRootUtil.getNodePath(root,"0021");
                    AutoUtil.performSetText(node7,code,record,"ReplacePhoneThread输入验证码");
                }
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread输入验证码")){
                AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosByText(root,"下一步");
                AutoUtil.performClick(node8,record,"ReplacePhoneThread输入验证码下一步");
            }

            if(AutoUtil.checkAction(record,"ReplacePhoneThread输入验证码下一步")){
                AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosByText(root,"完成");
                if(node8!=null){
                    code = "";
                    AutoUtil.recordAndLog(record,"008登录成功");
                }
            }

            AccessibilityNodeInfo node9 = AutoUtil.findNodeInfosByText(root,"验证码不正确，请重新输入");
            if(node9!=null){
                code="";
                AccessibilityNodeInfo node10 = AutoUtil.findNodeInfosByText(root,"确定");
                AutoUtil.performClick(node10,record,"ReplacePhoneThread验证码不正确确定");
            }

        }
    }



}
