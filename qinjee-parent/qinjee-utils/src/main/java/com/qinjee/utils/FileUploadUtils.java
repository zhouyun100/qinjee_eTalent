/**
 * 文件名：FileUploadUtils
 * 工程名称：masterdata-v1.0-20191021
 * <p>
 * qinjee
 * 创建日期：2019/12/11
 * <p>
 * Copyright(C) 2019, by zhouyun
 * 原始作者：周赟
 */
package com.qinjee.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Properties;

/**
 * 腾讯云服务器SSH远程连接上传文件
 * @author 周赟
 * @date 2019/12/11
 */
public class FileUploadUtils {

    /**
     * 以下常量限开发使用，后续待封装，业务调用可忽略
     */
    private static String DOMAIN_NAME = "http://193.112.188.180/file/";
    private static String REMOTE_FILE_PATH = "/home/server/file/";
    private static String HOSTNAME = "193.112.188.180";
    private static Integer PORT = 22;
    private static String USERNAME = "dev_upload";
    private static String PASSWORD = "qinjee2019";

    public static String COMPANY_LOGO_DIRECTORY = "company/logo/";


    /**
     * 上传文件
     * @param uploadFile 文件
     * @param relativeDirectory 服务器相对目录(必须以/斜杠结尾)
     * @param fileName 文件名(即文件前缀，无需包含文件类型)
     *                 如果文件名为空，默认读取uploadFile文件名
     * @return
     */
    public static String uploadFile(File uploadFile, String relativeDirectory, String fileName){
        String path = null;

        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;

        try{

            //建立SSH连接，非FTP连接
            JSch jsch = new JSch();
            jsch.getSession(USERNAME, HOSTNAME, PORT);
            sshSession = jsch.getSession(USERNAME, HOSTNAME, PORT);
            sshSession.setPassword(PASSWORD);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;

            /**
             * 文件名(即文件前缀，无需包含文件类型)
             * 如果文件名为空，默认读取uploadFile文件名
             */
            String uploadFileName = uploadFile.getName();
            String suffixName = uploadFileName.substring(uploadFileName.lastIndexOf("."));
            if(StringUtils.isBlank(fileName)){
                fileName = uploadFileName;
            }else{
                fileName += suffixName;
            }
            InputStream input = new FileInputStream(uploadFile);

            //拼接最终需要上传至服务器的完整文件路径
            String dst = REMOTE_FILE_PATH + relativeDirectory + fileName;

            //文件上传
            sftp.put(input, dst);

            //返回文件可访问的URL
            path = DOMAIN_NAME + relativeDirectory + fileName;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }
        return path;
    }

    /**
     * MultipartFile 转 File
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile( @RequestParam MultipartFile file ) throws Exception {
        File toFile = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    /**
     * File 转 MultipartFile
     * @param file
     * @throws Exception
     */
    public static MultipartFile fileToMultipartFile( File file ) throws Exception {

        FileInputStream fileInput = new FileInputStream(file);
        MultipartFile toMultipartFile = new MockMultipartFile ("file",file.getName(),"text/plain",
                IOUtils.toByteArray(fileInput));
        toMultipartFile.getInputStream();
        return toMultipartFile;
    }

    /**
     * 将流写到文件
     * @param ins
     * @param file
     */
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream (file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭channel连接
     * @param channel
     */
    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    /**
     * 关闭Session连接
     * @param session
     */
    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static void main(String [] args) throws Exception{
        File uploadFile = new File("E:\\108x108.png");
        String path = uploadFile(uploadFile, COMPANY_LOGO_DIRECTORY,"1");
        System.out.println("path：" + path);
    }
}
