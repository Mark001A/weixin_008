package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.common.ConstantWxId;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.flowWindow.MyWindowManager;
import hyj.weixin_008.model.RegObj;
import hyj.weixin_008.service.ADBClickService;
import hyj.weixin_008.thread.AddFriendThread;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

import static android.content.Context.MODE_PRIVATE;
import static hyj.weixin_008.GlobalApplication.getContext;

/**
 * Created by Administrator on 2017/8/4.
 */

public class Set008DataService implements Runnable{
    AccessibilityService context;
    Map<String,String> record;
    ADBClickService adbService;
    RegObj regObj;
    public Set008DataService(AccessibilityService context, Map<String,String> record,RegObj regObj){
        this.context = context;
        this.record = record;
        this.regObj = regObj;
        this.adbService = new ADBClickService(context,record);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("url",MODE_PRIVATE);
        String startLoginIndex = sharedPreferences.getString("startLoginIndex","");
        if(startLoginIndex!=null&&!"".equals(startLoginIndex)){
            regObj.setCurrentIndex(Integer.parseInt(startLoginIndex.split("-")[0]));
        }
    }
    int countLongin=0;
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("myService","-->养号线程运行..."+Thread.currentThread().getName()+record);

            if(regObj.getWx008Datas().size()==0){
                AutoUtil.recordAndLog(record,"没有008数据");
                return;
            }

            if(WeixinAutoHandler.IS_PAUSE){
                LogUtil.d("autoChat","暂停服务");
                AutoUtil.sleep(3000);
                continue;
            }
            if(WeixinAutoHandler.IS_NEXT_NONE){
                System.out.println("----跳转下一个");
                loginNext();
                LogUtil.login(regObj.getCurrentIndex()+" exception", regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+"-"+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId()+"-跳转下一个");
                continue;
            }


            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->root is null");
                AutoUtil.sleep(500);
                continue;
            }

            AccessibilityNodeInfo node4 = AutoUtil.findNodeInfosById(root,ConstantWxId.TIPS);
            if(node4!=null){
                String tips = node4.getText().toString();
                System.out.println("tips--->"+tips);
                AutoUtil.sleep(500);
                continue;
            }
            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"等待数据恢复");
            AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosByText(root,"正在登录...");
            AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"请稍后...");
            if(node1!=null||node2!=null||node3!=null){
                System.out.println("--->请稍后...");
                AutoUtil.sleep(500);
                continue;
            }

            if(AutoUtil.checkAction(record,"wx登录2")){
                countLongin = countLongin+1;
                System.out.println("countLongin-->"+countLongin);
                if(countLongin>5){
                    loginNext();
                    LogUtil.login(regObj.getCurrentIndex()+" exception",regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+"-"+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId()+"-登录失败（0，5）");
                }
            }

            if(record.get("recordAction").contains("008")||AutoUtil.checkAction(record,Constants.CHAT_LISTENING))
                do008(root);
            if(record.get("recordAction").contains("st"))
                doVPN(root);
            if(record.get("recordAction").contains("wx"))
                doWxLogin(root);
        }
    }
    private void loginNext(){
        AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
        AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        WeixinAutoHandler.IS_NEXT_NONE = false;
    }

    private void doWxLogin(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"wx连接成功")){
            countLongin =0;
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动微信");
            AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            AutoUtil.sleep(5000);
        }
        if(!AutoUtil.checkAction(record,"wx登录2"))
            adbService.clickXYByWindow("登录&注册",255,1790,"wx点击登录1",500);
        if(!AutoUtil.checkAction(record,"wx输入手机号")&&!AutoUtil.checkAction(record,"wx下一步")){
            adbService.setTextByWindow("用微信号/QQ号/邮箱登录",540,720,regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone(),"wx输入手机号",0);
        }
        if(AutoUtil.findNodeInfosByText(root,regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone())!=null){
            if(!AutoUtil.checkAction(record,"wx下一步")){
                AutoUtil.sleep(5000);
            }
            adbService.clickXYByWindow("用微信号/QQ号/邮箱登录",540,1115,"wx下一步",2000);
        }
        if(AutoUtil.checkAction(record,"wx下一步"))
            adbService.setTextByWindow("用短信验证码登录",538,691,regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxPwd(),"wx输入密码",2000);
        if(adbService.clickXYByWindow("用短信验证码登录",563,995,"wx登录2",2000)) return;
        adbService.clickXYByWindow("是&否",625,1190,"wx不推荐通讯录",3000);
        if(AutoUtil.checkAction(record,"wx不推荐通讯录")){
            AutoUtil.clickXY(400,1845);
            System.out.println("---->切换");
            AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"忽略"),record,"wx不推荐通讯录");
            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"微信团队");
            AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"应急联系人");
            AccessibilityNodeInfo node4 = AutoUtil.findNodeInfosByText(root,"新的朋友");
            AccessibilityNodeInfo node5 = AutoUtil.findNodeInfosByText(root,"群聊");
            if(node1!=null||node3!=null||node4!=null||node5!=null){
                countLongin =0;
                LogUtil.login(regObj.getCurrentIndex()+" success",regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+" "+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId());
                AutoUtil.showToastByRunnable(context,"登录成功");
                AutoUtil.recordAndLog(record,"008登录成功");
                if("true".equals(regObj.getZc2())){//写个性签名
                    AutoUtil.recordAndLog(WeixinAutoHandler.record,"qm");
                }else if("true".equals(regObj.getZc3())){//发朋友圈
                    AutoUtil.recordAndLog(WeixinAutoHandler.record,"pyq");
                }
                AutoUtil.sleep(1000);
                if("true".equals(regObj.getAddSpFr())){
                    AutoUtil.recordAndLog(record,"登录成功添加好友");
                    new Thread(new AddFriendThread(context)).start();
                }
            }
            return;
        }
        wxException(root);
    }
    private void wxException(AccessibilityNodeInfo root){
        if(!AutoUtil.checkAction(record,"wx登录2")) return;
        AccessibilityNodeInfo node = AutoUtil.findNodeInfosByText(root,Constants.wx_Exception1);
        if(node!=null){
            LogUtil.login(regObj.getCurrentIndex()+" exception",regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+" "+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId()+"-"+Constants.wx_Exception1);
            LogUtil.d("exception",Constants.wx_Exception1);
            AutoUtil.recordAndLog(record,"008登录异常");
        }

        AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG10);
        if(node1!=null){
            LogUtil.login(regObj.getCurrentIndex()+" exception",regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+" "+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId()+"-"+ConstantWxId.REGMSG10);
            LogUtil.d("exception",ConstantWxId.REGMSG10);
            AutoUtil.recordAndLog(record,"008登录异常");
        }

        AccessibilityNodeInfo loginNode = AutoUtil.findNodeInfosByText(context.getRootInActiveWindow(),"登录");
        if(loginNode!=null){
            String errMsg = "登录错误（0，5）";
            AutoUtil.recordAndLog(record,"008登录异常");
            AccessibilityNodeInfo expNode = AutoUtil.findNodeInfosById(context.getRootInActiveWindow(),ConstantWxId.EXPMSG);
            if(expNode!=null){
                errMsg = expNode.getText().toString();
            }
            AccessibilityNodeInfo expNode1 = AutoUtil.findNodeInfosByText(context.getRootInActiveWindow(),"手机不在身边？");
            if(expNode1!=null){
                errMsg = "手机不在身边？";
            }
            LogUtil.d("exception",errMsg);
            LogUtil.login(regObj.getCurrentIndex()+" exception",regObj.getWx008Datas().get(regObj.getCurrentIndex()).getPhone()+" "+regObj.getWx008Datas().get(regObj.getCurrentIndex()).getWxId()+"-"+errMsg);
        }
    }

    private void do008(AccessibilityNodeInfo root){

       if(AutoUtil.checkAction(record,Constants.CHAT_LISTENING)){
           adbService.clickXYByWindow("工具箱",373,422,"008点击图片",2000);
       }

        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");

        if(AutoUtil.checkAction(record,"008一键操作")) {
            set008Data(root);
        }
        if(AutoUtil.checkAction(record,"008登录成功")||AutoUtil.checkAction(record,"008登录异常")||AutoUtil.checkAction(record,"008注册处理完成")){
            if(regObj.getCurrentIndex()<regObj.getDatas().size()-1){
                regObj.setCurrentIndex(regObj.getCurrentIndex()+1);
            }else {

            }

            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动008");
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            AutoUtil.recordAndLog(record,"008启动008");
            AutoUtil.sleep(2000);
        }

        if(list!=null&&!AutoUtil.checkAction(record,"st写入数据")){
            AutoUtil.clickXY(61,1863);
            AutoUtil.recordAndLog(record,"008点击悬浮框");
            AutoUtil.sleep(3500);
            return;
        }

        AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"一键操作");
        if(node1!=null){
            AutoUtil.clickXY(518,918);
            AutoUtil.recordAndLog(record,"008一键操作");
            AutoUtil.sleep(4000);
        }
    }
    private void set008Data(AccessibilityNodeInfo root){
        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
        if(list!=null){
            System.out.println("--list-getChildCount->"+list.getChildCount());
        }
        if(list!=null&&list.getChildCount()>90){
            for(int i=1;i<91;i++){
                if(list.getChild(i).isEditable()){
                    String data;
                    if(regObj.getDatas().get(regObj.getCurrentIndex())[1].contains("历史记录")){//红米2s提取的008数据
                        data  = regObj.getDatas().get(regObj.getCurrentIndex())[i+1];
                    }else {
                        data  = regObj.getDatas().get(regObj.getCurrentIndex())[i];
                    }
                    System.out.println("-rr->"+i+" "+data);
                    AutoUtil.performSetText(list.getChild(i),data,record,"008写入"+i+" "+data);
                }
            }
            AutoUtil.recordAndLog(record,"008写入数据完成");
            String msg = "总共:"+regObj.getDatas().size()+"  当前序号:"+regObj.getCurrentIndex();
            LogUtil.d("number",msg);
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),msg);
            if(AutoUtil.checkAction(record,"008写入数据完成")){
                AccessibilityNodeInfo save =AutoUtil.findNodeInfosByText(root,"保存");
                AutoUtil.performClick(save,record,"st保存",2500);
                AutoUtil.recordAndLog(record,"st写入数据");
            }

        }
    }
    String vpnIndex="1";
    private void doVPN(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"st写入数据")){
            AutoUtil.recordAndLog(record,"st设置VPN");
            AutoUtil.showToastByRunnable(context.getApplicationContext(),"设置VPN--"+vpnIndex);
            AutoUtil.opentActivity(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            AutoUtil.sleep(500);
        }
        AccessibilityNodeInfo linkText = AutoUtil.findNodeInfosById(context.getRootInActiveWindow(),"android:id/summary");
        if(linkText!=null&&linkText.getText().toString().equals("正在连接...")){
            AutoUtil.showToastByRunnable(GlobalApplication.getContext(),"正在连接...");
            System.out.println("--->正在连接..");
            AutoUtil.sleep(1000);
            return;
        }
       if(AutoUtil.checkAction(record,"st点击连接")){
            AccessibilityNodeInfo link =AutoUtil.findNodeInfosByText(context.getRootInActiveWindow(),"已连接");
            if(link!=null){
                AutoUtil.recordAndLog(record,"wx连接成功");
                AutoUtil.showToastByRunnable(GlobalApplication.getContext(),"启动微信");
                AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                return;
            }
           AccessibilityNodeInfo linkText5 = AutoUtil.findNodeInfosById(context.getRootInActiveWindow(),"android:id/summary");
           if(linkText5!=null){
               System.out.println("linkText5-->"+linkText5.getText());
           }
           if(linkText5!=null&&(linkText5.getText().toString().equals("PPTP VPN")||linkText5.getText().toString().equals("失败"))){
               AutoUtil.clickXY(522,738);
               AutoUtil.recordAndLog(record,"st点击连接");
               AutoUtil.sleep(2000);
               return;
           }
        }

        //clickTextXY1(538,890,"st其他连接方式","miui:id/action_bar_title","设置",100);
        //clickTextXY1(514,425,"st点击VPN","miui:id/action_bar_title","其他连接方式",800);
        clickTextXY1(514,425,"st点击VPN","miui:id/action_bar_title","无线和网络",800);

        if(AutoUtil.checkAction(record,"st点击VPN")||AutoUtil.checkAction(record,"st弹出")||AutoUtil.checkAction(record,"st设置VPN")){

            AccessibilityNodeInfo linkText4 = AutoUtil.findNodeInfosById(context.getRootInActiveWindow(),"android:id/summary");
            if(linkText4!=null){
                if(linkText4.getText().toString().equals("已连接")){
                    AutoUtil.clickXY(522,738);
                    AutoUtil.recordAndLog(record,"st弹出");
                    AutoUtil.sleep(1500);

                }else if (linkText4.getText().toString().equals("PPTP VPN")||linkText4.getText().toString().equals("失败")){
                    AutoUtil.clickXY(522,738);
                    AutoUtil.recordAndLog(record,"st点击连接");
                    AutoUtil.sleep(2000);
                }
            }
        }
        if(AutoUtil.checkAction(record,"st弹出")){
            AccessibilityNodeInfo dkNode = AutoUtil.findNodeInfosByText(context.getRootInActiveWindow(),"断开连接");
            if(dkNode!=null){
                AutoUtil.clickXY(756,1792);
                AutoUtil.recordAndLog(record,"st断开");
                AutoUtil.sleep(1000);
            }
            if(AutoUtil.checkAction(record,"st断开")){
                AutoUtil.clickXY(522,738);
                AutoUtil.recordAndLog(record,"st点击连接");
                AutoUtil.sleep(2000);
            }
        }
    }

    //先判断所在页面，在点击操作
    private void clickTextXY1(int x,int y,String action,String titleId,String title,int milliSeconds){
        AccessibilityNodeInfo root = context.getRootInActiveWindow();
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

}
