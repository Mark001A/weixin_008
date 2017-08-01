package hyj.weixin_008.util;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

import hyj.weixin_008.AutoUtil;

/**
 * Created by Administrator on 2017/7/27.
 */

public class WxUtil {
    public static boolean isRegisterWindow1(AccessibilityNodeInfo root){
        AccessibilityNodeInfo registerNode1 = AutoUtil.findNodeInfosByText(root,"注册");
        AccessibilityNodeInfo registerNode2 = AutoUtil.findNodeInfosByText(root,"登录");
        return registerNode1!=null&&registerNode2!=null;
    }
    public static boolean isRegisterWindow2(AccessibilityNodeInfo root){
        AccessibilityNodeInfo registerNode1 = AutoUtil.findNodeInfosByText(root,"注册");
        AccessibilityNodeInfo registerNode2 = AutoUtil.findNodeInfosByText(root,"登录");
        return registerNode1!=null&&registerNode2==null;
    }
    public static void clickRegisterBtn1(AccessibilityNodeInfo root,Map<String,String> record){
        if(isRegisterWindow1(root)){
            AutoUtil.clickXY(616,1217);
            AutoUtil.recordAndLog(record,"点击注册1");
        }
    }
    public static void setNamePhoneAndPwd(AccessibilityNodeInfo root,Map<String,String> record){
        if(AutoUtil.checkAction(record,"点击注册1")&&isRegisterWindow2(root)){
            AutoUtil.inputText("nihao..");
            AutoUtil.sleep(300);
            LogUtil.d("click","输入昵称");
            AutoUtil.clickXY(378,433);//点击手机号输入框
            AutoUtil.sleep(200);
            AutoUtil.clickXY(650,445);//点击手机号输入框清除
            AutoUtil.sleep(500);
            AutoUtil.inputText("17076156224");//输入手机号
            AutoUtil.sleep(1000);
            AutoUtil.clickXY(378,537);//点击密码框
            AutoUtil.sleep(300);
            AutoUtil.inputText("password..");//输入密码
            AutoUtil.sleep(1000);
            AutoUtil.clickXY(378,680);
            AutoUtil.recordAndLog(record,"点击注册2");
        }

    }
}
