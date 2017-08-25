package hyj.weixin_008.daoModel;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/22.
 */

public class Wx008Data extends DataSupport {
    private String datas;
    private String phone;
    private String wxId;
    private String wxPwd;//属性名称为pwd无法保存？
    private String expMsg;
    private int dieFlag;//0正常 1账号异常 2 操作频率过快 3 登录环境异常 长期未登录  批量注册 4手机不在身边
    private Date createTime;

    public Wx008Data() {
    }

    public String getWxPwd() {
        return wxPwd;
    }

    public void setWxPwd(String wxPwd) {
        this.wxPwd = wxPwd;
    }

    public String getExpMsg() {
        return expMsg;
    }

    public void setExpMsg(String expMsg) {
        this.expMsg = expMsg;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }


    public int getDieFlag() {
        return dieFlag;
    }

    public void setDieFlag(int dieFlag) {
        this.dieFlag = dieFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
