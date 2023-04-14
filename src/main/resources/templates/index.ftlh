<!DOCTYPE html>
<html lang="cn">
<head>
    <title>vue-table-list</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <style>
        table,
        td,
        th {
            text-align: center;
            border: 1px solid #000;
            border-collapse: collapse;
        }

        table td {
            padding: 10px 30px;
        }

    </style>
</head>
<body>
<table>
    <tr>
        <td>[Post]新增:add</td>
        <td>[Post]编辑:edit</td>
        <td>[Get]删除:delete/?</td>
        <td>[Get]单条查询:query/?</td>
        <td>[Post]列表查询:query/list</td>
    </tr>
    <tr>
        <td>[Post]批量新增:batch/add</td>
        <td>[Post]批量删除:delete</td>
        <td>[Post]Excel导入:excel/import</td>
        <td>[Get]Excel导出:excel/export</td>
        <td>[Get]大数据Excel导出:/excel/bigdata/export</td>
    </tr>
</table>
<br>
<table>
    <tr>
        <th>预览代码</th>
        <th>表名</th>
        <th>通用接口地址前缀</th>
        <th>下载</th>
    </tr>
    <#list dbInfos as dbInfo>
        <tr>
            <td><a href=${dbInfo.tableName}>${dbInfo.tableComment}</a></td>
            <td>${dbInfo.tableName}</td>
            <td>${dbInfo.tableName?substring(1)?replace("_","/")?lowerCase}</td>
            <td><a href=download/${dbInfo.tableName}>download</a></td>
        </tr>
    </#list>
</table>

</body>
</html>