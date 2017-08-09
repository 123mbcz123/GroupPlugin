package Top.DouJiang.GroupPlugin;

import Top.DouJiang.Static.StaticMap;

import java.util.Map;

/**
 * Created by NicoNicoNi on 2017/8/6 0006.
 */
public class CleanSleepGroup extends Thread {
    /*
    此线程用于把不活跃的群
    从Redis(内存) 中清除
     */
    public void run(){
        while(GroupStaticMap.isRunning){
            Map<String,Integer> LifeGroup=GroupStaticMap.LifeMap;
            for(String str:LifeGroup.keySet()){
                int i=LifeGroup.get(str);
                i++;
                //十秒不活跃移除Redis
                if(i>=10/*缓存时间*/){
                    LifeGroup.remove(str);
                    GroupTools.CleanGroupClassFromRedis(str);
                }else{
                    LifeGroup.put(str,i);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
