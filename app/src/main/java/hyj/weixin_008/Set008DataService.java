package hyj.weixin_008;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.util.FileUtil;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by Administrator on 2017/8/4.
 */

public class Set008DataService implements Runnable{
    static List<String[]> datas;
    MyService context;
    Map<String,String> record;
    public Set008DataService(MyService context, Map<String,String> record){
        this.context = context;
        this.record = record;
        datas = get008Datas();
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(1000);
            LogUtil.d("myService","-->写入008数据线程..."+Thread.currentThread().getName()+record);
            AccessibilityNodeInfo root = context.getRootInActiveWindow();
            if(root==null){
                System.out.println("-->root is null");
                AutoUtil.sleep(500);
                continue;
            }

            AccessibilityNodeInfo node1 = AutoUtil.findNodeInfosByText(root,"工具箱");
            if(node1!=null){
                AutoUtil.clickXY(373,422);
                AutoUtil.recordAndLog(record,"点击图片");
                AutoUtil.sleep(1000);
                continue;
            }
            if(AutoUtil.checkAction(record,"点击图片")){
                AccessibilityNodeInfo list = AutoUtil.findNodeInfosById(root,"com.soft.apk008v:id/set_value_con");
                System.out.println("list--->"+list);
                if(list!=null){
                    System.out.println("size--->"+list.getChildCount());
                }
                if(list!=null&&list.getChildCount()==91){
                    for(int i=1;i<91;i++){
                        if(list.getChild(i).isEditable()){
                            System.out.println("-rr->"+i+" "+datas.get(0)[i]);
                            AutoUtil.performSetText(list.getChild(i),datas.get(1)[i+1],record,"写入"+i+" "+datas.get(0)[i+1]);
                        }
                    }
                }
            }
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
    private int getStartIndex(){
        List<String[]> datas = get008Datas();
        return datas.indexOf("序列号");
    }
}
