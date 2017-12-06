package hyj.weixin_008.util;

import org.litepal.crud.DataSupport;

import java.util.List;

import hyj.weixin_008.daoModel.Wx008Data;

/**
 * Created by asus on 2017/11/26.
 */

public class DaoUtil {
    public static List<Wx008Data> getWx008Datas(){
        List<Wx008Data> wx008Datas = DataSupport.where("(expMsg  not like ? and expMsg  not like ?) or expMsg is null","%被限制登录%","%保护状态%").order("createTime asc").find(Wx008Data.class);
        return wx008Datas;
    }

    public static Integer getLoginFailNum(){
        List<Wx008Data> wx008Datas = DataSupport.where("loginState='0'").find(Wx008Data.class);
        if(wx008Datas==null)
            return null;
        else
            return wx008Datas.size();
    }
}