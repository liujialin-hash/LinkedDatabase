package BaseDao;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.ConfigManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Base {
    //链接数据库
    private Connection con;
    //操作数据库
    private PreparedStatement pst;
    //结果集
    private ResultSet rs;
    
    private String url= config().getValue("url");
    
    private String driver=config().getValue("driver");
    
    private String username=config().getValue("username");
    
    private String password=config().getValue("password");
    private String sql;

    //调用工具类
    private ConfigManager config(){
        ConfigManager configManager = ConfigManager.getConfigManager();
        return configManager;
    }
    
    //获取链接
    private Connection getConnection(){
        try {
            Class.forName(driver);
            con=DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    
    //开始操作数据库



    public  <T>List<T>  selectForList(String sql, Object [] params, Class<T> clazz ){
        con=getConnection();
        List<T> list=new ArrayList<>();
        try {
            pst=con.prepareStatement(sql);
            if (params!=null){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i+1,params[i]);
                }
            }
            rs=pst.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            Map<String,Object> map=new HashMap<>();

           while (rs.next()){
               for (int i = 1; i <columnCount; i++) {
                   map.put(md.getColumnLabel(i),rs.getObject(i));
               }
               String jsonStr= JSONObject.toJSONString(map);
               Object o = JSONObject.parseObject(jsonStr, clazz);
               T o1 = (T) o;
               list.add(o1);
           }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public <T> T select(String sql, Object [] params,Class<T> clazz){
        this.sql = sql;
        con=getConnection();
        Object o=null;
        try {
            pst=con.prepareStatement(sql);
            if (params!=null){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i+1,params[i]);
                }
            }
            rs=pst.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            Map<String,Object> map=new HashMap<>();
            int count=0;
            rs.last();
            count=rs.getRow();
            if (count>1){
                throw new RuntimeException("返回条数过多");
            }
            rs.first();
            while (rs.next()){
                for (int i = 1; i <columnCount; i++) {
                    map.put(md.getColumnLabel(i),rs.getObject(i));
                }
                String jsonStr=JSONObject.toJSONString(map);

                //这是第一种办法
//                T t = clazz.newInstance();
//                Class<?> aClass = t.getClass();
//                o= JSONObject.parseObject(jsonStr, aClass);
//                count++;
//                if (count>1){
//                    throw new RuntimeException("返回条数过多");
//                }


                ObjectMapper mapper = new ObjectMapper();

                //这是第种
               o = mapper.readValue(jsonStr, clazz);
                System.out.println(o);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        T o1= (T) o;

        return o1 ;

    }

    public Integer update(String sql,Object [] params){
        con=getConnection();
        int count=0;
        try {
            pst=con.prepareStatement(sql);
            if (params!=null){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i+1,params[i]);
                }
            }
            count=pst.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
        extracted(con,pst,rs);
        return count;
    }

    private void extracted(Connection con,PreparedStatement pst, ResultSet rs) {
        try {
            if (rs!=null){
                rs.close();
            }
            if (pst!=null) {
                pst.close();
            }
            if (con!=null){
                con.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
