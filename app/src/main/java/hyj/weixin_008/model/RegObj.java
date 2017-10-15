package hyj.weixin_008.model;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hyj.weixin_008.daoModel.Wx008Data;

/**
 * Created by Administrator on 2017/8/18.
 */

public class RegObj {
    private List<String[]> datas;//原存在txt数据
    private List<Wx008Data> wx008Datas;//数据库数据

    //private String currentAccount;
    private int currentIndex=0;
    private int totalNum;
    private int loginSuccessNum=0;
    private int loginFailNum=0;

    private String zc2;
    private String zc3;
    private String addSpFr;
    private String airplane;
    private String get008Data;//是否提取008数据

    private int airplaneChangeIpNum;//飞行模式换ip数

    private String currentIP;



    public RegObj(String airplane,String zc2,String zc3,String addSpFr,List<Wx008Data> wx008Datas,int airplaneChangeIpNum) {
        this.zc2 = zc2;
        this.zc3 = zc3;
        this.addSpFr = addSpFr;
        this.wx008Datas = wx008Datas;
        this.airplane = airplane;
        this.airplaneChangeIpNum = airplaneChangeIpNum;

       //兼容旧数据
        datas = new ArrayList<String[]>();
        for(Wx008Data data:wx008Datas){
            datas.add(JSONObject.parseObject(data.getDatas(),String[].class));
        }
    }

   /* public Wx008Data getCurrentWx008Data(){
        return wx008Datas.get(this.currentIndex);
    }*/

    public int getAirplaneChangeIpNum() {
        return airplaneChangeIpNum;
    }

    public void setAirplaneChangeIpNum(int airplaneChangeIpNum) {
        this.airplaneChangeIpNum = airplaneChangeIpNum;
    }

    public List<Wx008Data> getWx008Datas() {
        return wx008Datas;
    }

    public String getCurrentIP() {
        return currentIP;
    }

    public void setCurrentIP(String currentIP) {
        this.currentIP = currentIP;
    }

    public String getAirplane() {
        return airplane;
    }

    public void setAirplane(String airplane) {
        this.airplane = airplane;
    }

    public void setWx008Datas(List<Wx008Data> wx008Datas) {
        this.wx008Datas = wx008Datas;
    }

    public String getGet008Data() {
        return get008Data;
    }

    public void setGet008Data(String get008Data) {
        this.get008Data = get008Data;
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


   /* public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }*/

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
