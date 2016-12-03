package com.springapp.service.impl;

import com.springapp.service.IFileUploadService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Title:
 * Description:
 * Create Time: 2016/11/26 0026 15:57
 *
 * @author: YEEQiang
 * @version: 1.0
 */
@Service
public class FileUploadServiceImpl implements IFileUploadService {
    // 服务器的目录
    private final String serverPath = "E:/upload/";
    @Override
    public void fileUpload(HttpServletRequest request, HttpServletResponse response) {
        // 1.创建DiskFileItemFactory对象,配置缓存信息
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // 2.创建ServletFileUpload对象
        ServletFileUpload sfu = new ServletFileUpload(factory);

        // 3.设置文件名称的编码格式
        sfu.setHeaderEncoding("UTF-8");

        // 4.开始解析文件
        String fileMd5 = null;
        // 分块索引
        String chunk = null;
        try {
            List<FileItem> itemList = sfu.parseRequest(request);
            // 5.获取文件信息
            for (FileItem item : itemList) {
                // 6.判断是文件还是普通数据
                if (item.isFormField()) {
                    // 普通数据
                    String fieldName = item.getFieldName();
                    if ("desc".equals(fieldName)) {
                        // 获取文件信息
                        String desc = item.getString("UTF-8");
                        System.out.println(desc);
                    }
                    if ("fileMd5".equals(item.getFieldName())) {
                        fileMd5 = item.getString("UTF-8");
                        System.out.println(fileMd5);
                    }

                    if ("chunk".equals(item.getFieldName())) {
                        chunk = item.getString("UTF-8");
                        System.out.println(chunk);
                    }
                } else {
                   /* // 文件
                    String fileName = item.getName();
                    //item.getName();

                    // 获取文件的实际内容
                    InputStream is = item.getInputStream();

                    // 保存文件
                    FileUtils.copyInputStreamToFile(is,new File(serverPath+"/"+fileName));*/

                    // 保存分块文件
                    // 1.创建一个唯一的目录,保存分块文件
                    File file = new File(serverPath+"/"+fileMd5);
                    if (!file.exists()) {
                        // 创建目录
                        file.mkdir();
                    }
                    // 2保存文件
                    File chunkFile = new File(serverPath+"/"+fileMd5+"/"+chunk);
                    FileUtils.copyInputStreamToFile(item.getInputStream(),chunkFile);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkChunk(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 检查当前分块是否上传成功
        String fileMd5 = request.getParameter("fileMd5");
        String chunk = request.getParameter("chunk");
        String chunkSize = request.getParameter("chunkSize");

        // 找到分块文件
        File checkFile = new File(serverPath+"/"+fileMd5+"/"+chunk);
        response.setContentType("text/html;charset=utf-8");
        // 检查文件是否存在,且大小是否一致
        if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            // 上传过
            response.getWriter().write("{\"ifExist\":1}");
        } else {
            // 之前没有上传
            response.getWriter().write("{\"ifExist\":0}");
        }
    }

    @Override
    public void mergeFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileMd5 = request.getParameter("fileMd5");
        String fileName = request.getParameter("fileName");
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        File file = new File(serverPath+"/"+fileMd5);
        // 1.找到目录的所有分块文件
        File[] fileArray = file.listFiles(new FileFilter() {
            // 删除目录,只要文件
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }
                return true;
            }
        });
        // 转成集合,便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });
        File outputFile = new File(serverPath+"/"+UUID.randomUUID().toString()+fileSuffix);
        // 创建文件
        outputFile.createNewFile();
        // 输出流
        FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
        // 2.合并
        FileChannel inChannel;
        for (File f : fileList) {
            inChannel = new FileInputStream(f).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            // 删除分片
            f.delete();
        }
        // 3.删除文件夹
        File tempFile = new File(serverPath+"/"+fileMd5);
        if (tempFile.isDirectory() && tempFile.exists()) {
            tempFile.delete();
        }
    }
}
