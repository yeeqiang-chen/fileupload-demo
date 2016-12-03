package com.springapp.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Title:
 * Description:
 * Create Time: 2016/11/26 0026 15:57
 *
 * @author: YEEQiang
 * @version: 1.0
 */
public interface IFileUploadService {
    /**
     * 文件上传
     * @param request
     * @param response
     */
    void fileUpload(HttpServletRequest request, HttpServletResponse response);

    /**
     *分片文件检查
     * @param request
     * @param response
     * @throws IOException
     */
    void checkChunk(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 分片文件合并
     * @param request
     * @param response
     * @throws IOException
     */
    void mergeFile(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
