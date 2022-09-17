package com.langong.emcservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.langong.emcservice.domain.ColumnInfo;
import com.langong.emcservice.domain.SecTableInfo;
import com.langong.emcservice.domain.TableInfo;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SubTableQuery<T> extends BaseMapper<T> {

    @Select("SELECT * FROM ${sub_table}  WHERE ${fk} IN (select id from ${table})")
    List<Map<String, Object>> dynamicSql(@Param("table") String table, @Param("fk") String fk, @Param("sub_table") String sub_table);

    @Select("<script>"
            + "SELECT * FROM ${sub_table} WHERE ${fk} IN "
            + "<foreach item='item' index='index' collection='list'      open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    List<Map<String, Object>> SqlInConditoin(@Param("sub_table") String sub_table, @Param("fk") String fk, @Param("list") List<Serializable> list);

    @Select("select * from ${sub_table} where ${fk}=#{id} limit 1")
    Map<String, Object> SqlQueryOne(@Param("sub_table") String sub_table, @Param("fk") String fk, @Param("id") Serializable id);

    @Insert("<script>"
            + "insert into ${sub_table} <foreach item='item' collection='colnames' open='(' separator=',' close=')'>"
            + "${item} </foreach> values"
            + "<foreach item='v' collection='values' open='(' separator=',' close=')'>#{v} </foreach> </script>")
    boolean insertSubTable(@Param("sub_table") String sub_table, @Param("colnames") List<String> colnames, @Param("values") List<?> values);

    @Insert("<script>"
            + "insert into ${sub_table} <foreach item='item' collection='colnames' open='(' separator=',' close=')'>"
            + "${item} </foreach> values"
            + "<foreach item='v' collection='values' open='(' separator=',' close=')'>" +
            "<foreach index='key' item='val' collection='v.entrySet()' separator=',' open='(' close=')'>"+
            "'${val}' </foreach>"+
            "</foreach> </script>")
    boolean batchInsertSubTable(@Param("sub_table") String sub_table, @Param("colnames") List<String> colnames, @Param("values") List<?> values);

    @Update("<script>"
            + "update ${sub_table} set "
            + "<foreach index='key' item='val' collection='colnames.entrySet()' separator=','>"
            + "${key}=#{val} </foreach>"
            + "where id=#{id} </script>")
    boolean updateSubTable(@Param("sub_table") String sub_table, @Param("colnames") Map<String, Object> colnames, @Param("id") Serializable id);
    @Select("SELECT A.attname AS field,concat_ws ( '', T.typname, SUBSTRING ( format_type ( A.atttypid, A.atttypmod ) FROM '\\(.*\\)' ) ) AS TYPE," +
            " e.is_nullable AS REQUIRED," +
            " e.is_identity AS KEY," +
            " d.description AS COMMENT " +
            "FROM" +
            " pg_class C," +
            " pg_attribute A," +
            " pg_type T," +
            " pg_description d," +
            " information_schema.COLUMNS e " +
            " WHERE" +
            " C.relname = e.TABLE_NAME " +
            " AND e.table_schema = 'public' " +
            " AND e.COLUMN_NAME = A.attname " +
            " AND A.attnum > 0 " +
            " AND A.attrelid = C.oid " +
            " AND A.atttypid = T.oid " +
            " AND d.objoid = A.attrelid " +
            " AND d.objsubid = A.attnum " +
            " AND A.attname NOT IN ('creator','create_time','update_time','is_delete')" +
            " AND C.relname = '${table_name}'")
    List<TableInfo> getTableInfo(@Param("table_name") String table_name);

    @Select("select * from d_sec_table_info where table_name=#{table_name} and is_show=true")
    List<SecTableInfo> getSecTableInfo(@Param("table_name") String table_name);

    @Select("select column_name,column_comment,column_type  from d_sec_table_info where table_name=#{table_name} and is_show=true and column_name!~'id' and column_name not in('create_time','update_time')")
    List<ColumnInfo> getColumns(@Param("table_name") String table_name);

    @Select("<script>"
            + "select "
            + "<foreach item='item' collection='colnames' separator=','>"
            + "${item.columnName} as \"${item.columnComment}\""
            + "</foreach>"
            + "from ${table_name} "
            + "</script>")
    List<Map<String,Object>> getTableData(@Param("table_name") String table_name,@Param("colnames") List<SecTableInfo> colnames);
}
