package com.sncfc.scheduler.client.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class NetUtil {

    public static List<String> getAllLocalIP() throws SocketException {
        List<String> localIPs = new ArrayList<String>();
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface =   allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip =  addresses.nextElement();
                if (ip != null && !ip.isLoopbackAddress() &&
                        ip instanceof Inet4Address) {
                    localIPs.add(ip.getHostAddress());
                }
            }
        }
        return localIPs;
    }

    public static String getLocalIP() {
        try {
            List<String> allIps = getAllLocalIP();
            if (null == allIps || allIps.size() < 1) {
                return "unknown IP";
            } else {
                return allIps.get(0);
            }
        } catch (SocketException se) {
            return "unknown IP";
        }
    }
}
