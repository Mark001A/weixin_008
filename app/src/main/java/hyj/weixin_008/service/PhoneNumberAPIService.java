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
        //LogUtil.d("loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length==3&&strs[1].equals(apiId)){
            token = strs[2];
        }
        return token;
    }
    public String getPhone(String apiId,String token,String pjId){
        String phone = "";
        String phones = OkHttpUtil.okHttpGet("http://api.jyzszp.com/Api/index/getMobilenum?pid="+pjId+"&uid="+apiId+"&token="+token+"&mobile=&size=1");
        System.out.println("phoneBody-->"+phones);
        //LogUtil.d("phoneBody",phones);
        String[] strs = phones.split("\\|");
        if(strs!=null&&strs.length==2&&strs[0].matches("[\\d]{11}")){
            phone = strs[0];
        }
        return phone;
    }
    public String getValidCode(String apiId,String phone,String token,String pjId){
        String validCode = "";
        String veryCodeBody = OkHttpUtil.okHttpGet("http://api.jyzszp.com/Api/index/getVcodeAndReleaseMobile?uid="+apiId+"&token="+token+"&mobile="+phone+"&pid="+pjId);
        //LogUtil.d("veryCodeBody",veryCodeBody);
        String[] codeStr = veryCodeBody.split("\\|");
        if(codeStr!=null&&codeStr.length==3&&codeStr[0].matches("[\\d]{11}")){
            validCode = veryCodeBody.split("\\|")[1];
        }else{
            System.out.println("veryCodeBod:y"+veryCodeBody);
        }
        return validCode;
    }
}
