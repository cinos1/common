package com.langong.service.util;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeNode {
    private String id;
    private String parentId;
    @TableField(exist = false)
    private List<?> children = new ArrayList<>();

}
