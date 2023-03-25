package hucx.ddns;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class NetworkUtils {
    public static final String MAC_REGEX = "((([a-fA-F0-9]{2}:){5})|(([a-fA-F0-9]{2}-){5}))[a-fA-F0-9]{2}";

    public static InetAddress getTargetAddress(@NotNull String mac) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface targetNetworkInterface = networkInterfaces.nextElement();
            if (Arrays.equals(getMacBytes(mac), targetNetworkInterface.getHardwareAddress())) {
                Logger global = Logger.getGlobal();
                global.info("目标网卡名称: " + targetNetworkInterface);
                List<InterfaceAddress> interfaceAddresses = targetNetworkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    // prefixLength == 128 means it is Temporary IPv6 Address.
                    short networkPrefixLength = interfaceAddress.getNetworkPrefixLength();
                    if (networkPrefixLength == 128) {
                        return interfaceAddress.getAddress();
                    }
                }
            }
        }
        throw new SocketException();
    }

    private static byte[] getMacBytes(@NotNull String mac) {
        boolean matches = Pattern.matches(MAC_REGEX, mac);
        if (!matches) {
            throw new IllegalArgumentException();
        }
        String regex = String.valueOf(mac.charAt(2));
        byte[] macBytes = new byte[6];

        String[] strArr = mac.split(regex);
        for (int i = 0; i < strArr.length; i++) {
            int value = Integer.parseInt(strArr[i], 16);
            macBytes[i] = (byte) value;
        }

        return macBytes;
    }
}
