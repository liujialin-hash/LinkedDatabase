package util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    //读取配置文件
    private Properties properties=new Properties();

    //私有化构造器
    private static ConfigManager configManager;
    private ConfigManager(){
       //开始读取配置文件
        InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream("database.properties");
        try {
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取value
    public String getValue(String key){
        return properties.getProperty(key);
    }

    //获取构造的方法
    public static ConfigManager getConfigManager(){
        if (configManager==null){
            configManager=new ConfigManager();
        }
        return configManager;
    }


}
