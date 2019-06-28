package top.dadagum.oss.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;

import java.io.InputStream;

/**
 * @Description TODO
 * @Author Honda
 * @Date 2019/6/27 20:19
 **/
public class SimpleUploader {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    private static final String endpoint = "";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    private static final String accessKeyId = "";
    private static final String accessKeySecret = "";
    private static final String bucketName = "douban-test";

    /**
     * 将二进制流上传到 oss 中的文件夹中
     * @param stream
     * @param file /dir/fileName.suffix
     */
    public static void uploadFileStream(InputStream stream, String file) {
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        // 上传网络流。
        PutObjectResult putObjectResult = ossClient.putObject(bucketName, file, stream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

}
