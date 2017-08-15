package hyj.weixin_008;

import hyj.weixin_008.model.PhoneApi;
import hyj.weixin_008.service.PhoneNumberAPIService;
import hyj.weixin_008.util.LogUtil;

/**
 * Created by Administrator on 2017/8/15.
 */

public class GetPhoneAndValidCodeThread  implements Runnable{
    PhoneNumberAPIService phoneService = new PhoneNumberAPIService();
    PhoneApi pa;
    public GetPhoneAndValidCodeThread(PhoneApi pa){
        this.pa = pa;
    }
    @Override
    public void run() {
        while (true){
            AutoUtil.sleep(4000);
            if(pa.getToken()==null){
                String token = phoneService.login(pa.getApiId(),pa.getPwd());
                pa.setToken(token);
                LogUtil.d("GetPhoneAndValidCodeThread","token获取成功："+token);
            }
            if(pa.getToken()!=null){
                if(!pa.isPhoneIsAvailavle()){
                    String phone = phoneService.getPhone(pa.getApiId(),pa.getToken(),pa.getPjId());
                    if(phone.matches("[\\d]{11}")){
                        pa.setPhone(phone);
                        pa.setPhoneIsAvailavle(true);
                        LogUtil.d("GetPhoneAndValidCodeThread","phone获取成功："+phone);
                    }
                }

                if(pa.isPhoneIsAvailavle()&&!pa.isValidCodeIsAvailavle()){
                    String validCode = phoneService.getValidCode(pa.getApiId(),pa.getPhone(),pa.getToken(),pa.getPjId());
                    if(validCode.matches("[\\d]{4,8}")){
                        pa.setValidCode(validCode);
                        pa.setValidCodeIsAvailavle(true);
                        LogUtil.d("GetPhoneAndValidCodeThread","validCode获取成功："+validCode);
                    }
                }
            }

        }
    }
}
