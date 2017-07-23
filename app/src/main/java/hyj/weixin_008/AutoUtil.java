package hyj.weixin_008;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.util.LogUtil;


/**
 * Created by asus on 2017/5/13.
 */

public class AutoUtil {
    //休眠毫秒
    public static  void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void performSetText(AccessibilityNodeInfo nodeInfo,String text,Map<String,String> record,String recordAction){
        if(nodeInfo == null) return;
        if(nodeInfo.isEditable()){
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,createBuddleText(text));
        }else{
            performSetText(nodeInfo.getParent(),text,record,recordAction);
        }
        recordAndLog(record,recordAction);
    }
    //执行点击、记录下次操作、并打印日志、休眠
    public static void performClick(AccessibilityNodeInfo nodeInfo,Map<String,String> record, String recordAction, long ms) {
        if(nodeInfo == null)  return;
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            recordAndLog(record,recordAction);
            //record.put("recordAction",recordAction);
            //System.out.println("------>"+record);
            sleep(ms);
        } else {
            performClick(nodeInfo.getParent(),record,recordAction,ms);
        }
    }
    //执行点击、记录下次操作、并打印日志
    public static void performClick(AccessibilityNodeInfo nodeInfo,Map<String,String> record, String recordAction) {
        if(nodeInfo == null)  return;
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            recordAndLog(record,recordAction);
            //record.put("recordAction",recordAction);
            //System.out.println("------>"+record);
        } else {
            performClick(nodeInfo.getParent(),record,recordAction);
        }
    }
    public static void performScroll(AccessibilityNodeInfo nodeInfo,Map<String,String> record, String recordAction) {
        if(nodeInfo == null)  return;
        if(nodeInfo.isScrollable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            recordAndLog(record,recordAction);
        } else {
            performScroll(nodeInfo.getParent(),record,recordAction);
        }
    }
    //执行点击、记录下次操作、并打印日志
    public static void performClickAndExpect(AccessibilityNodeInfo nodeInfo,Map<String,String> record, String recordAction
            ,AccessibilityService context,AccessibilityNodeInfo rootNode,String id,String text) {
        performClick(nodeInfo,record,recordAction);
        if(rootNode==null) return;
        rootNode = context.getRootInActiveWindow();
        if(id!=null&&text!=null){

        }

    }
    //通过文本查找节点
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        if(nodeInfo==null) return null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) return null;
        return list.get(0);
    }
    //通过文本查找节点
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String id) {
        if(nodeInfo==null) return null;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if(list == null || list.isEmpty()) return null;
        return list.get(0);
    }
    //返回
    public static void performBack(AccessibilityService service,Map<String,String> record, String recordAction) {
        if(service == null)  return;
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        recordAndLog(record,recordAction);
        //record.put("recordAction",recordAction);
        //System.out.println("------>"+record);
    }
    //根据id和text查找节点
    public static AccessibilityNodeInfo fineNodeByIdAndText(AccessibilityNodeInfo nodeInfo,String id,String text){
        AccessibilityNodeInfo result = null;
        if(nodeInfo==null) return result;
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if(nodes == null || nodes.isEmpty()) return result;
        for(AccessibilityNodeInfo node:nodes){
            String name = node.getText()+"";
            if(name.equals(text)){
                result = node;
                break;
            }
        }
        return result;
    }
    //根据id和text查找节点,并确认是否获取到，此方法用于当前窗口发生变化
    public static AccessibilityNodeInfo fineNodeByIdAndTextCheck(AccessibilityNodeInfo node,String id,String text,
                                             AccessibilityService context,Map<String,String> record, String recordAction){
        AccessibilityNodeInfo result = null;
        int count=0;
        while (count<6){
            node = context.getRootInActiveWindow();
            if(text==null){
                result = findNodeInfosById(node,id);
            }else if(id==null){
                result = findNodeInfosByText(node,text);
            }else{
                result = fineNodeByIdAndText(node,id,text);
            }
            if(result!=null)
                break;
            count = count +1;
            sleep(500*count);
        }
        if(result==null)
            recordAndLog(record,recordAction);
        return result;
    }
    //获取聊天窗口内容
    public static String getChatWindowMsg(List<AccessibilityNodeInfo> receviceMsgs,int msgNum){
        String rMsg = "";
        if(receviceMsgs!=null){
            //判断当前屏幕是否显示完新接受的消息，显示不全需要滚屏
            if(receviceMsgs.size()>=msgNum){
                for(int j=msgNum;j>0;j--){
                    rMsg=rMsg+receviceMsgs.get(receviceMsgs.size()-j).getText()+"\n";
                }
                System.out.println("收到信息为-->"+rMsg);
            }else{
                //需要滚动屏幕
            }
        }
        return rMsg;
    }
    //创建buggle文本
    public static Bundle createBuddleText(String inputText){
        Bundle inputContent = new Bundle();
        inputContent.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,inputText);
        return inputContent;
    }
    public static void createPaste(String text){
        ClipboardManager clipboard = (ClipboardManager) GlobalApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }
    public static void createPasteInHandler(final AccessibilityNodeInfo phoneNode,final String text,final Map<String,String> record, final String recordAction){
        if(phoneNode==null) return;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                createPaste(text);
                phoneNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                recordAndLog(record,recordAction);
            }
        });
    }
    //返回执行状态并打印日志
    public static void recordAndLog(Map<String,String> record, String recordAction){
        record.put("recordAction",recordAction);
        LogUtil.d("record",recordAction);
    }
    //核对状态
    public static boolean checkAction(Map<String,String> record, String recordAction){
        if(recordAction.equals(record.get("recordAction")))
            return true;
        return false;
    }

    public static void  execShell(String cmd){
        try {
            Process process  = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showToastByRunnable(final Context context, final CharSequence text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void startAppByPackName(String packageName,String activity){
        Intent intent = new Intent();
        ComponentName cmp=new ComponentName(packageName,activity);
        //ComponentName cmp=new ComponentName("com.soft.apk008v","com.soft.apk008.LoadActivity");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        GlobalApplication.getContext().startActivity(intent);
    }
    public static void startSysSetting(){
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalApplication.getContext().startActivity(intent);
    }

    public static List<String> getSomeMsgs(){
        List<String> list = new ArrayList<String>();
        list.add("你好.");
        list.add("在。。");
        list.add("可以的。。");
        list.add("我不介意。。");
        list.add("没有讲过。。");
        list.add("是不是");
        list.add("应该不是");
        list.add("什么时候");
        list.add("大概今晚9点左右");
        list.add("我会按时");
        list.add("嗯，好");
        list.add("今晚去不去");
        list.add("同学过来");
        list.add("好久没去");
        list.add("是这个时候");
        list.add("早点回来");
        list.add("出发了");
        list.add("可以没有");
        return list;
    }

}
