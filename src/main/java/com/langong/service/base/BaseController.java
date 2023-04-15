package com.langong.service.base;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.langong.service.annotation.Wrapper;
import com.langong.service.domain.ColumnInfo;
import com.langong.service.domain.SecTableInfo;
import com.langong.service.mapper.SubTableQuery;
import com.langong.service.util.StringUtil;
import com.langong.service.util.WrapperHelp;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static java.lang.System.out;

@Slf4j
public class BaseController<T> {

    private Boolean logicDelete = false;
    @Autowired
    IService<T> iService;

    @Autowired
    SubTableQuery<T> dynamic;

    /**
     * 新增
     * @param t 实体类
     * @return 实体类
     */
    @PostMapping({"/add"})
    public Result<?> add(@RequestBody T t) {
        try {
            if (t != null) {
                this.iService.saveOrUpdate(t);
                return Result.ok(t);
            } else {
                return Result.error("新增对象不能为空");
            }
        } catch (DuplicateKeyException e) {
            return Result.error("新增失败，该记录已存在");
        } catch (Exception var3) {
            log.error("新增错误:" + var3.getMessage());
            return Result.error(var3.getMessage());
        }
    }

    /**
     * 批量新增
     * @param t 实体类数组
     * @return 结果
     */
    @PostMapping({"batch/add"})
    public Result<?> batchAdd(@RequestBody Collection<T> t) {
        if (t != null) {
            return Result.ok(this.iService.saveOrUpdateBatch(t));
        } else {
            return Result.error("新增对象不能为空");
        }
    }

    /**
     * 删除
     * @param id id
     * @return 结果
     */
    @GetMapping({"/delete/{id}"})
    public Result<?> delete(@PathVariable Serializable id) {
        if (logicDelete) {
            return Result.ok(this.iService.update(new UpdateWrapper<T>().eq("id", id).set("is_delete", true)));
        } else {
            return Result.ok(this.iService.removeById(id));
        }
    }

    /**
     * 批量删除
     * @param ids id数组
     * @return 结果
     */
    @PostMapping({"/delete"})
    public Result<?> batchDelete(@RequestBody List<Serializable> ids) {
        if (logicDelete) {
            return Result.ok(this.iService.update(new UpdateWrapper<T>().in("id", ids).set("is_delete", true)));
        } else {
            return Result.ok(this.iService.removeByIds(ids));
        }
    }

    /**
     * 详情
     * @param id id
     * @param column 列
     * @param t 实体类
     * @return 结果
     * @throws NoSuchFieldException 异常
     */
    @GetMapping({"/query/{id}","/query/{id}/{column}"})
    public Result<?> query(@PathVariable String id, @PathVariable(required = false) boolean column, T t) throws NoSuchFieldException {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        Class<?> clzz = t.getClass();
        Field fid = clzz.getDeclaredField("id");
        out.println(fid.getType().getTypeName());
        if (fid.getType().getTypeName().equals("java.lang.Integer")) {
            int iid = Integer.parseInt(id);
            queryWrapper.eq("id", iid);
        } else {
            queryWrapper.eq("id", id);
        }
        var res = this.iService.getOne(queryWrapper);
        one2one(res);
        if (column){
            TableName tn = clzz.getAnnotation(TableName.class);
            String tableName = tn.value();
            List<ColumnInfo> columns=dynamic.getColumns(tableName);
            return Result.ok(res,columns);
        }
        return Result.ok(res);
    }

    /**
     * 编辑
     * @param t 实体类
     * @return 结果
     */
    @PostMapping({"/edit"})
    public Result<?> edit(@RequestBody T t) {
        if (t != null) {
            return Result.ok(this.iService.updateById(t));
        } else {
            return Result.error("更新对象不能为空");
        }
    }

    /**
     * 列表分页查询
     * @param request 请求查询条件
     * @return 结果
     */
    @PostMapping({"/query/list"})
    public Result<?> queryO2o(HttpServletRequest request) throws IOException, IllegalAccessException {
        StringBuilder data = new StringBuilder();
        String line;
        BufferedReader reader;
        //获取request分页请求参数
        reader = request.getReader();
        while (null != (line = reader.readLine())) {
            data.append(line);
        }
        reader.close();
        T t = JSONUtil.toBean(data.toString(), this.iService.getEntityClass());
        QueryWrapper<T> queryWrapper = WrapperHelp.getWrapper(t);
        if (logicDelete) {
            queryWrapper.eq("is_delete", false);
        }

        PageData pageData = JSONUtil.toBean(data.toString(), PageData.class);

        Page<T> resPage = this.iService.page(new Page<>(pageData.getCurrent(), pageData.getLimit()), queryWrapper);
        one2one(resPage);
        return Result.ok(resPage);
    }

    /**
     * 导出excel
     */
    @GetMapping({"/excel/export"})
    public void exportExcel(T t, HttpServletResponse response) throws IOException {
        Class<?> cls = t.getClass();
        TableName tn = cls.getAnnotation(TableName.class);
        String tableName = tn.value();
        String fileName = tableName + ".xlsx";
        List<SecTableInfo> list = dynamic.getSecTableInfo(tableName);
        List<?> data = dynamic.getTableData(tableName, list);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.renameSheet(tableName);
        writer.write(data, true);
        writer.autoSizeColumnAll();
        OutputStream out = response.getOutputStream();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }

    /**
     * 大数据表导出excel
     */
    @GetMapping({"/excel/bigdata/export"})
    public void exportBigDataExcel(T t, HttpServletResponse response) throws IOException {
        Class<?> cls = t.getClass();
        TableName tn = cls.getAnnotation(TableName.class);
        String tableName = tn.value();
        String fileName = tableName + ".xlsx";
        List<SecTableInfo> list = dynamic.getSecTableInfo(tableName);
        List<?> data = dynamic.getTableData(tableName, list);
        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.renameSheet(tableName);
        writer.write(data, true);
        writer.autoSizeColumnAll();
        OutputStream out = response.getOutputStream();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }

    /**
     * 导入excel
     * @param file excel文件
     * @return 结果
     */
    @PostMapping({"/excel/import"})
    public Result<?> importExcel(T t, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("请选择文件上传");
        }
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        Class<?> cls = t.getClass();
        TableName tn = cls.getAnnotation(TableName.class);
        String tableName = tn.value();
        List<SecTableInfo> list = dynamic.getSecTableInfo(tableName);
        Map<String, String> map = new HashMap<>();
        list.forEach(tableInf -> map.put(tableInf.getColumnComment(), tableInf.getColumnName()));
        reader.setHeaderAlias(map);
        Collection<?> importList = reader.readAll(t.getClass());

        reader.close();
        IoUtil.close(file.getInputStream());
        return Result.ok(iService.saveOrUpdateBatch((Collection<T>) importList));
    }


    /**
     * 反射获取字表中数据
     */
    public void one2one(Page<T> tPage) {
        Class<?> cls = this.iService.getEntityClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if ("serialVersionUID".equals(f.getName())) {
                continue;
            }
            Wrapper w = f.getAnnotation(Wrapper.class);
            if (w != null && w.type() == Wrapper.Type.ONE2ONE) {
                String subTableName = w.SubTableName();
                if (subTableName.equals("")) {
                    subTableName = "t_" + StringUtil.toUnderlineCase(f.getName());
                }

                //获取ids值List
                var ids = getIds(tPage, w.PK());
                if (ids.size() > 0) {
                    var list = dynamic.SqlInCondition(subTableName, StringUtil.toUnderlineCase(w.FK()), ids,w.OD());
                    Map<Serializable, Map<String, Object>> subMap = new HashMap<>();
                    String FkEntity = StringUtil.toCamelCase(w.FK());
                    for (var l : list
                    ) {
                        subMap.put((Serializable) l.get(FkEntity), l);
                    }
                    //拼接赋值
                    setSubTableValue(tPage, subMap, f.getName(), w.PK());
                }

            } else if (w != null && w.type() == Wrapper.Type.ONE2MANY) {
                String subTableName = w.SubTableName();
                //获取ids值List
                var ids = getIds(tPage, w.PK());
                if (ids.size() > 0) {
                    var list = dynamic.SqlInCondition(subTableName, StringUtil.toUnderlineCase(w.FK()), ids,w.OD());
                    Map<Serializable, List<Map<String, Object>>> subListMap = new HashMap<>();
                    String FkEntity = StringUtil.toCamelCase(w.FK());
                    for (var l : list
                    ) {
                        if (subListMap.containsKey((Serializable) l.get(FkEntity))) {
                            subListMap.get((Serializable) l.get(FkEntity)).add(l);
                        } else {
                            List<Map<String, Object>> listMap = new ArrayList<>();
                            listMap.add(l);
                            subListMap.put((Serializable) l.get(FkEntity), listMap);
                        }
                    }
                    //拼接赋值
                    setSubTableValue(tPage, subListMap, f.getName(), w.PK());
                }
            }
        }
    }

    /**
     * single entity map
     */
    public void one2one(T t) {
        Class<?> cls = t.getClass();
        Field[] fields = cls.getDeclaredFields();

        for (Field f : fields) {
            f.setAccessible(true);
            if ("serialVersionUID".equals(f.getName())) {
                continue;
            }
            //获取注解
            Wrapper w = f.getAnnotation(Wrapper.class);
            if (w != null && w.type() == Wrapper.Type.ONE2ONE) {
                String subTableName = w.SubTableName();
                if (subTableName.equals("")) {
                    subTableName = "t_" + StringUtil.toUnderlineCase(f.getName());
                }
                //获取ids值List
                var subMap = dynamic.SqlQueryOne(subTableName, StringUtil.toUnderlineCase(w.FK()), (Serializable) ReflectUtil.getFieldValue(t, w.PK()));
                //拼接赋值
                ReflectUtil.setFieldValue(t, f.getName(), subMap);

            } else if (w != null && w.type() == Wrapper.Type.ONE2MANY) {
                String subTableName = w.SubTableName();
                List<Serializable> ids = new ArrayList<>();
                ids.add((Serializable) ReflectUtil.getFieldValue(t, w.PK()));
                var subList = dynamic.SqlInCondition(subTableName, StringUtil.toUnderlineCase(w.FK()), ids,w.OD());
                //拼接赋值
                ReflectUtil.setFieldValue(t, f.getName(), subList);
            }
        }
    }


    /**
     * 获取ids
     */
    private List<Serializable> getIds(Page<T> tPage, String pk) {
        List<Serializable> ids = new ArrayList<>();
        for (var record : tPage.getRecords()
        ) {
            ids.add((Serializable) ReflectUtil.getFieldValue(record, pk));
        }
        return ids;
    }

    /**
     * setSubTableValue
     */
    private void setSubTableValue(Page<T> tPage, Map<Serializable, ?> subMap, String colName, String pk) {
        for (var record : tPage.getRecords()
        ) {
            ReflectUtil.setFieldValue(record, colName, subMap.get((Serializable) ReflectUtil.getFieldValue(record, pk)));
        }
    }

    public void setLogicDelete(Boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

}
