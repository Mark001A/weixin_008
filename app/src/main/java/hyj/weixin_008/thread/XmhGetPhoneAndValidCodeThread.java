package hyj.weixin_008.thread;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.PhoneNumberAPIService;
import hyj.weixin_008.service.XmhPhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class XmhGetPhoneAndValidCodeThread implements Runnable{
    // PhoneNumberAPIService phoneService = new PhoneNumberAPIService();
    //AlzPhoneNumberAPIService phoneService = new AlzPhoneNumberAPIService();
    String mainUrl ="http://www.ximahuang.com/alz/api";
    XmhPhoneNumberAPIService phoneService = new XmhPhoneNumberAPIService(mainUrl);

    PhoneApi pa;
    public XmhGetPhoneAndValidCodeThread(PhoneApi pa){
        this.pa = pa;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(4000);
            LogUtil.d("XmhGetPhoneAndValidCodeThread","【-->XmhGetPhoneAndValidCodeThread获取号码线程运行...】"+Thread.currentThread().getName()+" phone:"+pa.isPhoneIsAvailavle()+" validCode:"+pa.isValidCodeIsAvailavle()+" isSendMsg:"+pa.isSendMsg());
            if(pa.getToken()==null){
                /*pa.setApiId("52922-akx");
                pa.setPwd("aa105105");
                pa.setPjId("1296");*/
                String token = phoneService.login(pa.getApiId(),pa.getPwd());
                pa.setToken(token);
                LogUtil.d("XmhGetPhoneAndValidCodeThread","token获取成功："+token);
                //首次启动执行释放所有号码，目前只支持吸码蝗
                cancelAllRecv(token);
            }
            if(pa.getToken()!=null){
                if(!pa.isPhoneIsAvailavle()){
                    String phone = phoneService.getPhone(pa.getApiId(),pa.getToken(),pa.getPjId(),pa.getCnNum());
                    LogUtil.d("XmhGetPhoneAndValidCodeThread","phoneBody:"+phone);
                    if(phone.matches("[\\d]{6,20}")){
                        pa.setPhone(phone);
                        pa.setPhoneIsAvailavle(true);
                    }
                }

               if(pa.isPhoneIsAvailavle()&&!pa.isValidCodeIsAvailavle()){
                    String validCode = phoneService.getValidCode(pa.getApiId(),pa.getPhone(),pa.getToken(),pa.getPjId());
                    LogUtil.d("XmhGetPhoneAndValidCodeThread","validCodeBody："+validCode);
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
