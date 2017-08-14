package hyj.weixin_008.service;

import java.util.AbstractList;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/14.
 */

public class PhoneNumberAPIService {
    public String login(String apiId,String pwd){
        String token = "";
        String url = "http://api.jyzszp.com/Api/index/loginIn?uid="+apiId+"&pwd="+pwd;
        String body = OkHttpUtil.okHttpGet(url);
        LogUtil.d("loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length==3&&strs[1].equals(apiId)){
            token = strs[2];
        }
        return token;
    }
}
