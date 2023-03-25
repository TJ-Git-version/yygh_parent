package top.forforever.yygh.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.forforever.yygh.oss.prop.OssProperties;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @create: 2023/3/20
 * @Description:
 * @FileName: OssSerivce
 * @自定义内容：
 */
@Service
public class OssService {

    @Autowired
    private OssProperties ossProperties;

    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ossProperties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ossProperties.getKeyid();
        String accessKeySecret = ossProperties.getKeysecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ossProperties.getBucketname();

        //设置上传图片的名称
        String dateTime = new DateTime().toString("yyyy/MM/dd");
        String filename =dateTime + "/" +UUID.randomUUID().toString().replaceAll("-", "") + file.getOriginalFilename();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        //https://tj-yygh.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230130192217.jpg
        try {
            ossClient.putObject(bucketName, filename, file.getInputStream());
            StringBuffer append = new StringBuffer()
                    .append("https://")
                    .append("%s")
                    .append(".")
                    .append("%s")
                    .append("/")
                    .append("%s");
            return String.format(append.toString(), ossProperties.getBucketname(), ossProperties.getEndpoint(), filename);
        } catch (Exception ce) {
            System.out.println("Error Message:" + ce.getMessage());
            return "";
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
