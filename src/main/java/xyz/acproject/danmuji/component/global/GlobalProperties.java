package xyz.acproject.danmuji.component.global;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhou
 * @Description: 单例类来实现全局属性的存储和访问
 * @Data: 2023-05-14
 */
public class GlobalProperties {
    private static List<GlobalProperty> properties = new ArrayList<>();

    static {
        GlobalProperty defaultProperty = new GlobalProperty("chat4.zhulei.xyz", "https://chat4.zhulei.xyz/api");
        properties.add(defaultProperty);
    }

    public static List<GlobalProperty> getProperties() {
        return properties;
    }

    public static void addProperty(GlobalProperty property) {
        properties.add(property);
    }

    public static void removeProperty(GlobalProperty property) {
        properties.remove(property);
    }

    public static void setProperties(List<GlobalProperty> newProperties) {
        properties = newProperties;
    }
}

