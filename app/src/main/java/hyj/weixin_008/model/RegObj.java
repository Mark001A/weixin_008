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

    public RegObj(List<String[]> datas, Map<String, String> accounts) {
        this.datas = datas;
        this.accounts = accounts;
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
