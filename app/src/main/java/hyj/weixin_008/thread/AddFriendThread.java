package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.Constants;
import hyj.weixin_008.common.ConstantWxId;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.model.AddFrObj;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class AddFriendThread implements Runnable {
    AccessibilityService context;
    Map<String,String> record;
    AddFrObj obj = new AddFrObj();
    public AddFriendThread(AccessibilityService context){
        this.context = context;
        this.record = WeixinAutoHandler.record;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(100);
            LogUtil.d("AddFriendThread","AddFriendThread线程..."+Thread.currentThread().getName()+" ");
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->root is null");
                AutoUtil.sleep(500);
                continue;
            }
            AccessibilityNodeInfo nodeTip = AutoUtil.findNodeInfosByText(root,"正在查找联系人...");
            AccessibilityNodeInfo nodeTip1 = AutoUtil.findNodeInfosByText(root,"正在添加...");
            AccessibilityNodeInfo nodeTip2 = AutoUtil.findNodeInfosByText(root,"正在发送...");
            if(nodeTip!=null||nodeTip1!=null||nodeTip2!=null){
                System.out.println("--->正在查找联系人....正在发送..正在添加..");
                AutoUtil.sleep(500);
                continue;
            }

            AccessibilityNodeInfo exceptionNode = AutoUtil.findNodeInfosByText(root,"没有找到\""+obj.getOneWx()+"\"相关结果");
            if(exceptionNode!=null){
                AutoUtil.performBack(context,record,"没有找到相关结果返回1");
                AutoUtil.sleep(2000);
                AutoUtil.performBack(context,record,"没有找到相关结果返回2");
                AutoUtil.sleep(2000);
                AutoUtil.performBack(context,record,"没有找到相关结果返回3");
            }
            //List<AccessibilityNodeInfo> node1 = inspectNode(root,ConstantWxId.ADDFR1);
            List<AccessibilityNodeInfo> node2 = inspectNode(root,ConstantWxId.ADDFR2);
            List<AccessibilityNodeInfo> node3 = inspectNode(root,ConstantWxId.ADDFR3);
            List<AccessibilityNodeInfo> node33 = inspectNode(root,ConstantWxId.ADDFR33);
            List<AccessibilityNodeInfo> node4 = inspectNode(root,ConstantWxId.ADDFR4);
            List<AccessibilityNodeInfo> node5 = inspectNode(root,ConstantWxId.ADDFR5);
            List<AccessibilityNodeInfo> node6 = inspectNode(root,ConstantWxId.ADDFR6);
            List<AccessibilityNodeInfo> node7 = inspectNode(root,ConstantWxId.ADDFR7);

            AccessibilityNodeInfo exception = AutoUtil.findNodeInfosByText(root,"发消息");
            if(exception!=null&&!AutoUtil.checkAction(record,"已添加过返回上一步")){
                if(obj.getCurrentIndex()<obj.getWxList().size()-1){
                    AutoUtil.performBack(context,record,"已添加过返回上一步");
                    AutoUtil.sleep(1500);
                    obj.setCurrentIndex(obj.getCurrentIndex()+1);
                }else{
                    LogUtil.d("AddFriendThread","搜索完成");
                    AutoUtil.recordAndLog(record,"008注册处理完成");
                    return;
                }
            }

            if(AutoUtil.checkAction(record,Constants.CHAT_LISTENING)||AutoUtil.checkAction(record,"登录成功添加好友")){
                AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"新的朋友"),record,"新的朋友",500);
                //AutoUtil.performClick(node1.get(0),record,"+",500);
            }
            if(node2!=null&&node2.size()>1){
                AutoUtil.performClick(node2.get(1),record,"添加朋友",1000);
            }
            if(node3!=null&&node3.size()>0){
                if(!node3.get(0).isClickable()){
                    AutoUtil.clickXY(526,328);//没有绑定手机号需要root，由于某个按钮无法点击
                    obj.setBdPhone(false);
                    LogUtil.d("AddFriendThread","root 点击");
                }
                //AutoUtil.performClick(node3.get(0),record,"微信号/QQ号/手机号",1000);
            }
            if(node33!=null&&node33.size()>0&&node33.get(0).isClickable()){
                AutoUtil.performClick(node33.get(0),record,"微信号/QQ号/手机号",1000);
            }
            if(!AutoUtil.checkAction(record,"点击搜索微信号")&&!AutoUtil.checkAction(record,"搜索输入")&&node4!=null&&node4.size()>0&&node4.get(0).getText()!=null){
                AutoUtil.performSetText(node4.get(0),obj.getOneWx(),record,"搜索输入");
            }
            if(node5!=null&&node5.size()>0&&!AutoUtil.checkAction(record,"发送好友后返回上一级")){
                if(!AutoUtil.checkAction(record,"点击搜索微信号")&&(node5.get(0).getText()+"").equals("搜索:"+obj.getOneWx()))
                    AutoUtil.performClick(node5.get(0),record,"点击搜索微信号",800);
            }
            if(AutoUtil.checkAction(record,"点击搜索微信号")&&AutoUtil.findNodeInfosByText(root,"该用户不存在")!=null){
                if(node4!=null&&node4.size()>0&&node4.get(0).getText()!=null){
                    if(obj.getCurrentIndex()<obj.getWxList().size()-1){
                        obj.setCurrentIndex(obj.getCurrentIndex()+1);
                        AutoUtil.performSetText(node4.get(0),obj.getOneWx(),record,"搜索输入");
                    }else{
                        LogUtil.d("AddFriendThread","搜索完成");
                        AutoUtil.recordAndLog(record,"008注册处理完成");
                        return;
                    }
                }
            }
            if(!AutoUtil.checkAction(record,"发送"))
                AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"添加到通讯录"),record,"添加到通讯录",1000);
            if(node6!=null&&node6.size()>0){
                AutoUtil.performSetText(node6.get(0),"..",record,"我是");

                if(node7!=null&&node7.size()>0){
                    AutoUtil.performClick(node7.get(0),record,"发送",2000);
                }
            }
            if(!obj.isBdPhone())//不绑定手机走的路线不一样
                AutoUtil.performClick(AutoUtil.findNodeInfosByText(root,"添加朋友"),record,"添加朋友",1000);
            if(AutoUtil.checkAction(record,"发送")&&AutoUtil.findNodeInfosByText(root,"添加到通讯录")!=null){
                if(obj.getCurrentIndex()<obj.getWxList().size()-1){
                    obj.setCurrentIndex(obj.getCurrentIndex()+1);
                    AutoUtil.performBack(context,record,"发送好友后返回上一级");
                }else{
                    LogUtil.d("AddFriendThread","搜索完成");
                    AutoUtil.recordAndLog(record,"008注册处理完成");
                    return;
                }
            }

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
