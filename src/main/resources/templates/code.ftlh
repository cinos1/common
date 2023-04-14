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
<template>
    <div class="app-container">
        <!--工具栏-->
        <div class="head-container">
            <!-- 搜索 -->
            <!-- 增删查改-->
            <div class="tool-container">
                <div>
                    <el-button @click="queryRows" size="medium" type="primary">查找</el-button>
                </div>
                <div>
                    <el-button @click="addRow" size="medium" type="primary">新增</el-button>
                    <el-button @click="deleteRows" size="medium" type="primary">删除</el-button>
                </div>
            </div>
        </div>
        <!--表单组件-->
        <el-dialog :close-on-click-modal="false" :show-close="false" :visible="show_form" custom-class="gen-dialog"
                   title="详情" width="500px">
            <el-form :model="form" :rules="rules" inline label-width="140px" ref="form" size="small">
                <#list tableInfos as tableInfo>
                    <#if tableInfo.field!="id" || tableInfo.field!="project_id">
                        <el-form-item label="${tableInfo.comment}" prop="${dashedToCamel(tableInfo.field)}">
                            <#if tableInfo.type?contains("char") >
                                <el-input clearable v-model.trim="form.${dashedToCamel(tableInfo.field)}" type="text"
                                          maxlength="${tableInfo.type?replace("\\D+","","r")}" show-word-limit
                                          placeholder="请输入${tableInfo.comment}"/>
                            <#elseIf tableInfo.type?startsWith("text") || tableInfo.type?startsWith("longtext") >
                                <el-input v-model.trim="form.${dashedToCamel(tableInfo.field)}" type="textarea"
                                          :autosize="{minRows: 2, maxRows: 5}"
                                          placeholder="请输入${tableInfo.comment}"/>
                            <#elseIf tableInfo.type?startsWith("int")>
                                <el-input :maxlength="50" clearable
                                          v-model.trim="form.${dashedToCamel(tableInfo.field)}" type="number"
                                          placeholder="请输入${tableInfo.comment}"/>
                            <#elseIf tableInfo.type?startsWith("float") || tableInfo.type?startsWith("double") || tableInfo.type?startsWith("decimal")>
                                <el-input :maxlength="50" clearable
                                          v-model.trim="form.${dashedToCamel(tableInfo.field)}" type="number"
                                          :step="0.01" placeholder="请输入${tableInfo.comment}"/>
                            <#elseIf tableInfo.type?startsWith("date") || tableInfo.type?startsWith("datetime") || tableInfo.type?startsWith("timestamp")>
                                <el-date-picker v-model="form.${dashedToCamel(tableInfo.field)}" type="date"
                                                placeholder="请选择${tableInfo.comment}"/>
                            <#elseIf tableInfo.type?startsWith("bool")>
                                <el-switch v-model.trim="form.${dashedToCamel(tableInfo.field)}" active-value="true"
                                           inactive-value="false" :active-text="'是'" :inactive-text="'否'"/>
                            <#else>
                                <el-input :maxlength="50" clearable
                                          v-model.trim="form.${dashedToCamel(tableInfo.field)}" type="text"
                                          placeholder="请输入${tableInfo.comment}"/>
                            </#if>
                        </el-form-item>
                    </#if>
                </#list>
            </el-form>
            <div class="dialog-footer" slot="footer">
                <el-button @click="cancelCU" type="text">取消</el-button>
                <el-button :loading="form_loading" @click="submitCU" type="primary">确认</el-button>
            </div>
        </el-dialog>
        <!--表格渲染-->
        <el-table :data="data" @selection-change="handleSelectionChange" class="gen-table" ref="table" size="small"
                  style="width: 100%;" v-loading="loading" height="100%">
            <el-table-column type="selection" width="55"/>
            <#list tableInfos as tableInfo>
                <#if tableInfo.field!="id" || tableInfo.field!="project_id">
                    <el-table-column label="${tableInfo.comment}" prop="${dashedToCamel(tableInfo.field)}" width="100"/>
                </#if>
            </#list>
            <el-table-column align="center" label="操作" width="150px">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.$index, scope.row)" size="mini" type="primary">编辑</el-button>
                    <el-popconfirm @confirm="handleDel(scope.$index, scope.row.id)" title="确认删除?">
                        <el-button size="mini" slot="reference" type="danger">删除</el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>
        <div class="foot-container">
            <!--分页组件-->
            <el-pagination
                    :current-page="queryParam.current"
                    :page-sizes="[10, 20, 30, 50]"
                    :page-size="queryParam.limit"
                    :total="this.total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    background
                    layout="total,sizes,prev, pager, next,jumper"
            >
            </el-pagination>
        </div>
    </div>
</template>
<script>
    export default {
        name: '${tableName}',
        data() {
            return {
                pre_url: "${tableName?substring(1)?replace("_","/")?lowerCase}",
                form_loading: false,
                show_form: false,
                form: {
                    <#list tableInfos as tableInfo>
                    <#if tableInfo.type?startsWith("varchar") || tableInfo.type?startsWith("text")>
                    ${dashedToCamel(tableInfo.field)}: '',
                    <#elseIf tableInfo.type?startsWith("int") >
                    ${dashedToCamel(tableInfo.field)}: 0,
                    <#elseIf tableInfo.type?startsWith("float") || tableInfo.type?startsWith("double") || tableInfo.type?startsWith("decimal")>
                    ${dashedToCamel(tableInfo.field)}: 0.00,
                    <#elseIf tableInfo.type?startsWith("bool")>
                    ${dashedToCamel(tableInfo.field)}: false,
                    <#else>
                    ${dashedToCamel(tableInfo.field)}: null,
                    </#if>
                    </#list>
                },
                rules: {
                    <#list tableInfos as tableInfo>
                    <#if tableInfo.field!="id" && tableInfo.key=="NO" && tableInfo.required=="NO">
                    ${tableInfo.field}: [{required: true, message: '${tableInfo.comment}不能为空', trigger: 'blur'},],
                    </#if>
                    </#list>
                },
                data: [],
                selected: [],
                queryParam: {current: 1, limit: 10},
                total: 0,
                loading: false,
            }
        },
        mounted() {
            this.queryRows();
        },
        methods: {
            resetForm() {
                this.$refs['form'].resetFields();
            },
            handleSelectionChange(val) {
                this.selected = [];
                val.forEach((item) => {
                    this.selected.push(item.id);
                });
            },
            queryRows() {
                this.loading = true;
                this.$axios.post(this.pre_url + "/query/list", this.queryParam).then(({data}) => {
                    this.data = data.result.records;
                    this.total = data.result.total;
                    this.loading = false;
                });
            },
            addRow() {
                this.show_form = true;
            },
            deleteRows() {
                this.$confirm('确认删除选中行?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.form_loading = true;
                    this.$axios.post(this.pre_url + "/delete", this.selected).then(({data}) => {
                        this.form_loading = false;
                        this.$message({
                            type: 'success',
                            message: data.message,
                        });
                        this.queryRows();
                    }).catch(error => {
                        console.log(error);
                        this.form_loading = false;
                        this.$message({
                            type: 'error',
                            message: '删除失败!'
                        });
                    });
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            handleEdit(index, row) {
                this.form = row;
                this.show_form = true;
            },
            handleDel(index, id) {
                this.$axios.get(this.pre_url + "/delete/" + id).then(({data}) => {
                    this.$message({
                        type: 'success',
                        message: data.message,
                    });
                    this.queryRows();
                }).catch(error => {
                    console.log(error);
                    this.$message({
                        type: 'error',
                        message: '删除失败!'
                    });
                });
            },
            cancelCU() {
                this.show_form = false;
                this.resetForm();
            },
            submitCU() {
                this.$axios.post(this.pre_url + "/add", this.form).then(({data}) => {
                    console.log(data);
                    if (data.code == 200) {
                        this.show_form = false;
                        this.resetForm();
                        this.queryRows();
                    }
                });
            },
            handleSizeChange(limit) {
                this.queryParam.limit = limit;
            },
            handleCurrentChange(current) {
                this.queryParam.current = current;
                this.queryRows();
            },
        }
    }
</script>
<style lang="scss" scoped>
</style>