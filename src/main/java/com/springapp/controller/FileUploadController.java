package com.springapp.controller;

import com.springapp.service.IFileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Title:
 * Description:
 * Create Time: 2016/11/26 0026 15:52
 *
 * @author: YEEQiang
 * @version: 1.0
 */
@Controller
public class FileUploadController {
    @Autowired
    private IFileUploadService fileUploadService;

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public ModelAndView fileUploadComponent() {
//        ModelAndView mv = new ModelAndView("fileUpload");
        ModelAndView mv = new ModelAndView("webuploader");
        return mv;
    }

    /**
     * 文件上传
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        this.fileUploadService.fileUpload(request,response);
    }

    /**
     * 检查分片
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "checkChunk", method = RequestMethod.POST)
    public void checkChunk(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.fileUploadService.checkChunk(request, response);
    }

    /**
     * 合并分片
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "mergeChunkFile", method = RequestMethod.POST)
    public void mergeChunkFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.fileUploadService.mergeFile(request, response);
    }
}
