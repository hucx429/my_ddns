package hucx.ddns;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.dnspod.v20210323.models.RecordListItem;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger;

    private static final String LOG_PATH = "/log.log";
    // 腾讯云API的 SECRET_ID
    private static final String SECRET_ID = "";
    // 腾讯云API的 SECRET_KEY
    private static final String SECRET_KEY = "";
    // 要同步的主域名 例：org.example
    private static final String DOMAIN = "";
    // 子域名 例：www
    private static final String SUB_DOMAIN = "";
    // 设备网卡的MAC地址 例：D9-C6-37-A1-B2-C3
    private static final String PHYSICAL_ADDRESS = "";

    static {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String parent = new File(path).getParent();
        String logPath = parent + LOG_PATH;
        logger = Logger.getGlobal();
        logger.setUseParentHandlers(false);
        try {
            SimpleLogFormatter formatter = new SimpleLogFormatter();
            FileHandler fileHandler = new FileHandler(logPath, true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.throwing("ddns.hucx.Main", "static block", e);
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress targetAddress = NetworkUtils.getTargetAddress(PHYSICAL_ADDRESS);
            logger.info("---设备当前的IPV6地址: " + targetAddress.getHostAddress());
            AutoConfigDnsHelper autoConfigDnsHelper = new AutoConfigDnsHelper(SECRET_ID, SECRET_KEY);
            RecordListItem currentRecordInfo = autoConfigDnsHelper.getCurrentRecordInfo(SUB_DOMAIN, DOMAIN);
            InetAddress currentInfoAddress = InetAddress.getByName(currentRecordInfo.getValue());
            logger.info("DNS记录解析的IPV6地址: " + currentInfoAddress.getHostAddress());
            logger.info("DNS记录上一次修改时间: " + currentRecordInfo.getUpdatedOn());
            boolean equals = currentInfoAddress.equals(targetAddress);
            if (equals) {
                logger.info("不需要修改");
                return;
            }
            autoConfigDnsHelper.modifyRecord(currentRecordInfo, targetAddress, DOMAIN);
            logger.info("DNS解析记录同步成功");
        } catch (SocketException e) {
            logger.warning("获取当前网络信息时发生异常!");
            logger.throwing("ddns.hucx.NetworkUtils", "getTargetAddress", e);
        } catch (TencentCloudSDKException e) {
            logger.warning("请求腾讯云API时发生异常!");
            logger.throwing("ddns.hucx.AutoConfigDnsHelper", "getCurrentRecordInfo", e);
        } catch (UnknownHostException e) {
            logger.warning("DNS解析记录值转换时发生异常!");
            logger.throwing("java.net.InetAddress", "getByName", e);
        }
    }
}
