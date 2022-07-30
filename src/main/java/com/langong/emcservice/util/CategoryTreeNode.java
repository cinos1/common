package com.langong.emcservice.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.langong.emcservice.base.PageData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeNode extends PageData {
    private String id;
    private String parentId;
    @TableField(exist = false)
    private List<?> children = new ArrayList<>();

}
