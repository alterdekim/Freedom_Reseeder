package com.alterdekim.freedom.reseeder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Network {
    public static String getDomainName( String url ) throws Exception {
        String pattern = "(\\w*://)([\\w-_.]+)([:\\w\\W]*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    public static String getDomainZone( String url ) throws Exception {
        String pattern = "(\\w*://)([\\w-_.]+)([:\\w\\W]*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            String domain = m.group(2);
            return domain.split("\\.")[domain.split("\\.").length-1];
        }
        return "";
    }
}
