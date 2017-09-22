package hyj.weixin_008.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/8/20.
 */

public class AddFrObj {
    private int currentIndex=0;
    private List<String> wxList = new ArrayList<String>();
    private boolean isBdPhone;
    public AddFrObj(){
        /*wxList.add("cba4871");
        wxList.add("nnk4869");*/
        /*wxList.add("hyj5690");
        wxList.add("zhjj520366");*/

        wxList.add("w666mb");
        wxList.add("w777mb");
        wxList.add("w888mb");
        wxList.add("w333mb");
        wxList.add("w222mb");
        wxList.add("w333wc");
        wxList.add("w444wc");
       /* wxList.add("w666wc");
        wxList.add("w777wc");
        wxList.add("w222wt");*/
    }

    public boolean isBdPhone() {
        return isBdPhone;
    }

    public void setBdPhone(boolean bdPhone) {
        isBdPhone = bdPhone;
    }

    public String getOneWx(){
        return  wxList.get(this.currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<String> getWxList() {
        return wxList;
    }

    public void setWxList(List<String> wxList) {
        this.wxList = wxList;
    }
}
