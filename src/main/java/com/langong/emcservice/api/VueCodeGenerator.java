package com.langong.emcservice.api;

import com.langong.emcservice.mapper.SubTableQuery;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/vcg")
public class VueCodeGenerator {

    @Resource
    private SubTableQuery subTableQuery;

    // 注入FreeMarker配置类
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @GetMapping("/{tableName}")
    public String hello(@PathVariable String tableName, Model model) {
        List<?> tableInfos = subTableQuery.getTableInfo(tableName);
        model.addAttribute("tableInfos", tableInfos);
        model.addAttribute("tableName", tableName);
        return "vue";
    }

    @GetMapping("/")
    public String index(Model model) {
        List<?> dbInfos = subTableQuery.getDBInfo();
        model.addAttribute("dbInfos", dbInfos);
        return "index";
    }

    @GetMapping("/download/{tableName}")
    public ResponseEntity<byte[]> download(@PathVariable String tableName) throws IOException, TemplateException {
        List<?> tableInfos = subTableQuery.getTableInfo(tableName);
        // 创建FreeMarker模板对象
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("code.ftlh");

        // 准备数据模型
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableInfos", tableInfos);
        dataModel.put("tableName", tableName);

        // 渲染模板
        String renderedTemplate = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataModel);

        // 将渲染后的模板作为字节数组返回给浏览器
        byte[] fileContentBytes = renderedTemplate.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", tableName+".vue");
        headers.setContentLength(fileContentBytes.length);

        return new ResponseEntity<>(fileContentBytes, headers, HttpStatus.OK);
    }
}
