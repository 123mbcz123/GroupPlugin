package Top.DouJiang.GroupPlugin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NicoNicoNi on 2017/8/10 0010.
 */
public class GroupActivity implements Serializable {
    private String GroupId=null;
    public String GetGroupId(){
        return GroupId;
    }
    public GroupActivity(String GroupId){
        this.GroupId=GroupId;
    }
    public Set<String> GetActiveUserId(){
        return ActiveUserId;
    }
    private Set<String> ActiveUserId=new HashSet<>();
    public void AddActiveUserId(String Id){
        ActiveUserId.add(Id);
    }
    public void RemoveActiveUserId(String Id){
        ActiveUserId.remove(Id);
    }
    public boolean isInActiveUserId(String Id){
        for(String s:ActiveUserId){
            if(s.equalsIgnoreCase(Id)){
                return true;
            }
        }
        return false;
    }
}
