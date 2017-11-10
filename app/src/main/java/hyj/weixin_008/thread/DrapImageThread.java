package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.os.Environment;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.Constants;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.model.AutoChatObj;
import hyj.weixin_008.util.DragImageUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.ParseRootUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class DrapImageThread implements Runnable {
    AccessibilityService context;
    Map<String,String> record;
    public DrapImageThread(AccessibilityService context, Map<String,String> record){
        this.context = context;
        this.record = record;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("DrapImageThread","拖动方块DrapImageThread..."+Thread.currentThread().getName()+" ");
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                LogUtil.d("DrapImageThread","DrapImageThread root is null");
                AutoUtil.sleep(500);
                continue;
            }

            //方块处理
      if(1==1||AutoUtil.checkAction(record,"wx开始安全验证")){
            AccessibilityNodeInfo fkNode = ParseRootUtil.getNodePath(root,"0000000000");
            AccessibilityNodeInfo errorNode = ParseRootUtil.getNodePath(root,"0000000003");
           if(AutoUtil.checkAction(record,"wx拖动方块")){
               if(errorNode!=null&&"请控制拼图块对齐缺口".equals(errorNode.getText()+"")){
                   AccessibilityNodeInfo refreshNode = ParseRootUtil.getNodePath(root,"0030");
                   AutoUtil.performClick(refreshNode,record,"wx刷新方块");
                   LogUtil.d("DrapImageThread","wx刷新方块");
               }else{
                   LogUtil.d("DrapImageThread","等待拖动结果");
                   continue;
               }
           }
            if(fkNode!=null&&"拖动下方滑块完成拼图".equals(fkNode.getContentDescription().toString())){
                AutoUtil.sleep(2000);
                String  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Screenshots";
                LogUtil.d("DrapImageThread","path-->"+path);
                File file = new File(path);
                File[] files = file.listFiles();
                //清空文件
                if(files!=null&&files.length>0){
                    for(File f:files){
                        System.out.println();
                        //f.delete();
                    }
                }
                //截图
                //AutoUtil.execShell("input keyevent 120");
                AutoUtil.sleep(2000);

                File file1 = new File(path);
                File[] files1 = file1.listFiles();
                if(files1!=null&&files1.length>0){
                    File file2 = files1[0];
                    if(file2!=null&&file2.length()>0){
                        int distance = DragImageUtil.getDragDistance(path+"/"+file2.getName());
                        LogUtil.d("DrapImageThread","distance-->"+distance);
                        AutoUtil.execShell("input swipe 232 1040 "+distance+" 1040");
                        AutoUtil.recordAndLog(record,"wx拖动方块");
                    }
                }

            }
        }

        }
    }

}
