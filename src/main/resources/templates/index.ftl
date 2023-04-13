<#function dashedToCamel(s)>
    <#return s
    ?replace('(^_+)|(_+$)', '', 'r')
    ?replace('\\_+(\\w)?', ' $1', 'r')
    ?replace('([A-Z])', ' $1', 'r')
    ?capitalize
    ?replace(' ' , '')
    ?uncapFirst
    >
</#function>
<!DOCTYPE html>
<html lang="cn">
<head>
    <title>vue-table-list</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <style>

    </style>
</head>
<body>
<script>
    var url = window.location.origin + "/";
</script>
<#list dbInfos as dbInfo>
    <a href=url+${dbInfo.tableName}>${dbInfo.tableComment}</a>
    <br>
</#list>
</body>
</html>