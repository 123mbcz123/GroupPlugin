package Top.DouJiang.GroupPlugin;

import Top.DouJiang.ServerSocket.Sockets;
import Top.DouJiang.Static.StaticMap;
import Top.DouJiang.Util.Mysqls.ConnectionPool;
import Top.DouJiang.Util.Redis.RedisUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by NicoNicoNi on 2017/8/5 0005.
 */
public class GroupTools {
    /*
     判断时候在此QQ群
     QQ群不存在||不在QQ群
     都会返回 false
     */
    public static boolean isInGroup(String id,String GroupId){
        GroupClass gc=GetGroup(GroupId);
        if(gc==null){
            return false;
        }
        Set<String > MemberSet=gc.getMember_Set();
        for(String s:MemberSet){
            if(s.equalsIgnoreCase(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * 此方法用于获取群对象
     * 会优先从Redis中寻找
     * @param GroupId QQ群号
     * @return  返回GroupClass群对象
     **/
    public static GroupClass GetGroup(String GroupId){

        GroupClass gc=null;
        gc=ReadGroupClassFromRedis(GroupId);
        if(gc==null){
            gc=ReadGroupClassFromMysql(GroupId);
        }
        return gc;
    }
    public static void CleanGroupClassFromRedis(String GroupId){
        RedisUtil ru=StaticMap.ru;
        ru.del(GroupId);
    }
    public static GroupClass ReadGroupClassToRedisFromMysql(String GroupId){
        GroupClass gc=ReadGroupClassFromMysql(GroupId);
        if(gc==null){
            return null;
        }
        RedisUtil ru=StaticMap.ru;
        ru.SerializeObject(GroupId,gc);
        return gc;
    }
    public static GroupClass ReadGroupClassFromRedis(String GroupId){
        RedisUtil ru= StaticMap.ru;
        if(!ru.exists(GroupId)) {
            return null;
        }
        Object o=ru.UnSerializeObject(GroupId);
        if(!(o instanceof GroupClass)){
            return null;
        }
        return (GroupClass)o;
    }
    public static GroupClass ReadGroupClassFromMysql(String GroupId){
        ConnectionPool.PooledConnection pool=StaticMap.pool;
        ResultSet rs=null;
        PreparedStatement ps=null;
        ObjectInputStream in =null;
        Object obj =null;
        try {
            ps=pool.getPrepareStatement("select * from Groups where Group_Id=?;");
            ps.setString(1,GroupId);
            rs=ps.executeQuery();
            if(rs.next()){
               String Master = rs.getString("Group_Master");
                in = new ObjectInputStream(rs.getBlob("Group_Manager").getBinaryStream());
                obj = in.readObject();
                in.close();
                if(!(obj instanceof Set)){
                    return null;
                    //
                }
                Set<String> Manger_Set=(Set<String>)obj;
                in = new ObjectInputStream(rs.getBlob("Group_Member").getBinaryStream());
                obj = in.readObject();
                in.close();
                if(!(obj instanceof Set)){
                    return null;
                    //
                }
                Set<String> Member_Set=(Set<String>)obj;
               GroupClass gc=new GroupClass(Member_Set,Master,Manger_Set,GroupId);
               return gc;
            }else{
                //查询异常
                return null;
            }
        } catch (SQLException e) {
            //查询异常
            return null;
        } catch (IOException e) {
           // e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return null;
    }
    public static void SaveToRedis(GroupClass gc){

    }
    /*
    更新群主信息
     */
    public static int UpdateGroupMasterMysql(String GroupId,String Master){
        ConnectionPool.PooledConnection pool=StaticMap.pool;
        PreparedStatement ps=null;
        try {
            ps=pool.getPrepareStatement("update groups set Group_Master=? where Group_Id=?;");
            ps.setString(1,Master);
            ps.setString(2,GroupId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return -1;
    }
    /*
    更新群成员列表
     */
    public static int UpdateGroupMemberMysql(String GroupId,Set<String> Member_Set){
        ConnectionPool.PooledConnection pool=StaticMap.pool;
        PreparedStatement ps=null;
        try {
            ps=pool.getPrepareStatement("update groups set Group_Member=? where Group_Id=?;");
            ps.setObject(1,Member_Set);
            ps.setString(2,GroupId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /*
    更新群管理列表
     */
    public static int UpdateGroupManagerMysql(String GroupId,Set<String> Manager_Set){
        ConnectionPool.PooledConnection pool=StaticMap.pool;
        PreparedStatement ps=null;
        try {
            ps=pool.getPrepareStatement("update groups set Group_Manager=? where Group_Id=?;");
            ps.setObject(1,Manager_Set);
            ps.setString(2,GroupId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /*

     */
}
