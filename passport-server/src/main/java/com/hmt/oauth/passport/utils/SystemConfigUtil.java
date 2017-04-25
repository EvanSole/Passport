package com.hmt.oauth.passport.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 读取系统资源配置文件 passportConfigs.properties
 */
public class SystemConfigUtil {

    private static final String CONFIG_FILE = "passportConfigs.properties";
    private static final Map<String, String> properties;

    public static String get(String key){
        return ((String)properties.get(key));
    }

    public static String get(String key, String defaultValue) {
        String value = (String)properties.get(key);
        return ((value == null) ? defaultValue : value);
    }

    static
    {
        Map _properties = new HashMap();

        Properties tmp = new Properties();
        InputStream in = SystemConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (in != null) {
            try {
                tmp.load(in);
            } catch (IOException e) {
                throw new RuntimeException(" Read passportConfigs.properties config file failed!", e);
            }
            for (Iterator i$ = tmp.entrySet().iterator(); i$.hasNext(); ) { Map.Entry entry = (Map.Entry)i$.next();
                _properties.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        properties = Collections.unmodifiableMap(_properties);
    }

}
