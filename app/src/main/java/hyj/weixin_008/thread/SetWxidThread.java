package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.util.DragImageUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.ParseRootUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class SetWxidThread implements Runnable {
    public static final String TAG = "SetWxidThread";
    AccessibilityService context;
    Map<String,String> record;
    public SetWxidThread(AccessibilityService context, Map<String,String> record){
        this.context = context;
        this.record = record;
    }
    int wxNum = 100;
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(1500);
            LogUtil.d("SetWxidThread","【SetWxidThread...】"+Thread.currentThread().getName()+" ");
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                LogUtil.d("SetWxidThread","SetWxidThread root is null");
                AutoUtil.sleep(500);
                continue;
            }
            ParseRootUtil.debugRoot(root);

            AccessibilityNodeInfo node0 = ParseRootUtil.getNodeByPathAndText(root,"0020","你的微信号成功设置为");
            if(node0!=null){
                AutoUtil.recordAndLog(record,"008登录成功");
                continue;
            }

            if(!AutoUtil.checkAction(record,"SetWxidThread点击设置")){
                AccessibilityNodeInfo node1 = ParseRootUtil.getNodeByPathAndText(root,"040","我");
                AutoUtil.performClick(node1,record,"SetWxidThread点击我");
                System.out.println("SetWxidThread node1-->"+node1);
            }

            AccessibilityNodeInfo node2 = ParseRootUtil.getNodeByPathAndText(root,"00570","设置");
            AccessibilityNodeInfo node21 = ParseRootUtil.getNodeByPathAndText(root,"00670","设置");
            AutoUtil.performClick(node2,record,"SetWxidThread点击设置");
            AutoUtil.performClick(node21,record,"SetWxidThread点击设置");
            System.out.println("SetWxidThread node2-->"+node2);

            AccessibilityNodeInfo node3 = ParseRootUtil.getNodeByPathAndText(root,"00260","帐号与安全");
            AutoUtil.performClick(node3,record,"SetWxidThread点击账号安全");
            System.out.println("SetWxidThread node3-->"+node3);

            AccessibilityNodeInfo node4 = ParseRootUtil.getNodeByPathAndText(root,"00210","微信号");
            AutoUtil.performClick(node4,record,"SetWxidThread点击微信号");
            System.out.println("SetWxidThread node4-->"+node4);

            if(AutoUtil.checkAction(record,"SetWxidThread点击微信号")||AutoUtil.checkAction(record,"SetWxidThread点击确认微信号已存在")){
                AccessibilityNodeInfo node5 = ParseRootUtil.getNodePath(root,"0032");
                AutoUtil.performSetText(node5,"axq"+wxNum,record,"SetWxidThread输入微信号");
                wxNum = getNextNum(wxNum);
                System.out.println("SetWxidThread node5-->"+node5);

            }

            if(AutoUtil.checkAction(record,"SetWxidThread输入微信号")){
                AccessibilityNodeInfo node6 = ParseRootUtil.getNodePath(root,"002");
                AutoUtil.performClick(node6,record,"SetWxidThread保存微信号");
                System.out.println("SetWxidThread node6-->"+node6);
            }

            AccessibilityNodeInfo node7 = ParseRootUtil.getNodeByPathAndText(root,"03","确定");
            AutoUtil.performClick(node7,record,"SetWxidThread点击确认微信号");
            System.out.println("SetWxidThread node7-->"+node7);

            AccessibilityNodeInfo node8 = ParseRootUtil.getNodeByPathAndText(root,"01","确定");
            AutoUtil.performClick(node8,record,"SetWxidThread点击确认微信号已存在");
            System.out.println("SetWxidThread node8-->"+node8);

        }
    }

    private int getNextNum(int num){
        num = num+1;
        while (String.valueOf(num).indexOf("0")>-1||String.valueOf(num).indexOf("1")>-1){
            num = num+1;
        }
        return num;
    }

}
