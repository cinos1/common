package com.langong.emcservice.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PageData  {

    @TableField(exist = false)
    @JsonProperty("current")
    private int current=1;
    @TableField(exist = false)
    @JsonProperty("limit")
    private int limit=50;
}

