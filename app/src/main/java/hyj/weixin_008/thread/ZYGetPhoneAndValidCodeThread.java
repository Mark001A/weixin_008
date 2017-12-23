package hyj.weixin_008.thread;

import com.alibaba.fastjson.JSONObject;

import hyj.weixin_008.AutoUtil;
import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.XmhPhoneNumberAPIService;
import hyj.weixin_008.service.ZYPhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;
import hyj.weixin_008.util.OkHttpUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class ZYGetPhoneAndValidCodeThread implements Runnable{
    ZYPhoneNumberAPIService phoneService = new ZYPhoneNumberAPIService();

    PhoneApi pa;
    public ZYGetPhoneAndValidCodeThread(PhoneApi pa){
        this.pa = pa;
    }
    @Override
    public void run() {
        while (true){
            try {
            AutoUtil.sleep(5000);
            LogUtil.d("ZYGetPhoneAndValidCodeThread","【-->ZYGetPhoneAndValidCodeThread获取号码线程运行...】"+Thread.currentThread().getName()+" phone:"+pa.isPhoneIsAvailavle()+" validCode:"+pa.isValidCodeIsAvailavle()+" isSendMsg:"+pa.isSendMsg());
            if(pa.getToken()==null){
                String token = phoneService.login(pa.getApiId(),pa.getPwd());
                pa.setToken(token);
                LogUtil.d("ZYGetPhoneAndValidCodeThread","token获取成功："+token);
                cancelAllRecv(token);
            }
            if(pa.getToken()!=null){
                if(!pa.isPhoneIsAvailavle()){
                    JSONObject phoneObj = phoneService.getPhone(pa.getToken(),pa.getPjId());
                    String phone = phoneObj.getString("Phone");
                    String MSGID = phoneObj.getString("MSGID");
                    LogUtil.d("ZYGetPhoneAndValidCodeThread","phoneBody:"+phone);
                    if(phone.matches("[\\d]{4,20}")){
                        pa.setPhoneId(MSGID);
                        pa.setPhone(phone);
                        pa.setPhoneIsAvailavle(true);
                    }
                }

               if(pa.isPhoneIsAvailavle()&&!pa.isValidCodeIsAvailavle()){
                    String validCode = phoneService.getValidCode(pa.getApiId(),pa.getPhoneId(),pa.getToken(),pa.getPjId());
                    LogUtil.d("ZYGetPhoneAndValidCodeThread","validCodeBody："+validCode);
                    if(validCode.matches("[\\d]{4,8}")){
                        pa.setValidCode(validCode);
                        pa.setValidCodeIsAvailavle(true);
                    }
                }

                //释放手机号码
                if(pa.getReleasPhone()!=null){
                    LogUtil.d("ZYGetPhoneAndValidCodeThread","【释放手机号】："+pa.getReleasPhone());
                    releasePhone(pa.getToken(),pa.getReleasPhone());
                    pa.setReleasPhone(null);
                }

                /*//发送短信
                if(pa.isSendMsg()){
                    String status = phoneService.sendMsg(pa.getToken(),pa.getPhone(),pa.getMsg());
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

            }catch (Exception e){
                System.out.println("----Thread error");
                e.printStackTrace();
            }
        }
    }

    //针对吸吸码蝗
    public String cancelAllRecv(String token){
        String url ="http://zhiyuan.quanhuini.com/AllRelease?Token="+token;
        LogUtil.d("ZYGetPhoneAndValidCodeThread cancelAllRecvUrl",url);
        String cancelAllRecvBody = OkHttpUtil.okHttpGet(url);
        LogUtil.d("ZYGetPhoneAndValidCodeThread cancelAllRecvBody",cancelAllRecvBody);
        return cancelAllRecvBody;
    }

    private void releasePhone(String token,String phone){
        String url  = "http://zhiyuan.quanhuini.com/ReleasePhone?Token="+token+"&MSGID="+phone;
        LogUtil.d("ZYGetPhoneAndValidCodeThread releasePhone",url);
        String releasePhoneBody = OkHttpUtil.okHttpGet(url);
        LogUtil.d("ZYGetPhoneAndValidCodeThread releasePhone",releasePhoneBody);
    }
}
