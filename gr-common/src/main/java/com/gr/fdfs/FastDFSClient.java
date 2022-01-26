package com.gr.fdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.gr.exception.ResultEnum;
import com.gr.exception.ResultException;
import com.gr.annotation.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author liangc
 * @date 2019/8/16 16:31
 */
@Slf4j
@Component
public class FastDFSClient
{
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UploadProperties prop;
    @Value("${fast.upload.baseUrl}")
    private String baseUrl;

    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException
     */
    @RedissonLock(lockKey = "fdfs")
    public String uploadFile(MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        //文件后缀格式判断
        if (!originalFilename.toLowerCase().matches("^.+\\.(png|jpg|gif|bmp|jpeg|webp|jfif|pdf|text|xlsx|xls|ppt|pptx|doc|docx|zip|rar|rar4|mp4|avi|flv)$")) {
            log.error("文件格式错误");
            throw new ResultException(ResultEnum.ERR021);
        }
        //恶意软件判断
        if(originalFilename.matches("^.+\\.(png|jpg|gif|bmp|jpeg|webp|jfif)$")){
            //对图片文件格式进行校验
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)) {
                throw new ResultException(ResultEnum.ERR021);
            }
            //检验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                log.error("【文件上传】上传文件格式错误");
                throw new ResultException(ResultEnum.ERR021);
            }
        }
        StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()),null);

        return getResAccessUrl(storePath);
    }

    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException
     */
    @RedissonLock(lockKey = "fdfs")
    public String uploadFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {

            StorePath storePath = storageClient.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);

            return getResAccessUrl(storePath);

        } catch (Exception e) {
            log.error("文件上传失败{}", e.getMessage());
        }
        throw new ResultException(ResultEnum.ERR021);
    }

    /**
     *
     * @param buff
     * @param fileExtension
     * @return
     */
    @RedissonLock(lockKey = "fdfs")
    public String uploadFile(byte[] buff, String fileExtension) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buff)){

            StorePath storePath = storageClient.uploadFile(inputStream, buff.length, fileExtension, null);

            return getResAccessUrl(storePath);

        } catch (Exception e) {
            log.error("文件上传失败{}", e.getMessage());
        }
        throw new ResultException(ResultEnum.ERR021);
    }

    /**
     * 上传文件
     * @return 文件访问地址
     * @throws IOException
     */
    @RedissonLock(lockKey = "fdfs")
    public String uploadFile(ByteArrayOutputStream bos, String fileName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bos.toByteArray())){

            StorePath storePath = storageClient.uploadFile(inputStream, inputStream.available(), FilenameUtils.getExtension(fileName), null);

            return getResAccessUrl(storePath);

        } catch (Exception e) {
            log.error("文件上传FastDFS服务器失败{}",e.getMessage());
        }
        throw new ResultException(ResultEnum.ERR021);
    }

    /**
     * 将一段字符串生成一个文件上传
     * @param content 文件内容
     * @param fileExtension
     * @return
     */
    @RedissonLock(lockKey = "fdfs")
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buff)){
            StorePath storePath = storageClient.uploadFile(inputStream, buff.length, fileExtension,null);

            return getResAccessUrl(storePath);

        } catch (Exception e) {
            log.error("文件上传失败{}", e.getMessage());
        }
        throw new ResultException(ResultEnum.ERR021);
    }

    // 封装图片完整URL地址
    private String getResAccessUrl(StorePath storePath) {

        return StringUtils.isNotBlank(storePath.getFullPath()) ? baseUrl + storePath.getFullPath() : null;
    }

    /**
     * 下载文件
     * @param fileUrl 文件URL
     * @return 文件字节
     * @throws IOException
     */
    public byte[] downloadFile(String fileUrl) {
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), downloadByteArray);
    }

    /**
     * 文件下载
     * @param path 文件路径，例如：/group1/path=M00/00/00/itstyle.png
     * @param filename 下载的文件命名
     * @return
     */
    public void download(String path, String filename, HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()){
            // 获取文件
            StorePath storePath = StorePath.parseFromUrl(path);
            if (StringUtils.isBlank(filename)) {
                filename = FilenameUtils.getName(storePath.getPath());
            }
            byte[] bytes = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
            response.reset();
            response.setContentType("applicatoin/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

            out.write(bytes);
        } catch (IOException e) {
            log.error("文件下载失败{}", e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param fileUrl 文件访问地址
     * @return
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            log.error("删除文件{}", e.getMessage());
        }
    }


    /**
     * 获取文件后缀名
     * @param fileName
     * @return 如："jpg"、"txt"、"zip" 等
     */
    public static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }

}
