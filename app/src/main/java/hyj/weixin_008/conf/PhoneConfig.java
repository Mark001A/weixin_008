package hyj.weixin_008.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/14.
 */

public class PhoneConfig {
    static Map<String, Map<String,String>> agent =new HashMap<String, Map<String,String>>();
    static Map<String, String> alzUrls =new HashMap<String,String>();
    static{
        agent.put("alz",alzUrls);
        alzUrls.put("mainUrl","http://api.xingjk.cn/api/do.php");
    }
}
