package hyj.weixin_008.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/14.
 */

public class AlzPhoneNumberAPIService {
    public String login(String apiId,String pwd){
        String token = "";
        //String url = "http://api.jyzszp.com/Api/index/loginIn?uid="+apiId+"&pwd="+pwd;
        String url ="http://api.xingjk.cn/api/do.php?action=loginIn&name="+apiId+"&password="+pwd;
        String body = OkHttpUtil.okHttpGet(url);
        //LogUtil.d("loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length==2&&strs[0].equals("1")){
            token = strs[1];
        }
        return token;
    }
    public String getPhone(String apiId,String token,String pjId){
        String phone = "";
        //String phones = OkHttpUtil.okHttpGet("http://api.jyzszp.com/Api/index/getMobilenum?pid="+pjId+"&uid="+apiId+"&token="+token+"&mobile=&size=1");
        String phones = OkHttpUtil.okHttpGet("http://api.xingjk.cn/api/do.php?action=getPhone&sid="+pjId+"&token="+token);
        System.out.println("phoneBody-->"+phones);
        //LogUtil.d("phoneBody",phones);
        String[] strs = phones.split("\\|");
        if(strs!=null&&strs.length==2&&strs[1].matches("[\\d]{11}")){
            phone = strs[1];
        }
        return phone;
    }
    public String getValidCode(String apiId,String phone,String token,String pjId){
        String validCode = "";
        //String veryCodeBody = OkHttpUtil.okHttpGet("http://api.jyzszp.com/Api/index/getVcodeAndReleaseMobile?uid="+apiId+"&token="+token+"&mobile="+phone+"&pid="+pjId);
        String veryCodeBody = OkHttpUtil.okHttpGet("http://api.xingjk.cn/api/do.php?action=getMessage&sid="+pjId+"&phone="+phone+"&token="+token);
        //LogUtil.d("veryCodeBody",veryCodeBody);
        String[] codeStr = veryCodeBody.split("\\|");
        if(codeStr!=null&&codeStr.length==2&&codeStr[0].matches("1")){
            validCode = veryCodeBody.split("\\|")[1];
            validCode = regString(validCode,"[\\d]{6}",0);
        }else{
            System.out.println("veryCodeBod:y"+veryCodeBody);
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
