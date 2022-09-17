package com.langong.emcservice.domain;

import com.langong.emcservice.util.StringUtil;
import lombok.Data;

import java.io.Serializable;


@Data
public class ColumnInfo implements Serializable {

    private String columnName;

    private String columnComment;
    private String columnType;

    public String getColumnName() {
        return StringUtil.toCamelCase(columnName);
    }
}
