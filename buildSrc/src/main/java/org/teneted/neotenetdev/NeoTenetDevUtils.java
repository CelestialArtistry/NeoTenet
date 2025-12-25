package org.teneted.neotenetdev;

import java.util.TimeZone;

public class NeoTenetDevUtils {

    public static boolean isCN() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        String id = tz.getID();
        return id.contains("China") || id.equals("Asia/Shanghai") || id.equals("Asia/Chongqing") || id.equals("Asia/Urumqi");
    }
}
