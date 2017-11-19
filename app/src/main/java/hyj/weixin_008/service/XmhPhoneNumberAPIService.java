package hyj.weixin_008.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/14.
 */

public class XmhPhoneNumberAPIService {
    String mainUrl;
    public XmhPhoneNumberAPIService(String mainUrl){
        this.mainUrl = mainUrl;
    }
    public String login(String apiId,String pwd){
        String token = "";
        String url =mainUrl+"?action=loginIn&name="+apiId+"&password="+pwd+"&developer=developer=ff40f1097dfe48849359559734e228ad";
        String body = OkHttpUtil.okHttpGet(url);
        LogUtil.d(" XmhGetPhoneAndValidCodeThread loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length==2&&strs[0].equals("1")){
            token = strs[1];
        }
        return token;
    }
    public String getPhone(String apiId,String token,String pjId,String cnNum){
        if("62".equals(cnNum)){
            cnNum = "ID";
        }
        String phone = "";
        String url = mainUrl+"?action=getPhone&sid="+pjId+"&token="+token+"&filterCc="+cnNum;
        String phones = OkHttpUtil.okHttpGet(url);
        System.out.println("XmhGetPhoneAndValidCodeThread getPhone url-->"+url);
        System.out.println("XmhGetPhoneAndValidCodeThread phoneBody-->"+phones);
        //LogUtil.d("phoneBody",phones);
        String[] strs = phones.split("\\|");
        if(strs!=null&&strs.length==2&&strs[1].matches("[\\d]{5,20}")){
            phone = strs[1];
        }
        return phone;
    }
    public String getValidCode(String apiId,String phone,String token,String pjId){
        String validCode = "";
        String veryCodeBody = OkHttpUtil.okHttpGet(mainUrl+"?action=getMessage&sid="+pjId+"&phone="+phone+"&token="+token);
        //LogUtil.d("veryCodeBody",veryCodeBody);
        String[] codeStr = veryCodeBody.split("\\|");
        if(codeStr!=null&&codeStr.length==2&&codeStr[0].matches("1")){
            validCode = veryCodeBody.split("\\|")[1];
            validCode = regString(validCode,"[\\d]{4,10}",0);
        }else{
            System.out.println("XmhGetPhoneAndValidCodeThread veryCodeBod:y"+veryCodeBody);
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
