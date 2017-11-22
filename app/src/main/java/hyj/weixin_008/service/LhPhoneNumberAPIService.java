package hyj.weixin_008.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/14.
 */

public class LhPhoneNumberAPIService {
    public String login(String apiId,String pwd){
        String url = "http://lhapi.tounige.com/yhapi.ashx?Action=userLogin&userName="+apiId+"&userPassword="+pwd;
        String token = "";
        String body = OkHttpUtil.okHttpGet(url);
        LogUtil.d(" LhPhoneNumberAPIService loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length>1&&strs[0].equals("OK")){
            token = strs[1];
        }
        return token;
    }

    public String getPhone(String apiId,String token,String pjId,String cnNum){
        String phone = "";
        String url = "http://lhapi.tounige.com/yhapi.ashx?Action=getPhone&token="+token+"&i_id="+pjId+"&d_id=&p_operator=&p_qcellcore=&mobile=";
        String phones = OkHttpUtil.okHttpGet(url);
        System.out.println("LhPhoneNumberAPIService getPhone url-->"+url);
        System.out.println("LhPhoneNumberAPIService phoneBody-->"+phones);
        String[] strs = phones.split("\\|");
        if(strs!=null&&strs.length>4&&strs[0].equals("OK")){
            phone = strs[1];
        }
        return phones;
    }
    public String getValidCode(String token,String phoneId){
        String url = "http://lhapi.tounige.com/yhapi.ashx?Action=getPhoneMessage&token="+token+"&p_id="+phoneId;
        String validCode = "";
        String veryCodeBody = OkHttpUtil.okHttpGet(url);
        LogUtil.d("LhPhoneNumberAPIService getValidCodeUrl",url);
        LogUtil.d("LhPhoneNumberAPIService getValidCode",veryCodeBody);
        String[] codeStr = veryCodeBody.split("\\|");
        if(codeStr!=null&&codeStr.length>2&&codeStr[0].equals("OK")){
            validCode = veryCodeBody.split("\\|")[1];
        }else{
            System.out.println("LhPhoneNumberAPIService veryCodeBod:y"+veryCodeBody);
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
