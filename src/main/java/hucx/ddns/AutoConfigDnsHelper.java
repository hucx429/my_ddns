package hucx.ddns;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class AutoConfigDnsHelper {
    private final DnspodClient client;

    public AutoConfigDnsHelper(String secretId, String secretKey) {
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        // 实例化要请求产品的client对象,clientProfile是可选的
        client = new DnspodClient(cred, "");
    }

    public RecordListItem getCurrentRecordInfo(@NotNull String subDomain, @NotNull String domain) throws TencentCloudSDKException {
        DescribeRecordListRequest req = new DescribeRecordListRequest();
        req.setDomain(domain);
        // 返回的resp是一个DescribeRecordListResponse的实例，与请求对象对应
        DescribeRecordListResponse resp = client.DescribeRecordList(req);
        RecordListItem[] recordList = resp.getRecordList();
        for (RecordListItem record : recordList) {
            if (subDomain.equals(record.getName())) {
                return record;
            }
        }
        return null;
    }


    public void modifyRecord(RecordListItem currentRecordInfo, InetAddress targetAddress, String domain) throws TencentCloudSDKException {
        ModifyRecordRequest req = new ModifyRecordRequest();
        req.setDomain(domain);
        req.setSubDomain(currentRecordInfo.getName());
        req.setRecordType(currentRecordInfo.getType());
        req.setRecordLine(currentRecordInfo.getLine());
        req.setValue(targetAddress.getHostAddress());
        req.setRecordId(currentRecordInfo.getRecordId());
        // 返回的resp是一个ModifyRecordResponse的实例，与请求对象对应
        ModifyRecordResponse resp = client.ModifyRecord(req);
    }
}
