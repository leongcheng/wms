var vm = new Vue({
    el: '#webApp',
    data: {
        pickerOptions: {
            shortcuts: [{
                text: '最近一周',
                onClick(picker) {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                    picker.$emit('pick', [start, end]);
                }
            }, {
                text: '最近一个月',
                onClick(picker) {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                    picker.$emit('pick', [start, end]);
                }
            }, {
                text: '最近三个月',
                onClick(picker) {
                    const end = new Date();
                    const start = new Date();
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                    picker.$emit('pick', [start, end]);
                }
            }]
        },
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
        // 日期范围
        dateRange: [],
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
        schedule: {},

        // 表单校验
        rules: {
            beanName: [
                { required: true, message: "请输入任务名称", trigger: "blur" }
            ],
            cronExpression: [
                { required: true, message: "请输入cron表达式", trigger: "blur" }
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
            this.ids = selection.map(item => item.jobId);
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
        /** 搜索参数 */
        handleQuery: function () {
            vm.queryParams.startDate = vm.dateRange != null ? vm.dateRange[0] : null;
            vm.queryParams.endDate = vm.dateRange != null ? vm.dateRange[1] : null;
            vm.getPageList(1);
        },
        /** 查询列表数据 */
        getPageList: function (page) {
            this.pageList = [];
            this.loading = true;
            this.queryParams.page = page;
            httpUtils.sendGet('/sys/schedule/list', this.queryParams).then(function (res) {
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
            vm.title = "添加任务";
            vm.schedule = { status: 0, params: '系统默认（无参）' };
            vm.showList = true;
        },
        /** 修改 */
        handleUpdate(row) {
            // console.log(this.ids)
            const jobId = row.jobId || this.ids;
            if(jobId == null){
                return;
            }
            vm.title = "修改任务";
            vm.schedule = {};
            vm.showList = true;

            vm.getInfo(jobId);
        },
        // 更多操作触发
        handleCommand(command, row) {
            switch (command) {
                case "handleRun":
                    this.handleRun(row);
                    break;
                case "handleView":
                    this.handleView(row);
                    break;
                case "handleJobLog":
                    this.handleJobLog(row);
                    break;
                default:
                    break;
            }
        },
        /** 立即执行一次 */
        handleRun(row) {
            // console.log(this.ids)
            const jobId = row.jobId || this.ids;
            if(jobId == null){
                return;
            }
            this.$nextTick(() => {
                httpUtils.sendPost('/sys/schedule/run', row).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '执行成功' });
                        vm.reload();
                    }
                });
            });
        },
        /** 调度日志 */
        handleJobLog: function (event) {
            window.location.href = '../../modules/quartz/schedule_log.html';
            // window.location.href = '../../modules/quartz/schedule_log.html?jobId=' + event.jobId == undefined ? '' : event.jobId;
        },
        /** 表单提交 */
        saveOrUpdate(){
            this.$refs.schedule.validate((valid) => {
                if (valid) {
                    let url = vm.schedule.jobId == null ? '/sys/schedule/save' : '/sys/schedule/update';
                    httpUtils.sendPost(url, vm.schedule).then(function (res) {
                        if(res.data.code === 0){
                            vm.$message({ type: 'success', message: '操作成功' });
                            vm.reload();
                        }
                    });
                }
            });
        },
        /** 删除 */
        handleDelete(row) {
            const jobIds = row.jobId == undefined ? this.ids : [row.jobId];
            if(jobIds.length === 0 || jobIds == undefined){
                return;
            }
            this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
                httpUtils.sendPost('/sys/schedule/delete', jobIds).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
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
            httpUtils.sendGet('/sys/schedule/info/' + id).then(function (res) {
                vm.schedule = res.data.schedule
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