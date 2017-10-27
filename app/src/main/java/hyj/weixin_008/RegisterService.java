package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.common.ConstantWxId;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.model.RegObj;
import hyj.weixin_008.service.ADBClickService;
import hyj.weixin_008.service.PhoneNumberAPIService;
import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

import static android.content.Context.MODE_PRIVATE;
import static hyj.weixin_008.GlobalApplication.getContext;

/**
 * Created by Administrator on 2017/8/4.
 */

public class RegisterService implements Runnable{
    static List<String[]> datas;
    static Map<String,String> accounts;
    String currentAccount;
    AccessibilityService context;
    Map<String,String> record;
    ADBClickService adbService;
    int currentIndex;
    RegObj regObj;
    PhoneApi pa;
    public RegisterService(AccessibilityService context, Map<String,String> record,PhoneApi pa,RegObj regObj){
        this.context = context;
        this.record = record;
        this.pa = pa;
        this.regObj = regObj;
        adbService = new ADBClickService(context,record);
        datas = get008Datas();
        accounts = getWxAccounts();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("url",MODE_PRIVATE);
        String startLoginAccount = sharedPreferences.getString("startLoginAccount","");
        if(startLoginAccount!=null&&!"".equals(startLoginAccount)){
            System.out.println("startLoginAccount-->"+startLoginAccount);
            currentIndex = Integer.parseInt(startLoginAccount.split("-")[0]);
        }else{
            currentIndex=0;
        }
    }
    int countLongin=0;
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("myService","-->注册线程运行..."+Thread.currentThread().getName()+record);

            if(WeixinAutoHandler.IS_PAUSE){
                LogUtil.d("autoChat","暂停服务");
                AutoUtil.sleep(3000);
                continue;
            }
            if(WeixinAutoHandler.IS_NEXT_NONE){
                System.out.println("----跳转下一个");
                loginNext();
                LogUtil.login("exception",currentAccount+"-跳转下一个");
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
                    LogUtil.login("exception",currentAccount+"-登录失败（0，5）");
                }
            }

            if(record.get("recordAction").contains("008")||AutoUtil.checkAction(record,Constants.CHAT_LISTENING))
                do008(root);
            if(record.get("recordAction").contains("st"))
                //doVPN(root);
                AutoUtil.recordAndLog(record,"wx连接成功");
            if(record.get("recordAction").contains("wx"))
                doWxRegister(root);
        }
    }
    private void loginNext(){
        AutoUtil.recordAndLog(record,Constants.CHAT_LISTENING);
        AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动008");
        AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        WeixinAutoHandler.IS_NEXT_NONE = false;
    }

    private void doWxRegister(AccessibilityNodeInfo root){
        if(AutoUtil.checkAction(record,"wx连接成功")){
            countLongin =0;
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动微信");
            AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            AutoUtil.sleep(5000);
        }
        if(!AutoUtil.checkAction(record,"wx一键操作"))
            adbService.clickXYByWindow("登录&注册",255,1790,"wx点击登录1",500);
        if(!AutoUtil.checkAction(record,"wx输入手机号")){
            //adbService.setTextByWindow("用微信号/QQ号/邮箱登录",540,720,pa.getPhone(),"wx输入手机号",0);
            adbService.setTextByWindow("用微信号/QQ号/邮箱登录",540,720,"12569521584","wx输入手机号",0);
        }
        //if(AutoUtil.findNodeInfosByText(root,pa.getPhone())!=null)
            adbService.clickXYByWindow("用微信号/QQ号/邮箱登录",540,1115,"wx下一步",1000);
        if(AutoUtil.checkAction(record,"wx下一步"))
            adbService.clickXYByWindow("用短信验证码登录",267,885,"wx点击短信验证码登录",500);
            //adbService.setTextByWindow("用短信验证码登录",538,691,accounts.get(currentAccount),"wx输入密码",2000);
        if(adbService.clickXYByWindow("获取验证码",897,723,"wx点击获取验证码",1000)) return;
        if(!AutoUtil.checkAction(record,"wx点击注册2")&&adbService.clickXYByWindow("确认手机号码",818,1192,"wx确认手机号码",2000)){
            AutoUtil.clickXY(61,1863);
            AutoUtil.sleep(1500);
            AutoUtil.recordAndLog(record,"wx点击悬浮框");
            AutoUtil.sleep(800);
            return;
        }else {
            adbService.clickXYByWindow("确认手机号码",818,1192,"wx确认手机号码",2000);
        }
        AccessibilityNodeInfo node008 = AutoUtil.findNodeInfosByText(root,"一键操作");
        if(node008!=null){
            AutoUtil.clickXY(518,918);
            AutoUtil.recordAndLog(record,"wx一键操作");
            AutoUtil.sleep(1000);
            AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
        }
        if(AutoUtil.checkAction(record,"wx一键操作")){
            if(!adbService.clickXYByWindow("登录&注册",834,1790,"wx点击注册1",500)){
                AutoUtil.startAppByPackName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            }
            return;
        }
        AccessibilityNodeInfo textNode1 = AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG1);
        if(textNode1!=null&&!AutoUtil.checkAction(record,"wx点击注册2")&&pa.isPhoneIsAvailavle()){
            AccessibilityNodeInfo textNode2 = AutoUtil.findNodeInfosByText(root,"注册");
            AccessibilityNodeInfo textNode3 = AutoUtil.findNodeInfosByText(root,"昵称");
            AccessibilityNodeInfo textNode4 = root.findAccessibilityNodeInfosByText("手机号").get(1);
            AccessibilityNodeInfo textNode5 = AutoUtil.findNodeInfosByText(root,"密码");
            AutoUtil.performSetText(textNode3.getParent().getChild(1),String.valueOf(System.currentTimeMillis()).substring(10),record,"wx输入昵称");
            AutoUtil.performSetText(textNode4.getParent().getChild(1),pa.getPhone(),record,"wx手机号");
            pa.setZcPwd("www"+pa.getPhone().substring(6));
            AutoUtil.performSetText(textNode5.getParent().getChild(1),pa.getZcPwd(),record,"wx输入密码");
            AutoUtil.sleep(2000);
            AutoUtil.performClick(textNode2,record,"wx点击注册2");
            return;
        }

        /*if(pa.isValidCodeIsAvailavle()){
            if(adbService.setTextByWindow(ConstantWxId.REGMSG2,550,600,pa.getValidCode(),"wx输入验证码",1500))
                if(adbService.clickXYByWindow(ConstantWxId.REGMSG2,550,950,"wx输入验证码下一步",2000)) return;
        }*/
        if(1==1){
            if(adbService.setTextByWindow(ConstantWxId.REGMSG2,550,600,"152654","wx输入验证码",1500))
                if(adbService.clickXYByWindow(ConstantWxId.REGMSG2,550,950,"wx输入验证码下一步",2000)) return;
        }
        if(AutoUtil.checkAction(record,"wx确认手机号码")){
            if(AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG2)!=null&&!pa.isValidCodeIsAvailavle()){
                if(pa.getWaitValicodeTime()==30){
                    pa.setWaitValicodeTime(0);
                    AutoUtil.recordAndLog(record,"008登录异常");
                }
                LogUtil.d("WaitValicodeTim",pa.getWaitValicodeTime()+"");
                pa.setWaitValicodeTime(pa.getWaitValicodeTime()+1);
                return;
            }
        }
        if(AutoUtil.checkAction(record,"wx不是我的，继续注册")){
            if(AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG6)!=null
                    ||AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG8)!=null
                    ||AutoUtil.findNodeInfosByText(root,ConstantWxId.REGMSG9)!=null){
                AutoUtil.recordAndLog(record,"008登录异常");
                return;
            }
        }
        if(adbService.clickXYByWindow(ConstantWxId.REGMSG5,540,1100,"wx不是我的，继续注册",1000)) return;
        if(adbService.clickXYByWindow(ConstantWxId.REGMSG3,600,1220,"wx了解更多",1000)) return;
        if(adbService.clickXYByWindow(ConstantWxId.REGMSG4,540,1800,"wx以后再说",1000)) return;

        //adbService.clickXYByWindow("是&否",625,1190,"wx不推荐通讯录",3000);
        if(AutoUtil.checkAction(record,"wx以后再说")||AutoUtil.checkAction(record,"wx不是我的，继续注册")){
            AutoUtil.clickXY(400,1845);
            System.out.println("---->切换");
            AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"忽略"),record,"wx不推荐通讯录");
            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"微信团队");
            AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"应急联系人");
            if(node1!=null||node3!=null){
                countLongin =0;
                LogUtil.reg("reg",pa.getPhone()+"-"+pa.getZcPwd());
                AutoUtil.showToastByRunnable(context,"注册成功");
                AutoUtil.recordAndLog(record,"wx注册成功");
                pa.setRegSuccessphone(pa.getPhone());
                pa.setPhoneIsAvailavle(false);
                pa.setValidCodeIsAvailavle(false);
                AutoUtil.sleep(3000);
                AutoUtil.clickXY(946,1833);//点我
                if(!regObj.getZc2().equals("true")&&!regObj.getZc3().equals(true)){
                    AutoUtil.recordAndLog(record,"008注册处理完成");
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
            LogUtil.login("exception",currentAccount+"-"+accounts.get(currentAccount)+"-"+Constants.wx_Exception1);
            LogUtil.d("exception",Constants.wx_Exception1);
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
            LogUtil.login("exception",currentAccount+"-"+accounts.get(currentAccount)+"-"+errMsg);
        }
    }

    private void do008(AccessibilityNodeInfo root){

        if(AutoUtil.checkAction(record,"008注册处理完成")){
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            AutoUtil.recordAndLog(record,"008注册处理完成-启动008");
            AutoUtil.sleep(2000);
            return;
        }
        if(AutoUtil.checkAction(record,"008注册处理完成-启动008")||AutoUtil.checkAction(record,"008获取写入数据失败")){
            AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
            if(list!=null&&list.getChildCount()>90){
                String[] str = new String[list.getChildCount()+2];
                for(int i=0;i<list.getChildCount();i++){
                    str[i]=list.getChild(i).getText()+"";
                    System.out.println("data--> "+i+" "+str[i]);
                }
                str[list.getChildCount()]= pa.getZcPwd();
                str[list.getChildCount()+1]= pa.getRegSuccessphone();
                String jsonStr = JSON.toJSONString(str);

                Wx008Data wx008Data = new Wx008Data();
                wx008Data.setDatas(jsonStr);
                wx008Data.setPhone(pa.getRegSuccessphone());
                wx008Data.setWxPwd(pa.getZcPwd());
                wx008Data.setCreateTime(new Date());
                if(wx008Data.save()){
                    LogUtil.d("RegisterService","写入数据库:"+JSON.toJSONString(wx008Data));
                }

                LogUtil.log008(jsonStr);
                LogUtil.d("RegisterService","写入txt:"+jsonStr);
                AutoUtil.recordAndLog(record,"008写入成功注册数据完成");
                AutoUtil.sleep(1000);
            }else {
                AutoUtil.recordAndLog(record,"008获取写入数据失败");
                AutoUtil.sleep(2000);
                return;
            }
        }

       if(AutoUtil.checkAction(record,Constants.CHAT_LISTENING)){
           adbService.clickXYByWindow("工具箱",373,422,"008点击图片",2000);
       }

        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");

        if(AutoUtil.checkAction(record,"008一键操作")||AutoUtil.checkAction(record,"008没有可用号码")) {
            set008Data(root);
        }
        if(AutoUtil.checkAction(record,"008登录异常")){
            pa.setPhoneIsAvailavle(false);
            pa.setValidCodeIsAvailavle(false);
            AutoUtil.showToastByRunnable(GlobalApplication.getContext().getApplicationContext(),"启动008");
            AutoUtil.startAppByPackName("com.soft.apk008v","com.soft.apk008.LoadActivity");
            AutoUtil.recordAndLog(record,"008启动008");
            AutoUtil.sleep(2000);
        }
        //开始注册前和注册完成执行清除数据
        if(list!=null&&!AutoUtil.checkAction(record,"st写入数据")&&!AutoUtil.checkAction(record,"008没有可用号码")){
            AutoUtil.clickXY(61,1863);
            //AutoUtil.sleep(1500);
            AutoUtil.recordAndLog(record,"008点击悬浮框");
            AutoUtil.sleep(2500);
            return;
        }

        AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"一键操作");
        if(node1!=null){
            AutoUtil.clickXY(518,918);
            AutoUtil.recordAndLog(record,"008一键操作");
            AutoUtil.sleep(5000);
        }
    }
    private void set008Data(AccessibilityNodeInfo root){
        AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
        if(list!=null&&list.getChildCount()>90){
            AccessibilityNodeInfo generate =AutoUtil.findNodeInfosByText(root,"随机生成");
            AccessibilityNodeInfo save =AutoUtil.findNodeInfosByText(root,"保存");
            if(true){
            //if(pa.isPhoneIsAvailavle()){
                AutoUtil.performClick(generate,record,"008随机生成",1000);

                AutoUtil.performSetText(list.getChild(6),pa.getPhone(),record,"008写入"+pa.getPhone());
                AutoUtil.performSetText(list.getChild(36),"6.0",record,"008写入6.0");
                AutoUtil.performSetText(list.getChild(38),"23",record,"008写入23");

                AutoUtil.performClick(save,record,"008保存",2000);
                AutoUtil.recordAndLog(record,"st写入数据");
            }else{
                AutoUtil.recordAndLog(record,"008没有可用号码");
                AutoUtil.sleep(1000);
                return;
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
                System.out.println("--->断开连接等待");
                AutoUtil.sleep(9000);
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
