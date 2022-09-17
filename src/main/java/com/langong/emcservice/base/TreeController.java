package com.langong.emcservice.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.langong.emcservice.util.CategoryTreeNode;
import com.langong.emcservice.util.StringUtil;
import com.langong.emcservice.util.TreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class TreeController<T extends CategoryTreeNode> extends BaseController<T> {
    @GetMapping({"/tree"})
    public Result<?> queryTree(@RequestParam("projectId") String projectId) {
        try {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            if(!Objects.equals(projectId, "")){
                queryWrapper.eq("project_id",projectId);
            }
            List<? extends CategoryTreeNode> list = this.iService.list(queryWrapper);
            List<CategoryTreeNode> ctnList = new ArrayList<>(list);
            List<CategoryTreeNode> resultList = TreeUtil.build(ctnList); //进行封装
            return Result.ok(resultList);

        } catch (Exception var3) {
            log.error("查询树,错误{}", var3.getMessage());
            return Result.error("查询树失败");
        }
    }

    @PostMapping({"/tree"})
    @SuppressWarnings("unchecked")
    public Result<?> postTree(@RequestBody Map<String,Object> queryMap) {
        try {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            for(String key:queryMap.keySet()){
                if(queryMap.get(key)!=null || queryMap.get(key)!=""){
                    String columnName= StringUtil.toUnderlineCase(key);
                    queryWrapper.eq(columnName,queryMap.get(key));
                }
            }

            List<?> list = this.iService.list(queryWrapper);
            List<CategoryTreeNode> resultList = TreeUtil.build((List<CategoryTreeNode>) list); //进行封装
            return Result.ok(resultList);

        } catch (Exception var3) {
            log.error("查询树,错误{}", var3.getMessage());
            return Result.error("查询树失败");
        }
    }

}
