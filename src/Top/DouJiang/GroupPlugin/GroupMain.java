package Top.DouJiang.GroupPlugin;
import Top.DouJiang.APIs.API;
import Top.DouJiang.ServerSocket.Sockets;
import Top.DouJiang.Tool.SocketTools;
import Top.DouJiang.Tool.SystemTools;
import Top.DouJiang.plugin.*;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NicoNicoNi on 2017/8/5 0005.
 */
public class GroupMain implements PluginMain,ChatEvents{
    @Override
    public TaskResult ChatEvent(TaskClass tc) {
        if(tc.getType()!=1){
            //不为QQ群请求跳过
            //System.out.println("不为QQ群请求跳过");
            return null;
        }
        //ConnectionPool.PooledConnection pool= API.GetPooledConnection();
        String GroupId=tc.getToId();
        //System.out.println("来自: "+tc.getFromId()+" GroupId: "+GroupId);
       // pool.getPrepareStatement()
        GroupClass gc=GroupTools.GetGroup(GroupId);
        if(gc==null) {
           // System.out.println("错误的群");
            return null;
        }
        Set<String> Member_Set=gc.getMember_Set();
        Set<Sockets> Online_Set=new HashSet<>();
        boolean isInGroup=false;
        for(String s:Member_Set){
            Sockets ss=API.GetSocketThroughId(s);
            if(ss!=null){
                Online_Set.add(ss);
                if(ss.getId()!=null&&ss.getId().equalsIgnoreCase(tc.getFromId())){
                    isInGroup=true;
                }
                //本群所有的人,如果在线 发送消息
            }
        }
        if(!isInGroup){
            //System.out.println("不在此群中");
            return null;
        }
        //System.out.println("Online: "+Online_Set);
        for(Sockets s:Online_Set){
            Map<String,String> MsgMap=new HashMap<>();
            MsgMap.put("Cmd","Chat");
            MsgMap.put("TypeId","1");
            MsgMap.put("Msg",SocketTools.Base64Encryption(tc.getMsg()));
            MsgMap.put("FromId",tc.getFromId());
            s.Send(SocketTools.MapToJson(MsgMap)); //广播消息
        }
        return null;
    }

    @Override
    public void onEnable() {
        GroupStaticMap.isRunning=true;
        new CleanSleepGroup().start();
        SystemTools.Print("QQ群功能加载完成!",1,0);
    }

    @Override
    public void onDisable() {
        GroupStaticMap.isRunning=false;
    }
}
