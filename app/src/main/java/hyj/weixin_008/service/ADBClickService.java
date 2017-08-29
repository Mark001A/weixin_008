package hyj.weixin_008.service;

import android.accessibilityservice.AccessibilityService;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.RootShellCmd;

/**
 * Created by asus on 2017/8/5.
 */

public class ADBClickService {
    static final String TAG = "ADBClickService";
    AccessibilityService context;
    Map<String,String> record;
    public ADBClickService(AccessibilityService context, Map<String,String> record){
        this.record = record;
        this.context = context;
    }
    public boolean clickXYByWindow(String windowNodeTexts,int clickX,int clickY,String action,long sleepTime){
        if(!checkWindow(windowNodeTexts,action)) return false;
        AutoUtil.clickXY(clickX,clickY);
        AutoUtil.recordAndLog(record,action);
        AutoUtil.sleep(sleepTime);
        return true;
    }
    public boolean setTextByWindow(String windowNodeTexts,int clickX,int clickY,String inputText,String action,long sleepTime){
        if(!checkWindow(windowNodeTexts,action)) return false;
        AutoUtil.clickXY(clickX,clickY);
        AutoUtil.sleep(1500);
        AutoUtil.inputText(inputText);
        AutoUtil.recordAndLog(record,action);
        AutoUtil.sleep(sleepTime);
        return true;
    }
    public boolean checkWindow(String windowNodeTexts,String action){
        AccessibilityNodeInfo root = context.getRootInActiveWindow();
        if(root==null){
            //LogUtil.d(TAG,"action "+action+" root is null!");
            return false;
        }
        if(windowNodeTexts.contains("&")){
            String[] str = windowNodeTexts.split("&");
            for(String text:str){
                AccessibilityNodeInfo node = AutoUtil.findNodeInfosByText(root,text);
                if(node==null){
                    //LogUtil.d(TAG,"node's text  "+text+" is not find!");
                    return false;
                }
            }

        }else{
            AccessibilityNodeInfo node = AutoUtil.findNodeInfosByText(root,windowNodeTexts);
            if(node==null){
                //LogUtil.d(TAG,"node's text  "+windowNodeTexts+" is not find!");
                return false;
            }
        }
        return true;
    }
}
