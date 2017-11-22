package hyj.weixin_008.thread;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.common.WeixinAutoHandler;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.XmhPhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class DieThread implements Runnable{


    @Override
    public void run() {
        /*while (true){
            LogUtil.d("die","die thread..");
            AutoUtil.sleep(8000);
            String url = "http://120.78.134.230/die";
            String str = OkHttpUtil.okHttpGet(url);
            LogUtil.d("die response","die response str:"+str);
            if("OK".equals(str)){
                WeixinAutoHandler.record.put("die","OK");
                System.exit(0);
                int a = 1/0;
            }
        }*/
    }

}
