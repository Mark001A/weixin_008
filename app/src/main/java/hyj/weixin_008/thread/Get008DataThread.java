package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;

import java.util.List;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.model.Get008Data;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by asus on 2017/8/17.
 */

public class Get008DataThread implements Runnable {
    Get008Data tempdata;
    AccessibilityService context;
    public Get008DataThread(AccessibilityService context,Get008Data tempdata){
        this.tempdata = tempdata;
        this.context = context;

    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("Get008DataThread","-->提取008历史数据线程..."+Thread.currentThread().getName()+" "+tempdata.getRecord());
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->root is null");
                AutoUtil.sleep(500);
                continue;
            }

            if(AutoUtil.checkAction(tempdata.getRecord(),"历史记录按钮界面")){
                AccessibilityNodeInfo historyNode = AutoUtil.findNodeInfosByText(root,"历史记录");
                if(historyNode!=null){
                    AutoUtil.performClick(historyNode,tempdata.getRecord(),"点击历史记录");
                    continue;
                }
            }

            if(AutoUtil.checkAction(tempdata.getRecord(),"点击历史记录")){
                List<AccessibilityNodeInfo> phoneList = root.findAccessibilityNodeInfosByViewId("com.soft.apk008v:id/listItem_tagName");
                if(phoneList==null||phoneList.size()==0){
                    System.out.println("--->历史记录为空");
                    continue;
                }
                AccessibilityNodeInfo phoneNode = getPhoneNodeByPhone(phoneList,tempdata.getPoneByThisIndex());
                if(phoneNode==null){
                    AutoUtil.performScroll(phoneList.get(0),tempdata.getRecord(),"下滚");
                    AutoUtil.recordAndLog(tempdata.getRecord(),"点击历史记录");
                    continue;
                }
                tempdata.setCurrentPhone(phoneNode.getText().toString());
                tempdata.setCurrentIndex(tempdata.getCurrentIndex()+1);
                AutoUtil.performClick(phoneNode,tempdata.getRecord(),"点击号码",400);
                continue;
            }
            if(AutoUtil.checkAction(tempdata.getRecord(),"点击号码")){
                AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
                if(list!=null&&list.getChildCount()>90){
                    String[] str = new String[list.getChildCount()+1];
                    for(int i=0;i<list.getChildCount();i++){
                        str[i]=list.getChild(i).getText()+"";
                        System.out.println("data--> "+i+" "+str[i]);
                    }
                    str[list.getChildCount()]= tempdata.getCurrentPhone();
                    //LogUtil.log008(JSON.toJSONString(str));
                    System.out.println("json-->"+JSON.toJSONString(str));
                    AutoUtil.recordAndLog(tempdata.getRecord(),"历史记录按钮界面");
                    AutoUtil.sleep(1000);
                }
            }
        }
        }
    private AccessibilityNodeInfo getPhoneNodeByPhone(List<AccessibilityNodeInfo> phoneList,String phone){
        if(phoneList==null||phoneList.size()==0) return null;
        for(int i=0,l=phoneList.size();i<l;i++){
            if(phone.equals(phoneList.get(i).getText()+"")){
                return phoneList.get(i);
            }
        }
        return null;
    }
}
