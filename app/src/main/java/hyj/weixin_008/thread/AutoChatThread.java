package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.Constants;
import hyj.weixin_008.common.ConstantWxId;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.model.AddFrObj;
import hyj.weixin_008.model.AutoChatObj;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class AutoChatThread implements Runnable {
    AccessibilityService context;
    Map<String,String> record;
    AutoChatObj obj = new AutoChatObj();
    public AutoChatThread(AccessibilityService context){
        this.context = context;
        this.record = WeixinAutoHandler.record;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("AutoChatThread","AutoChatThread线程..."+Thread.currentThread().getName()+" ");
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->AutoChatThread线程 root is null");
                AutoUtil.sleep(500);
                continue;
            }

            //1、点击搜索
            List<AccessibilityNodeInfo> nodes1 = root.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/gh");//搜索父节点
            if(nodes1!=null&&nodes1.size()>0){
                AccessibilityNodeInfo node1 = nodes1.get(0).getChild(1);
                AutoUtil.performClick(node1,record,"搜索");
            }
            //2、输入wxid
            AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/hb");
            AutoUtil.performSetText(node2,obj.getWxid(),record,"输入wxid");
            //3、点击搜索出来wxid
            AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"微信号: "+obj.getWxid());
            AccessibilityNodeInfo node4 = AutoUtil.findNodeInfosByText(root,"查找微信号:"+obj.getWxid());
            if(node3!=null){
                AutoUtil.performClick(node3,record,"点击wxid");
            }else if(node4!=null){
                LogUtil.d("AutoChatThread线程",obj.getWxid()+"不存在。");
                if(obj.getWxidIndex()==obj.getWxids().size()-1){
                    AutoUtil.recordAndLog(record,"008注册处理完成");
                    return;
                }else {
                    obj.setWxidIndex(obj.getWxidIndex()+1);
                    continue;
                }
            }
            //4、填充发送内容
            findEditAndSetText(0);
            if(!AutoUtil.checkAction(record,Constants.CHAT_ACTION_05)){
                AccessibilityNodeInfo keyBtn = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/a6z");
                if(keyBtn!=null) {
                    LogUtil.d("autoChat","键盘按钮已获取");
                    AutoUtil.performClick(keyBtn, record, "点击键盘按钮",500);
                    findEditAndSetText(1);
                }else{
                    LogUtil.d("autoChat","键盘按钮 is null");
                }
            }

            //5、发送
            if(AutoUtil.checkAction(record,Constants.CHAT_ACTION_06)||AutoUtil.checkAction(record,Constants.CHAT_ACTION_05)){
                AccessibilityNodeInfo sendBtn = AutoUtil.findNodeInfosByText(root,"发送");
                AutoUtil.performClick(sendBtn,record,Constants.CHAT_ACTION_06,1000);
                obj.setMsgIndex(obj.getMsgIndex()+1);
                if(obj.getMsgIndex()==3){
                    if(obj.getWxidIndex()==obj.getWxids().size()-1){
                        AutoUtil.recordAndLog(record,"008注册处理完成");
                        return;
                    }
                    obj.setMsgIndex(0);
                    obj.setWxidIndex(obj.getWxidIndex()+1);
                    back2List(root);
                    System.out.println("getWxidIndex-->"+obj.getWxidIndex());
                    System.out.println("getMsgIndex-->"+obj.getMsgIndex());
                }
            }


        }
    }
    private void back2List(AccessibilityNodeInfo root){
        AccessibilityNodeInfo backBtn = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/h1");//左上角返回
        AutoUtil.performClick(backBtn,record,"全局返回");
    }
    private void findEditAndSetText(int tryCount){
        if(tryCount==10) return;
        AccessibilityNodeInfo editText = AutoUtil.findNodeInfosById(context.getRootInActiveWindow(),"com.tencent.mm:id/a71");//发送内容填充框
        if(editText!=null){
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,AutoUtil.createBuddleText(System.currentTimeMillis()+" tt"));
            AutoUtil.recordAndLog(record,Constants.CHAT_ACTION_05);
        }else if(tryCount!=0) {
            LogUtil.d("autoChat","输入框 is null "+tryCount);
            AutoUtil.sleep(500);
            findEditAndSetText(tryCount+1);
        }
    }
    public List<AccessibilityNodeInfo> inspectNode(AccessibilityNodeInfo root,String id){
        List<AccessibilityNodeInfo> node =  root.findAccessibilityNodeInfosByViewId(id);
        System.out.println("node id--->"+id);
        if(node!=null){
            System.out.println(" nodes size-->"+node.size()+ " id:"+id);
            for(AccessibilityNodeInfo n:node){
                System.out.println("node text-->"+n.getText()+" desc:"+n.getContentDescription()+" class:"+n.getClassName()
                        +" error:"+n.getError()+" isEdit:"+n.isEditable()+" idClick:"+n.isClickable()+" extra:"+n.getExtras()+" collectionInfo:"+n.getCollectionInfo());
            }
        }
        return node;
    }
}
