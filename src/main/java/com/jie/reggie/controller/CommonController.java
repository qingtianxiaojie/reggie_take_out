package com.jie.reggie.controller;

import com.jie.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用控制类
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传图片
     * @param file 文件
     * @return 返回文件名称
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //原始文件名转换成不重复文件名(类似于雪花算法id)
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//.jpg
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;//cba.jpg
        //判断当前地址目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()){
            //目录不存在，自动创建目录
            dir.mkdirs();
        }

        try {
            //转存临时文件至地址
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name 文件名称
     * @param response 文件
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream =
                    new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");//设定格式为图片
            //用一个数组接数据
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);// 将数据一个一个读出去
                outputStream.flush();//刷新
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
