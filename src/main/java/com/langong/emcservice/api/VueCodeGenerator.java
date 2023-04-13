package com.langong.emcservice.api;

import com.langong.emcservice.mapper.SubTableQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/vcg")
public class VueCodeGenerator {

    @Resource
    private SubTableQuery subTableQuery;

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
}
