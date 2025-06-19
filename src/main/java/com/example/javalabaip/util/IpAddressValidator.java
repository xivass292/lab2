package com.example.javalabaip.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddressValidator {

    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        try {
            InetAddress.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}