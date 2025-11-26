package com.fcgo.eft.sutra.util;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Getter
@Service
public class IsProdService {
    private boolean isProdService = false;
    private String prodIpAddress = "";

    public void init() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue; // skip loopback and down interfaces

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getHostAddress().contains("10.100.193.76")) {
                        isProdService = true;
                        prodIpAddress = "10.100.193.76";
                    }
                    System.out.println("Local IP Address: " + addr.getHostAddress());
                }
            }
        } catch (Exception ignored) {
        }
    }
}
