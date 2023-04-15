package com.langong.service.domain;

import lombok.Data;

@Data
public class TableInfo {
    private String field;
    private String required;
    private String type;
    private String key;
    private String comment;
}
