package hyj.weixin_008.thread;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.AlzPhoneNumberAPIService;
import hyj.weixin_008.service.PhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class ALZGetPhoneAndValidCodeThread implements Runnable{
    AlzPhoneNumberAPIService phoneService = new AlzPhoneNumberAPIService();
    PhoneApi pa;
    public ALZGetPhoneAndValidCodeThread(PhoneApi pa){
        this.pa = pa;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(4000);
            LogUtil.d("ALZGetPhoneAndValidCodeThread","-->获取号码线程运行..."+Thread.currentThread().getName()+" phone:"+pa.isPhoneIsAvailavle()+" validCode:"+pa.isValidCodeIsAvailavle()+" isSendMsg:"+pa.isSendMsg());
            if(pa.getToken()==null){
                /*pa.setApiId("52922-akx");
                pa.setPwd("aa105105");
                pa.setPjId("1296");*/
                String token = phoneService.login(pa.getApiId(),pa.getPwd());
                pa.setToken(token);
                LogUtil.d("ALZGetPhoneAndValidCodeThread","token获取成功："+token);
            }
            if(pa.getToken()!=null){
                if(!pa.isPhoneIsAvailavle()){
                    String phone = phoneService.getPhone(pa.getApiId(),pa.getToken(),pa.getPjId());
                    LogUtil.d("ALZGetPhoneAndValidCodeThread","phoneBody:"+phone);
                    if(phone.matches("[\\d]{11}")){
                        pa.setPhone(phone);
                        pa.setPhoneIsAvailavle(true);
                    }
                }

               if(pa.isPhoneIsAvailavle()&&!pa.isValidCodeIsAvailavle()){
                    String validCode = phoneService.getValidCode(pa.getApiId(),pa.getPhone(),pa.getToken(),pa.getPjId());
                    LogUtil.d("ALZGetPhoneAndValidCodeThread","validCodeBody："+validCode);
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
}
