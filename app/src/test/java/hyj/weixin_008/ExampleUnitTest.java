package hyj.weixin_008;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import hyj.weixin_008.service.PhoneNumberAPIService;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        PhoneNumberAPIService service = new PhoneNumberAPIService();
        String token = service.login("52922-akx","aa105105");
        System.out.println("token-->"+token);
        String phone = service.getPhone("52922-akx",token,"1289");
        System.out.println("phone-->"+phone);
    }
}