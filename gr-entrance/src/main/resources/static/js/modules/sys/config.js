var vm = new Vue({
    el: '#webApp',
    data: {
        // 显示修改
        showList: false,
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 表格数据
        pageList: [],
        // 默认排序
        defaultSort: {prop: 'createTime', order: 'create_time'},
        // 查询参数
        queryParams: {
            page: 1,
            pageSize: 10,
            totalCount: 0,
            information: null,
            startDate: null,
            endDate: null,
        },
        title: '',
        sysConfig: {},

        companyList : [],
        roleList: [],
        postList: [],
        // 表单校验
        rules: {
            configName: [
                { required: true, message: "参数名称不能为空", trigger: "blur" }
            ],
            configCode: [
                { required: true, message: "参数编码不能为空", trigger: "blur" }
            ],
        },
        statusList: [{label: '正常', value: 0}, {label: '停用', value: 1}]
    },
    created: function () {
        this.getPageList(1);
    },
    computed: {

    },
    methods : {
        /** 多选框选中数据 */
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.configId);
            this.single = selection.length != 1;
            this.multiple = !selection.length;
        },
        /** 排序触发事件 */
        handleSortChange(column, prop, order) {
            //this.queryParams.order = column.prop;
            //this.queryParams.isAsc = column.order;
            //this.getPageList();
        },
        /** 每页 ${val} 条 */
        handleSizeChange(val) {
            //console.log(`每页 ${val} 条`);
            this.queryParams.limit = val;
            this.getPageList(this.queryParams.page);
        },
        /** 当前页: ${val} */
        handleCurrentChange(val) {
            //console.log(`当前页 ${val} 条`);
            this.queryParams.page = val;
            this.getPageList(this.queryParams.page);
        },
        handleQuery: function () {
            vm.queryParams.startDate = vm.dateRange != null ? vm.dateRange[0] : null;
            vm.queryParams.endDate = vm.dateRange != null ? vm.dateRange[1] : null;
            vm.getPageList(1);
        },
        getPageList: function (page) {
            this.pageList = [];
            this.loading = true;
            this.queryParams.page = page;
            httpUtils.sendGet('/sys/config/list', this.queryParams).then(function (res) {
                vm.loading = false;
                vm.pageList = res.data.page.list;
                vm.queryParams.pageSize = res.data.page.pageSize;
                vm.queryParams.totalCount = res.data.page.totalCount;
                vm.queryParams.currentPage = res.data.page.currentPage;
            }).catch(function (err) {
                vm.loading = false;
            });
        },
        /** 新增 */
        handleAdd(row) {
            vm.title = "添加参数";
            vm.showList = true;
            vm.sysConfig = { status: 0 };
        },
        /** 修改 */
        handleUpdate(row) {
            const configId = row.configId || this.ids;
            if(configId == null){
                return;
            }
            vm.title = "修改参数";
            vm.showList = true;
            vm.sysConfig = {};

            vm.getInfo(configId);
        },
        saveOrUpdate(){
            this.$refs.sysConfig.validate((valid) => {
                if (valid) {
                    let url = vm.sysConfig.configId == null ? '/sys/config/save' : '/sys/config/update';
                    httpUtils.sendPost(url, vm.sysConfig).then(function (res) {
                        if(res.data.code === 0){
                            vm.$message({ type: 'success', message: '操作成功' });
                            vm.reload();
                        }else {
                            vm.$message.error(res.data.msg);
                        }
                    });
                }
            });
        },
        /** 删除 */
        handleDelete(row) {
            const configIds = this.ids || [row.configId];
            if(configIds.length === 0 || configIds == undefined){
                return;
            }
            this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
                httpUtils.sendPost('/sys/config/delete', configIds).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
                    }else {
                        vm.$message.error(res.data.msg);
                    }
                });
            }).catch(() => {});
        },
        /** 导出 */
        handleExport: function () {
            httpUtils.sendDownload('/sys/schedule/export', Qs.stringify(vm.q)).then(res => {
                // console.log(res.data)
            });
        },
        getInfo: function (id) {
           httpUtils.sendGet('/sys/config/info/' + id).then(function (res) {
                vm.sysConfig = res.data.sysConfig
            });
        },
        openFullScreen: function() {
            this.loading = this.$loading({
                lock: true,
                text: "正在处理...",
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
        },
        reload: function (event) {
            vm.showList = false;
            vm.getPageList(1);
        },
        resetQuery: function () {
            window.location.reload();
        },
    }
});