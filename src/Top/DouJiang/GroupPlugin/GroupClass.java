package Top.DouJiang.GroupPlugin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NicoNicoNi on 2017/8/5 0005.
 */
public class GroupClass implements Serializable{
    /**
     *
     * @param Member_Set 成员列表 包涵管理员 群主
     * @param Harem_Master 群主
     * @param Harem_Manager 管理员
     * @param GroupId 群ID
     */
    public GroupClass(Set<String> Member_Set,String  Harem_Master,Set<String> Harem_Manager,String GroupId){
        this.Member_Set=Member_Set;
        this.Harem_Manager=Harem_Manager;
        this.Harem_Master=Harem_Master;
        this.GroupId=GroupId;
    }

    public Set<String> getMember_Set() {
        return Member_Set;
    }

    private Set<String> Member_Set=null;

    public String getHarem_Master() {
        return Harem_Master;
    }

    private String  Harem_Master=null;

    public Set<String> getHarem_Manager() {
        return Harem_Manager;
    }

    private Set<String> Harem_Manager= null;

    public String getGroupId() {
        return GroupId;
    }

    private String GroupId=null;
}
