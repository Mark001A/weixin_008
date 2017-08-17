package hyj.weixin_008.model;

import java.util.HashMap;
import java.util.Map;

import hyj.weixin_008.AutoUtil;

/**
 * Created by asus on 2017/8/17.
 */

public class Get008Data {
    private String currentPhone;
    private int currentIndex = 0;
    private Map<String,String> record = new HashMap<String,String>();

    public Get008Data(){
        AutoUtil.recordAndLog(record,"历史记录按钮界面");
    }

    public String getCurrentPhone() {
        return currentPhone;
    }

    public void setCurrentPhone(String currentPhone) {
        this.currentPhone = currentPhone;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Map<String, String> getRecord() {
        return record;
    }

    public void setRecord(Map<String, String> record) {
        this.record = record;
    }
}
