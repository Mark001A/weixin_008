package hyj.weixin_008;

import android.os.Handler;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hyj.weixin_008.flowWindow.MyWindowManager;

/**
 * Created by Administrator on 2017/8/11.
 */

public class RefreshFlowMsg implements Runnable{
    Map<String,String> record;
    public RefreshFlowMsg(Map<String,String> record){
        this.record = record;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(300);
            MyWindowManager.updateFlowMsg(record+"");
            System.out.println("--msgMap------"+record);
        }
    }
}
