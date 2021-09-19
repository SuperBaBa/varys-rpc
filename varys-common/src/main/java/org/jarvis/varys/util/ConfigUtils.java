package org.jarvis.varys.util;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class ConfigUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\s*\\{?\\s*([\\._0-9a-zA-Z]+)\\s*\\}?");
    private static volatile Properties PROPERTIES;


    String ENABLED_KEY = "enabled";

    String DISABLED_KEY = "disabled";

    static String PROPERTIES_KEY = "dubbo.properties.file";

    static String DEFAULT_PROPERTIES = "dubbo.properties";

   /* public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && value.length() > 0) {
            return value;
        }
        Properties properties = getProperties();
        return replaceProperty(properties.getProperty(key, defaultValue), (Map) properties);
    }

    public static Properties getProperties() {
        if (PROPERTIES == null) {
            synchronized (ConfigUtils.class) {
                if (PROPERTIES == null) {
                    String path = System.getProperty(PROPERTIES_KEY);
                    if (path == null || path.length() == 0) {
                        path = System.getenv(PROPERTIES_KEY);
                        if (path == null || path.length() == 0) {
                            path = DEFAULT_PROPERTIES;
                        }
                    }
                    PROPERTIES = ConfigUtils.loadProperties(path, false, true);
                }
            }
        }
        return PROPERTIES;
    }*/

}
