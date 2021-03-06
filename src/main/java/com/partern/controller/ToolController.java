package com.partern.controller;

import com.partern.responsebo.responseenitiy.ResponseEntity;
import com.partern.utils.Log;
import com.partern.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestMapping("/tool")
@RestController
@Slf4j
public class ToolController {

    @Value("${head_img.dir}")
    String uploadDir;

    @Value("${head_img.file_prefix}")
    String uploadPrefix;

    @RequestMapping("/getValidCode")
    public void getValidCode(HttpServletResponse response, HttpServletRequest request){
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");

            //生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            //存入会话session
            HttpSession session = request.getSession(true);
            session.setAttribute("_code", verifyCode.toLowerCase());
            //生成图片
            int w = 146, h = 33;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            Log.error( "获取验证码异常：%s"+e.getMessage());
        }
    }

    /**
     *
     * @param picture   上传的文件，目前仅支持单文件上传
     * @param request
     */
    @RequestMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("picture") MultipartFile picture, HttpServletRequest request){


        //获取文件在服务器的储存位置
        String path = request.getServletContext().getRealPath(uploadDir);
        File dir = new File(path);

        log.info("文件保存路径:"+path);
        if(!dir.isDirectory()||!dir.exists()){
            dir.mkdir();
        }

        log.info("上传文件大小:" + picture.getSize());

        //获取原始文件名称(包含格式)
        String originalFileName = picture.getOriginalFilename();
        log.info("原始文件名称：" + originalFileName);

        String type = originalFileName.substring(originalFileName.lastIndexOf('.')+1);
        log.info("文件类型:" + type);

        //获取文件名称，（不包含格式）
        String name = originalFileName.substring(0,originalFileName.lastIndexOf('.'));
        log.info("文件名称:" + name);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(d);
        String fileName = uploadPrefix + date + name + "." + type;
        log.info("新文件名称:" + fileName);
        String filePath = path+"/"+fileName;
        File targetFile = new File(filePath);
        try {
            picture.transferTo(targetFile);
            return ResponseEntity.getPublicSuccessResponse(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.getPublicFailResponse(new Object());
        }
    }
}
