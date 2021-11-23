package com.kennycason.kumo.provider.controller;

import cn.hutool.core.io.FileUtil;
import com.kennycason.kumo.provider.service.CircleChineseWordCloudService;
import com.kennycason.kumo.provider.vo.CircleWordCloudRequestVo;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo.provider.controller
 * date:2021/11/19
 */
@RestController
@AllArgsConstructor
public class CircleChineseWordCloud {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircleChineseWordCloud.class);
    private final CircleChineseWordCloudService circleChineseWordCloudService;

    @SneakyThrows
    @PostMapping("/worldCloud/gene")
    public String convertWordCloud(@RequestBody CircleWordCloudRequestVo wordCloudRequestVo) {
        final String path = circleChineseWordCloudService.convertToWorldCloud(wordCloudRequestVo);
        return FileUtil.getName(path);
    }

    @SneakyThrows
    @GetMapping("/worldCloud/get")
    public void convertWordCloud(@RequestParam(value = "key", required = true) String key, HttpServletResponse response) {
        final String path = FileUtil.getTmpDirPath() + key;
        final String fileName = FileUtil.getName(path);
        response.setContentType("application/force-download");
        response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
        try (FileInputStream inputStream = new FileInputStream(path);
             ServletOutputStream outputStream = response.getOutputStream();) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getLocalizedMessage());
        }
        FileUtil.del(path);
    }
}
