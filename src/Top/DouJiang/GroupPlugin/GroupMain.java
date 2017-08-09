package Top.DouJiang.GroupPlugin;
import Top.DouJiang.APIs.API;
import Top.DouJiang.ServerSocket.Sockets;
import Top.DouJiang.Tool.SocketTools;
import Top.DouJiang.Tool.SystemTools;
import Top.DouJiang.plugin.*;

import java.util.HashMap;
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
            return null;
        }
        //ConnectionPool.PooledConnection pool= API.GetPooledConnection();
        String GroupId=tc.getToId();
       // pool.getPrepareStatement()
        if(!GroupTools.isInGroup(tc.getFromId(),GroupId)){
            //未完待续
            //不在此群中...
            return null;
        }
        GroupClass gc=GroupTools.GetGroup(GroupId);
        if(gc==null) {
            //待处理
            return null;
        }
        Set<String> Member_Set=gc.getMember_Set();
        for(String s:Member_Set){
            Sockets ss=API.GetSocketThroughId(s);
            if(ss!=null){
                //本群所有的人,如果在线 发送消息
                Map<String,String> MsgMap=new HashMap<>();
                MsgMap.put("Cmd","Chat");
                MsgMap.put("TypeId","1");
                MsgMap.put("Msg",tc.getMsg());
                MsgMap.put("FromId",tc.getFromId());
                ss.Send(SocketTools.MapToJson(MsgMap)); //广播消息
            }
        }
        return null;
    }

    @Override
    public void onEnable() {
        GroupStaticMap.isRunning=true;
        new CleanSleepGroup().start();
    }

    @Override
    public void onDisable() {
        GroupStaticMap.isRunning=false;
    }
}
