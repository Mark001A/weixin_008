package hyj.weixin_008.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */

public class AutoChatObj {
    private int msgIndex;
    private int wxidIndex;
    private List<String> wxids = new ArrayList<String>();

    public AutoChatObj(){
        wxids.add("hyj5690");
        wxids.add("w666mb");
        wxids.add("w333wc");
    }

    public String getWxid(){
        return wxids.get(wxidIndex);
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    public int getWxidIndex() {
        return wxidIndex;
    }

    public void setWxidIndex(int wxidIndex) {
        this.wxidIndex = wxidIndex;
    }

    public List<String> getWxids() {
        return wxids;
    }

    public void setWxids(List<String> wxids) {
        this.wxids = wxids;
    }
}
