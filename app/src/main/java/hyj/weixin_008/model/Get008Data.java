package hyj.weixin_008.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.AutoUtil;

/**
 * Created by asus on 2017/8/17.
 */

public class Get008Data {
    private String currentPhone;
    private int currentIndex = 0;
    private Map<String,String> record = new HashMap<String,String>();
    private List<String> phones = new ArrayList<String>();


    public Get008Data(){
        AutoUtil.recordAndLog(record,"历史记录按钮界面");
        phones.add("18725091354");
        phones.add("18725051945");
        phones.add("15974801479");
        phones.add("18725199713");
        phones.add("15288302694");
        phones.add("18313988424");
        phones.add("15198949813");
        phones.add("18788481051");
        phones.add("15198749023");
        phones.add("18725145945");
        phones.add("18725042844");
        phones.add("15808892544");
        phones.add("15288190453");
        phones.add("15887856214");
        phones.add("18313992453");
        phones.add("15887247873");
        phones.add("15925148752");
        phones.add("15288350141");
        phones.add("17191725074");
    }
    public String getPoneByThisIndex(){
        return phones.get(this.getCurrentIndex());
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
