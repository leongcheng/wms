package com.gr.utils;

import com.gr.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author liangc
 * @data 2019/8/20 13:52
 */
@Slf4j
public class IOUtil {

    /**
     * pdf下载文件
     * @param response
     * @param data
     */
    public static void download(HttpServletResponse response, byte[] data, String fileName) {
        try (ServletOutputStream out = response.getOutputStream()){
            if(fileName == null){
                fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf";
            }
            response.reset();
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Content-Length", "" + data.length);

            IOUtils.write(data, out);

        } catch (IOException e) {
            log.error("pdf文件下载失败{}",e.getMessage());
        }
    }

    /**
     * Excel下载文件
     * @param response
     * @param file
     */
    public static void export(File file, String fileName, HttpServletRequest request, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(file);

            response.reset();
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((setFileDownloadHeader(request, fileName) + ".pdf").getBytes("UTF8"), "ISO-8859-1"));
            response.addHeader("Content-Length", String.valueOf(fileInputStream.available()));
            response.flushBuffer();

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fileInputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, len);
            }

        } catch (Throwable e) {
            log.error("export文件下载失败{}", e.getMessage());
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件，进行压缩zip
     * @param response
     * @param files 文件转换成的byte字节流
     * @throws Exception
     */
    public static void outputZip(HttpServletResponse response, HttpServletRequest request, Map<String, byte[]> files, String zipName){
        ZipOutputStream zos = null;
        BufferedOutputStream bos = null;
        try{
            response.reset();
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ new String((setFileDownloadHeader(request, zipName) + ".zip").getBytes("UTF8"), "ISO-8859-1"));

            zos = new ZipOutputStream(response.getOutputStream());
            bos = new BufferedOutputStream(zos);

            for(Map.Entry<String, byte[]> entry : files.entrySet()){
                String fileName = entry.getKey(); //每个zip文件名
                byte[] file = entry.getValue();  //这个zip文件的字节

                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file));
                zos.putNextEntry(new ZipEntry(fileName));

                int len = 0;
                byte[] buf = new byte[10 * 1024];
                while((len = bis.read(buf, 0, buf.length)) != -1){
                    bos.write(buf, 0, len);
                }
                bis.close();
                bos.flush();
            }
        }catch(Exception e){
            log.error("zip文件下载失败{}", e.getMessage());
        }finally {
            try {
                if(zos != null){
                    zos.close();
                }
                if(bos != null){
                    bos.close();
                }
            } catch (IOException e) {
                log.error("文件流释放失败{}", e.getMessage());
            }
        }
    }


    public static void zipFiles(List<Workbook> arrayList, File zipfile, String fileName) {
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < arrayList.size(); i++) {
                ZipEntry entry = new ZipEntry(fileName + i + ".xls");
                out.putNextEntry(entry);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                arrayList.get(i).write(bos);
                bos.writeTo(out);
                out.closeEntry();
                bos.close();
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载Excel文件
     * @param response
     * @param workbook
     */
    public static void excel(HttpServletResponse response, HttpServletRequest request, Workbook workbook, String fileName) {
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();

            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((setFileDownloadHeader(request, fileName) + ".xls").getBytes("UTF8"), "ISO-8859-1"));
            response.setContentType("application/octet-stream; charset=UTF-8");

            workbook.write(outputStream);

        } catch (IOException e) {
            log.error("Excel导出失败{}",e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("Excel文件流释放异常{}", e.getMessage());
            }
        }
    }


    /**
     * 错误
     * @param response
     */
    public static void error(HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script type='text/javascript'>alert('下载失败，请稍后重试！')</script>");
            response.getWriter().flush();
            response.getWriter().close();

        } catch (IOException e) {
            log.error("下载输出报错信息失败{}", e.getMessage());
        }
    }

    /**
     * 文件预览
     *
     * @param response
     * @param data
     */
    public static void overview(HttpServletResponse response, byte[] data) {
        response.setContentType("image/pdf");
        response.setCharacterEncoding("utf-8");
        try (OutputStream os = response.getOutputStream()) {
            os.write(data);
            os.flush();
        } catch (Exception e) {
            log.error("文件预览失败{}", e.getMessage());
        }
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * Excel格式效验
     * @param file
     */
    public static void fileTypeVerify(MultipartFile file) {
        if(file.isEmpty()){
            throw new RRException("请选择文件上传！");
        }
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.toLowerCase().matches("^.+\\.(xlsx|xls)$")) {
            throw new RRException("文件格式错误");
        }
    }

}
