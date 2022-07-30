package com.langong.emcservice.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.langong.emcservice.base.PageData;
import lombok.Data;

import java.io.Serializable;

/**
* 导出字段表
* @TableName d_sec_table_info
*/
@TableName(value ="d_sec_table_info")
@Data
public class SecTableInfo extends PageData implements Serializable {

    /**
    * id
    */
    @TableId
    private String id;

    /**
    * 表名
    */
    private String tableName;

    /**
    * 表注释
    */
    private String tableComment;

    /**
    * 列名
    */
    private String columnName;

    /**
    * 列注释
    */
    private String columnComment;

    /**
    * 是否显示
    */
    private Boolean isShow;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
