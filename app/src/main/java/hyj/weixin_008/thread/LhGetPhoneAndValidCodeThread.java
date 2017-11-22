package hyj.weixin_008.thread;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.LhPhoneNumberAPIService;
import hyj.weixin_008.service.XmhPhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class LhGetPhoneAndValidCodeThread implements Runnable{

    LhPhoneNumberAPIService phoneService = new LhPhoneNumberAPIService();

    PhoneApi pa;
    public LhGetPhoneAndValidCodeThread(PhoneApi pa){
        this.pa = pa;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(4000);
            LogUtil.d("LhPhoneNumberAPIService","【-->获取号码线程运行...】"+Thread.currentThread().getName()+" phone:"+pa.isPhoneIsAvailavle()+" validCode:"+pa.isValidCodeIsAvailavle()+" isSendMsg:"+pa.isSendMsg());
            if(pa.getToken()==null){
                /*pa.setApiId("52922-akx");
                pa.setPwd("aa105105");
                pa.setPjId("1296");*/
                String token = phoneService.login(pa.getApiId(),pa.getPwd());
                pa.setToken(token);
                LogUtil.d("LhPhoneNumberAPIService","token获取成功："+token);
                //首次启动执行释放所有号码，目前只支持吸码蝗
                //cancelAllRecv(token);
            }
            if(pa.getToken()!=null){
                if(!pa.isPhoneIsAvailavle()){
                    String phones = phoneService.getPhone(pa.getApiId(),pa.getToken(),pa.getPjId(),pa.getCnNum());
                    LogUtil.d("LhPhoneNumberAPIService","phoneBody:"+phones);
                    if(phones.indexOf("OK")>-1){
                        String[] ph = phones.split("\\|");
                        pa.setPhone(ph[4]);
                        pa.setPhoneId(ph[1]);
                        pa.setPhoneIsAvailavle(true);

                    }
                }

               if(pa.isPhoneIsAvailavle()&&!pa.isValidCodeIsAvailavle()){
                    String validCode = phoneService.getValidCode(pa.getToken(),pa.getPhoneId());
                    LogUtil.d("LhPhoneNumberAPIService","validCodeBody："+validCode);
                    if(validCode.matches("[\\d]{4,8}")){
                        pa.setValidCode(validCode);
                        pa.setValidCodeIsAvailavle(true);
                    }
                }
               /* //发送短信
                if(pa.isSendMsg()){
                    String status = phoneService.sendMsg(pa.getApiId(),pa.getToken(),pa.getPjId(),pa.getPhone(),pa.getMsg(),"5");
                    pa.setStatus(status);
                    pa.setSendMsg(false);
                }
                //获取短信发送状态
                if(pa.getStatus()!=null){
                    String body = phoneService.getSendStatus(pa.getPjId(),pa.getToken(),pa.getPjId(),pa.getPhone(),pa.getStatus());
                    if("succ".equals(body)){
                        pa.setStatus(null);
                    }
                }*/

            }
        }
    }

    //针对吸吸码蝗
    public String cancelAllRecv(String token){
        String mainUrl = "http://www.ximahuang.com/alz/api";
        String url = mainUrl+"?action=cancelAllRecv&token="+token;
        LogUtil.d("XmhGetPhoneAndValidCodeThread cancelAllRecvUrl",url);
        String cancelAllRecvBody = OkHttpUtil.okHttpGet(url);
        LogUtil.d("XmhGetPhoneAndValidCodeThread cancelAllRecvBody",cancelAllRecvBody);
        return cancelAllRecvBody;
    }
}
