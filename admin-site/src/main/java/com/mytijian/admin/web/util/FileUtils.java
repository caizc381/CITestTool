package com.mytijian.admin.web.util;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.aliyun.oss.OSSClient;

public class FileUtils {

	private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	@Value("${aliyun.oss.endpoint}")
	private static String endpoint;
	
	@Value("${aliyun.oss.bucket}")
	private static String bucket;
	
	@Value("${aliyun.oss.accessKeyId}")
	private static String accessKeyId;
	
	@Value("${aliyun.oss.accessKeySecret}")
	private static String accessKeySecret;
	
	public static void uploadFile(List<File> fileList, String tmpFileDir){
		try{
			OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
			// 写入文件内容
			for(File file : fileList){
				ossClient.putObject(bucket, tmpFileDir + file.getName(), file);
			}
			ossClient.shutdown();
			logger.info("文件上传成功");
		} catch (Exception ex){
			logger.error("文件上传失败", ex);
		}
	}
}
