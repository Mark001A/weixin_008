package hyj.weixin_008;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String[] str = new String[2];
        str[0]="22";
        str[1]="333";
        String strs = JSON.toJSONString(str);
        String[] ds =  JSONObject.parseObject(strs,String[].class);
        System.out.println("-->"+ds[0]);
        System.out.println("-->"+ds[1]);
    }
}