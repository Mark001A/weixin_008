package hyj.weixin_008.thread;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.Constants;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.daoModel.Wx008Data;
import hyj.weixin_008.model.AutoChatObj;
import hyj.weixin_008.util.DaoUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.ParseRootUtil;

/**
 * Created by asus on 2017/8/20.
 */

public class AddFriendThread1 implements Runnable {
    AccessibilityService context;
    Map<String,String> record;
    AutoChatObj obj = new AutoChatObj();
    public AddFriendThread1(AccessibilityService context){
        this.context = context;
        this.record = WeixinAutoHandler.record;
    }
    List<Wx008Data> wx008Datas = DaoUtil.getWx008Datas();
    int currentAddWxid = 0;
    boolean isReceiveFr = false;
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(500);
            LogUtil.d("AddFriendThread1","AddFriendThread1..."+Thread.currentThread().getName()+" ");
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->AddFriendThread1 root is null");
                AutoUtil.sleep(500);
                continue;
            }

            ParseRootUtil.debugRoot(root);

            if(AutoUtil.actionContains(record,"AddFriendThread1")||AutoUtil.checkAction(record,"2")||AutoUtil.checkAction(record,"0")){

                //判断是否有人添加好友
                AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"朋友推荐");
                if(node1!=null){
                    isReceiveFr = true;
                }

                if(isReceiveFr){
                    doReceiveFr(root);//接受好友请求
                }else {
                    doAddFr(root);//添加好友
                }

            }

        }
    }

    private void doReceiveFr(AccessibilityNodeInfo root){
        AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"朋友推荐");
        if(node1!=null){
            System.out.println("node1-->pytj");
            isReceiveFr = true;
            AutoUtil.performClick(node1,record,"AddFriendThread1点击进入接受好友请求列表");
        }
        AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosByText(root,"接受");
        AutoUtil.performClick(node2,record,"AddFriendThread1点击接受");
        AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"去验证");
        AutoUtil.performClick(node3,record,"AddFriendThread1去验证");
        AccessibilityNodeInfo node4 = ParseRootUtil.getNodeByPathAndText(root,"00350","通过验证");
        AutoUtil.performClick(node4,record,"AddFriendThread1通过验证");
        AccessibilityNodeInfo node5 = AutoUtil.findNodeInfosByText(root,"完成");
        AutoUtil.performClick(node5,record,"AddFriendThread1完成通过好友");

        AccessibilityNodeInfo node6 = ParseRootUtil.getNodePath(root,"000");
        AccessibilityNodeInfo node7 = ParseRootUtil.getNodeByPathAndText(root,"001","详细资料");
        if(node7!=null&&AutoUtil.checkAction(record,"AddFriendThread1完成通过好友")){
            AutoUtil.performClick(node6,record,"AddFriendThread1返回接受列表");
        }

        AccessibilityNodeInfo node8 = ParseRootUtil.getNodeByPathAndText(root,"001","新的朋友");
        if(node8!=null&&node2==null){
            AutoUtil.performBack(context,record,"AddFriendThread1返回主界面");
            //AutoUtil.performClick(node8,record,"AddFriendThread1返回主界面");
        }
        if(AutoUtil.checkAction(record,"AddFriendThread1返回主界面")){
            isReceiveFr = false;
        }

    }
    private void doAddFr(AccessibilityNodeInfo root){
        //处理添加异常情况
        AccessibilityNodeInfo expNode1 = ParseRootUtil.getNodeByPathAndText(root,"00","用户不存在");
        if(expNode1!=null){
            AccessibilityNodeInfo expNode2 = ParseRootUtil.getNodeByPathAndText(root,"01","确定");
            AutoUtil.performClick(expNode2,record,"AddFriendThread1用户不存在");
            currentAddWxid = currentAddWxid+1;
            return;
        }

        //点击返回，序号加1
        if(AutoUtil.checkAction(record,"AddFriendThread1发送好友请求")){
            AccessibilityNodeInfo node61 = AutoUtil.findNodeInfosByText(root,"添加到通讯录");
            AccessibilityNodeInfo node7 = ParseRootUtil.getNodeByPathAndDesc(root,"0000","返回");
            if(node61!=null&&node7!=null){
                AutoUtil.performClick(node7,record,"AddFriendThread1发送好友请求返回");
                currentAddWxid = currentAddWxid+1;
                LogUtil.d("AddFriendThread1","【点击返回，序号加1】");
                return;
            }
        }

        //1、点击搜索
        List<AccessibilityNodeInfo> nodes1 = root.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/gh");//搜索父节点
        if(nodes1!=null&&nodes1.size()>0){
            AccessibilityNodeInfo node1 = nodes1.get(0).getChild(1);
            AutoUtil.performClick(node1,record,"AddFriendThread1搜索");
        }
        //2、输入wxid
        String wxid = getWxidByIndex(record.get("wxIndex"),currentAddWxid);
        LogUtil.d("AddFriendThread1","【current wxid】："+wxid);
        if(wxid==null){
            System.out.println("--->008 login seccess");
            AutoUtil.recordAndLog(record,"008登录成功");
            currentAddWxid = 0;
            updateFriends(record);
            return;
        }
        AccessibilityNodeInfo node2 = AutoUtil.findNodeInfosById(root,"com.tencent.mm:id/hb");
        AutoUtil.performSetText(node2,wxid,record,"AddFriendThread1输入wxid");
        //3、点击搜索出来wxid
        AccessibilityNodeInfo node3 = AutoUtil.findNodeInfosByText(root,"微信号: "+wxid);
        AccessibilityNodeInfo node31 = AutoUtil.findNodeInfosByText(root,"查找微信号:"+wxid);
        AccessibilityNodeInfo node32= AutoUtil.findNodeInfosByText(root,"搜索:"+wxid);
        AccessibilityNodeInfo node33= AutoUtil.findNodeInfosByText(root,"查找手机/QQ号:"+wxid);

        AutoUtil.performClick(node31,record,"AddFriendThread1点击查找wxid");
        AutoUtil.performClick(node32,record,"AddFriendThread1点击查找wxid");
        AutoUtil.performClick(node33,record,"AddFriendThread1点击查找wxid");

        AccessibilityNodeInfo node6 = AutoUtil.findNodeInfosByText(root,"添加到通讯录");
        AutoUtil.performClick(node6,record,"AddFriendThread1添加到通讯录");

        AccessibilityNodeInfo node7 = ParseRootUtil.getNodeByPathAndText(root,"002","发送");
        AutoUtil.performClick(node7,record,"AddFriendThread1发送好友请求");

        //AccessibilityNodeInfo node8 = ParseRootUtil.getNodeByPathAndText(root,"00350","发消息");
        AccessibilityNodeInfo node8 = AutoUtil.findNodeInfosByText(root,"发消息");
        System.out.println("node8-->"+node8);
        if(node8!=null&&!AutoUtil.checkAction(record,"AddFriendThread1以前已添加返回主界面")){
            /*AccessibilityNodeInfo node9 = ParseRootUtil.getNodePath(root,"003");
            AutoUtil.performClick(node9,record,"AddFriendThread1以前已添加返回主界面");*/
            AutoUtil.performBack(context,record,"AddFriendThread1以前已添加返回主界面");
            currentAddWxid = currentAddWxid+1;
        }




    }


   /* private String getWxidByIndex(String index,int addIndex){
        List<String> groups = getCurrentGroupByIndex(index);
        if(groups==null||groups.size()==0){
            LogUtil.d("AddFriendThread1","groups is null");
            return null;
        }
        if(addIndex<groups.size()){
            return groups.get(addIndex);
        }else {
            return null;
        }

    }*/
   private List<String> getAddWxids(){
       List<String> list1 =getAddWxids2();

       List<String> list = new ArrayList<String>();
       int index = 24;
       list.add(list1.get(index));
       list.add(list1.get(index+1));
       return list;
   }

    private List<String> getAddWxids2(){
        List<String> list = new ArrayList<String>();
        list.add("mnf444");
        list.add("uyh222");
        list.add("ikf222");
        list.add("ehg222");
        list.add("ehg333");
        list.add("ehg999");

        list.add("w222qu");
        list.add("w234wr");
        list.add("w456mb");
        list.add("w444mb");
        list.add("w555mb");
        list.add("w666mb");

        list.add("w777mb");
        list.add("w888mb");
        list.add("w333mb");
        list.add("w222mb");
        list.add("w333wc");
        list.add("w444wc");

        list.add("w666wc");
        list.add("w777wc");
        list.add("w222wt");
        list.add("w333wt");
        list.add("w444wt");
        list.add("w555hb");

        list.add("w555wt");
        list.add("w222wu");
        list.add("w444wu");
        list.add("w222hk");
        list.add("w333hk");
        list.add("w444hk");

        return list;
    }
    private String getWxidByIndex(String index,int addIndex){
        List<String> list =getAddWxids();
        if(addIndex<list.size()){
            return list.get(addIndex);
        }else {
            return null;
        }

    }
    private List<String> getCurrentGroupByIndex(String index){
        List<String> groups = new ArrayList<String>();
        int ix = Integer.parseInt(index);
        if(wx008Datas==null||wx008Datas.size()==0){
            LogUtil.d("AddFriendThread1","wx008Datas is null");
            return null;
        }
        if(ix%3==0&&ix+2<wx008Datas.size()){
            groups.add(getWxid(wx008Datas.get(ix+1)));
            groups.add(getWxid(wx008Datas.get(ix+2)));
        }else if(ix%3==1&&ix+1<wx008Datas.size()){
            groups.add(getWxid(wx008Datas.get(ix-1)));
            groups.add(getWxid(wx008Datas.get(ix+1)));
        }else if(ix%3==2){
            groups.add(getWxid(wx008Datas.get(ix-1)));
            groups.add(getWxid(wx008Datas.get(ix-2)));
        }
        return groups;
    }

    private String getWxid(Wx008Data data){
        String wxid = data.getWxId();
        if(wxid==null){
            wxid = data.getPhone();
        }
        return wxid;
    }
    //记录添加好友到数据库
    private void updateFriends(Map<String,String> record){
        String phone = record.get("phone");
        System.out.println("updateFriends phone:-->"+phone);
        Wx008Data wx008Data = DaoUtil.findByPhone(phone);
        List<String> list = JSON.parseArray(wx008Data.getFriends(),String.class);
        System.out.println("updateFriends db FriendList1:-->"+wx008Data.getFriends());
        if(list==null){
            wx008Data.setFriends(JSON.toJSONString(getAddWxids()));
        }else {
            wx008Data.setFriends(JSON.toJSONString(list.addAll(getAddWxids())));
        }
        wx008Data.updateAll("phone=?",phone);
        System.out.println("updateFriends db FriendList2:-->"+DaoUtil.findByPhone(phone).getFriends());
    }

}
