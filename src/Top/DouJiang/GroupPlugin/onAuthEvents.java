package Top.DouJiang.GroupPlugin;

import Top.DouJiang.Static.StaticMap;
import Top.DouJiang.Util.Mysqls.ConnectionPool;
import Top.DouJiang.Util.Redis.RedisUtil;
import Top.DouJiang.plugin.AuthClass;
import Top.DouJiang.plugin.AuthEvent;
import Top.DouJiang.plugin.AuthEventResult;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by NicoNicoNi on 2017/8/10 0010.
 */
public class onAuthEvents implements AuthEvent{
    /*
    优化第一步:
    查找活跃群:
    添加活跃群里的人员
    此程序不会创建活跃群对象
     */
    @Override
    public AuthEventResult PlayerSuccessfulAuthEvent(AuthClass ac) {
        String Id=ac.getS().getId();
        Set<String> Groups=GetGroupIdSet(Id);
        if(Groups==null){
            return null;
        }
        for(String group:Groups){
            Set<String> Activity=GetGroupActivePeople(group);
            if(Activity==null){
                continue;
            }
            Activity.add(Id);
            SetGroupActivePeople(group,Activity);
        }
        return null;
    }
    /*
    获取玩家的所有群
     */
    public Set<String> GetGroupIdSet(String Id){
        ConnectionPool.PooledConnection pool= StaticMap.pool;
        PreparedStatement ps=null;
        ResultSet rs=null;
        ObjectInputStream in =null;
        Object obj =null;
        try {
            ps=pool.getPrepareStatement("select groups from user_text where id=?;");
            ps.setString(1,Id);
            rs=ps.executeQuery();
            if(rs.next()){
                in = new ObjectInputStream(rs.getBlob("groups").getBinaryStream());
                obj = in.readObject();
                in.close();
                if(!(obj instanceof Set)){
                    return null;
                    //
                }
                return  (Set<String>)obj;
            }
        } catch (SQLException e) {
            //
        } catch (IOException e) {
            //
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return null;
    }
    public void SetGroupActivePeople(String GroupId,Set<String> ActivePeople){
        RedisUtil ru=StaticMap.ru;
        ru.SerializeObject("GroupActivity_"+GroupId,ActivePeople);
    }
    public Set<String> GetGroupActivePeople(String GroupId){
        Set<String> Activity=null;
        RedisUtil ru=StaticMap.ru;
        Object obj=null;
        if(!ru.exists("GroupActivity_"+GroupId)){
            return null;
        }
        obj=ru.UnSerializeObject("GroupActivity_"+GroupId);
        if(obj instanceof Set){
            Activity=(Set<String>)obj;
        }
        return Activity;
    }
}
