package com.newblog.huil.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Random;

/**
 * @author HuilLIN
 */
@Slf4j
@Component
public class OSSClientUtil {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.access-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key}")
    private String accessKeySecret;

    @Value("${aliyun.oss.dir}")
    private String filedir;

    @Value("${aliyun.oss.bucket}")
    private String bucketName;

    private OSS ossClient;

    @Bean
    public void init(){
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        ossClient = new OSSClientBuilder().build(endpoint,accessKeyId, accessKeySecret,conf);
        /* 通过一个Bucket对象来创建 */
        CreateBucketRequest bucketObj = new CreateBucketRequest(null);
        //设置bucketObj名称
        bucketObj.setBucketName(bucketName);
        // 设置bucketObj访问权限acl
        bucketObj.setCannedACL(CannedAccessControlList.PublicReadWrite);
        //创建Bucket
        ossClient.createBucket(bucketObj);
    }

    /**
     * 销毁
     */
    public void destory(){
        if(ossClient != null){
            ossClient.shutdown();
        }
    }

    public String uploadImg2Oss(String url) throws Exception {
        File fileOnServer = new File(url);
        FileInputStream fin;
        try{
            fin = new FileInputStream(fileOnServer);
            String[] split = url.split("/");
            this.uploadFile2OSS(fin,split[split.length-1]);
            return split[split.length-1];
        } catch (FileNotFoundException e) {
            throw new Exception("图片上传失败");
        }
    }

    public String uploadImage2Oss(MultipartFile file) throws Exception {
        if(file.getSize() > 1024 * 1024 *2){
            throw new Exception("图片上传大小不能超过2M");
        }
        String originalFilename = file.getOriginalFilename();
        String subString = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        String name = UUIDUtil.randomUUID32()+subString;
        try{
            InputStream inputStream = file.getInputStream();
            this.uploadFile2OSS(inputStream,name);
            return name;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("图片上传失败");
        }
    }

    public String getImgUrl(String fileUrl){
        if (StringUtils.isNotEmpty(StringUtils.trim(fileUrl))) {
            String[] split = fileUrl.split("/");
            return this.getUrl(this.filedir + split[split.length - 1]);
        }
        return null;
    }

    public String uploadFile2OSS(InputStream inputStream,String fileName){
        String ret = "";
        try{
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Param","no-cache");
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            //上传文件
            PutObjectResult putResult = ossClient.putObject(bucketName,filedir+fileName,inputStream,objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase("jpeg") ||
                FilenameExtension.equalsIgnoreCase("jpg") ||
                FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") ||
                FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") ||
                FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }

    public String getUrl(String key) {
        /** 设置URL过期时间为10年  3600l* 1000*24*365*10*/
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        if (url != null) {
            String imgName = url.toString();
            int index = imgName.indexOf("?");
            imgName = imgName.substring(0, index);
            return imgName;
        }
        return null;
    }

    public void delete(String key) {
        ossClient.deleteObject(bucketName, key);
    }

}