var vm = new Vue({
    el: '#webApp',
    data: {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非多个禁用
        multiple: true,
        // 默认排序
        defaultSort: {prop: 'createTime', order: 'create_time'},
        // 表格数据
        pageList: [],
        // 查询参数
        queryParams: {
            page: 1,
            limit: 10,
            pageSize: 10,
            totalCount: 0,
            information: null,
        },
    },
    created: function () {
        this.getPageList(1);
    },
    computed: {

    },
    methods : {
        /** 多选框选中数据 */
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.userId);
            this.single = selection.length != 1;
            this.multiple = !selection.length;
        },
        /** 排序触发事件 */
        handleSortChange(column, prop, order) {
            //this.queryParams.order = column.prop;
            //this.queryParams.isAsc = column.order;
            //this.getPageList();
        },
        /** 每页数 */
        handleSizeChange(val) {
            //console.log(`每页 ${val} 条`);
            this.queryParams.limit = val;
            this.getPageList(this.queryParams.page);
        },
        /** 当前页 */
        handleCurrentChange(val) {
            //console.log(`当前页 ${val} 条`);
            this.queryParams.page = val;
            this.getPageList(this.queryParams.page);
        },
        handleQuery: function () {
            vm.getPageList(1);
        },
        getPageList: function (page) {
            this.pageList = []
            this.loading = true;
            this.queryParams.page = page;
            httpUtils.sendGet('/sys/online/list', this.queryParams).then(function (res) {
                vm.loading = false;
                vm.pageList = res.data.page.list;
                vm.queryParams.pageSize = res.data.page.pageSize;
                vm.queryParams.totalCount = res.data.page.totalCount;
                vm.queryParams.currentPage = res.data.page.currentPage;
            }).catch(function (err) {
                vm.loading = false;
            });
        },
        /** 踢出用户 */
        batchForceLogout(row) {
            const userIds = row.userId == undefined ? this.ids : [row.userId];
            if(userIds.length === 0 || userIds == undefined){
                return;
            }
            this.$confirm('确定要将该用户踢出吗？', '系统提示', { type: 'warning' }).then(() => {
                vm.openFullScreen();
                httpUtils.sendPost('/sys/online/batchForceLogout', userIds).then(function (res) {
                    vm.loading.close();
                    if(res.data.code === 0){
                        vm.getPageList(1);
                        vm.$message({ type: 'success', message: '踢出成功' });
                    }
                }).catch(() => {});
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
        resetQuery: function () {
            window.location.reload();
        },
    }
});