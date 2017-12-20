package hyj.weixin_008.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/14.
 */

public class ZYPhoneNumberAPIService {
    public String login(String apiId,String pwd){
        String token = "";
        String url ="http://zhiyuan.quanhuini.com/Login?User="+apiId+"&Password="+pwd+"&Logintype=0";
        LogUtil.d(" ZYPhoneNumberAPIService loginBody url",url);
        String body = OkHttpUtil.okHttpGet(url);
        LogUtil.d(" ZYPhoneNumberAPIService loginBody",body);
        JSONObject jb = JSON.parseObject(body);
        if("0".equals(jb.getString("code"))){
            token = jb.getJSONObject("data").getString("Token");

        }
        return token;
    }


    public JSONObject getPhone( String token, String pjId){

        JSONObject phone = null;
        String url = "http://zhiyuan.quanhuini.com/GetPhoneNumber?Token="+token+"&ItemId="+pjId+"&Phone=&Operator=0&Developer=";
        String phones = OkHttpUtil.okHttpGet(url);
        System.out.println("-->hyj ZYPhoneNumberAPIService getPhone url-->"+url);
        System.out.println("-->hyj ZYPhoneNumberAPIService phoneBody-->"+phones);
        //LogUtil.d("phoneBody",phones);
        JSONObject jb = JSON.parseObject(phones);
        if("0".equals(jb.getString("code"))){
            phone = jb.getJSONObject("data");

        }
        System.out.println("-->hyj ZYPhoneNumberAPIService getPhone-->"+phone);
        return phone;
    }
    public String getValidCode(String apiId,String phone,String token,String pjId){
        String validCode = "";
        String veryCodeBody = OkHttpUtil.okHttpGet("http://zhiyuan.quanhuini.com/GetMessage?Token="+token+"&MSGID="+phone);
        System.out.println("hyj veryCodeBody-->"+veryCodeBody);
        JSONObject jb = JSON.parseObject(veryCodeBody);
        if("0".equals(jb.getString("code"))){
            phone = jb.getString("data");
            System.out.println("hyj veryCodeBody data-->"+phone);
            phone = regString(phone,"[\\d]{4,10}",0);
            System.out.println("hyj veryCodeBody code-->"+phone);
        }

        return validCode;
    }

    public static Matcher createMatcher(String matchStr, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(matchStr);
        return m;
    }

    public static String regString(String str, String reg, int groupNum) {
        String resultString = "";
        Matcher m = createMatcher(str, reg);
        if (m.find()) {
            resultString = m.group(groupNum);
        }
        return resultString;
    }
}
