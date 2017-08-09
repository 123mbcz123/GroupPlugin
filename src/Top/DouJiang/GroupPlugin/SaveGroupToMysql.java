package Top.DouJiang.GroupPlugin;

/**
 * Created by NicoNicoNi on 2017/8/6 0006.
 */
public class SaveGroupToMysql extends Thread{
    /*
    此类用于群数据被改变之后
    数据将从Redis --> Mysql
    数据并不会从Redis卸载
     */
    private String GroupId=null;
    public void run(){
    }
    public SaveGroupToMysql(String GroupId){
        this.GroupId=GroupId;
    }

}
