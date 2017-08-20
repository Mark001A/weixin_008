package hyj.weixin_008.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/18.
 */

public class RegObj {
    private List<String[]> datas;
    private Map<String,String> accounts;
    private String currentAccount;
    private int currentIndex=0;
    private int totalNum;
    private int loginSuccessNum=0;
    private int loginFailNum=0;

    private String zc2;
    private String zc3;
    private String addSpFr;

    public RegObj(List<String[]> datas, Map<String, String> accounts,String zc2,String zc3,String addSpFr) {
        this.datas = datas;
        this.accounts = accounts;
        this.zc2 = zc2;
        this.zc3 = zc3;
        this.addSpFr = addSpFr;
    }

    public String getAddSpFr() {
        return addSpFr;
    }

    public void setAddSpFr(String addSpFr) {
        this.addSpFr = addSpFr;
    }

    public String getZc2() {
        return zc2;
    }

    public void setZc2(String zc2) {
        this.zc2 = zc2;
    }

    public String getZc3() {
        return zc3;
    }

    public void setZc3(String zc3) {
        this.zc3 = zc3;
    }

    public int getTotalNum() {
        return datas.size();
    }

    public List<String[]> getDatas() {
        return datas;
    }

    public void setDatas(List<String[]> datas) {
        this.datas = datas;
    }

    public Map<String, String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, String> accounts) {
        this.accounts = accounts;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getLoginSuccessNum() {
        return loginSuccessNum;
    }

    public void setLoginSuccessNum(int loginSuccessNum) {
        this.loginSuccessNum = loginSuccessNum;
    }

    public int getLoginFailNum() {
        return loginFailNum;
    }

    public void setLoginFailNum(int loginFailNum) {
        this.loginFailNum = loginFailNum;
    }
}
