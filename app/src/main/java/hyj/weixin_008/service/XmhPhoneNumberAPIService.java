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

public class XmhPhoneNumberAPIService {
    String mainUrl;
    public XmhPhoneNumberAPIService(String mainUrl){
        this.mainUrl = mainUrl;
    }
    public String login(String apiId,String pwd){
        String token = "";
        String url =mainUrl+"?action=loginIn&name="+apiId+"&password="+pwd+"&developer=developer=ff40f1097dfe48849359559734e228ad";
        LogUtil.d(" XmhGetPhoneAndValidCodeThread loginBody url",url);
        String body = OkHttpUtil.okHttpGet(url);
        LogUtil.d(" XmhGetPhoneAndValidCodeThread loginBody",body);
        String[] strs = body.split("\\|");
        if(strs.length==2&&strs[0].equals("1")){
            token = strs[1];
        }
        return token;
    }
    public Map<String,String> cnMap(){
        Map<String,String> map = new HashMap<String,String>();

        map.put("1","CA");//加拿大-CA
        map.put("57","CO");//哥伦比亚-CO
        map.put("20","EG");//埃及-EG
        map.put("852","HK");//中国香港-HK
        map.put("62","ID");//印度尼西亚-ID
        map.put("91","IN");//印度-IN
        map.put("855","KH");//柬埔寨-KH
        map.put("266","LS");//莱索托-LS

        map.put("261","MG");//马达加斯加-MG
        map.put("95","MM");//缅甸-MM
        map.put("853","MO");//中国澳门-MO
        map.put("60","MY");//马来西亚-MY
        map.put("507","PA");//巴拿马-PA
        map.put("63","PH");//菲律宾-PH

        map.put("7","RU");//俄罗斯-RU
        map.put("66","TH");//泰国-TH
        map.put("1","US");//美国-US
        map.put("84","VN");//越南VN
        map.put("27","ZA");//南非-ZA
        return map;
    }
    /**
     *     目前仅支持加拿大-CA;哥伦比亚-CO;埃及-EG;中国香港-HK;印度尼西亚-ID;印度-IN;柬埔寨-KH;莱索托-LS;
     *      马达加斯加-MG;缅甸-MM;中国澳门-MO;马来西亚-MY;巴拿马-PA;菲律宾-PH;俄罗斯-RU;泰国-TH;美国-US;越南VN;南非-ZA
     * @param apiId
     * @param token
     * @param pjId
     * @param cnNum
     * @return
     */
    public String getPhone(String apiId,String token,String pjId,String cnNum){
        Map<String,String> map = cnMap();
        cnNum = map.get(cnNum);

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
