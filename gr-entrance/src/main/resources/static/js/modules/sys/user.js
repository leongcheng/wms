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
        sysUser: {},
        dialogVisible: false,

        // 公司选项
        deptOptions: [],
        // 岗位选项
        postOptions: [],
        // 角色选项
        roleOptions: [],
        // 表单校验
        rules: {
            account: [
                { required: true, message: "登录账号不能为空", trigger: "blur" },
                { min: 2, max: 20, message: '登录账号长度必须介于 2 和 20 之间', trigger: 'blur' }
            ],
            username: [
                { required: true, message: "用户名称不能为空", trigger: "blur" }
            ],
            password: [
                { required: true, message: "登录密码不能为空", trigger: "blur" },
                { min: 5, max: 20, message: '登录密码长度必须介于 5 和 20 之间', trigger: 'blur' }
            ],
            deptId: [
                { required: true, message: "请选择所属公司", trigger: "blur" }
            ],
            email: [
                {
                    type: "email",
                    message: "'请输入正确的邮箱地址",
                    trigger: ["blur", "change"]
                }
            ],
            phone: [
                {
                    pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
                    message: "请输入正确的手机号码",
                    trigger: "blur"
                }
            ]
        },
        statusList: [{label: '正常', value: 0}, {label: '停用', value: 1}]
    },
    created: function () {
        this.getPageList(1);
    },
    computed: {

    },
    methods : {
        // 文件上传中处理
        handlePreview: function(event, file, fileList) {
            vm.dialogVisible = true;
        },
        // 文件上传成功处理
        handleSuccess: function(response, file) {
            vm.dialogVisible = false;
            this.$refs.upload.clearFiles();
            this.$alert(response.msg, "提醒信息", { dangerouslyUseHTMLString: true });
            //this.$message({ type: 'info', message: response.msg });
            vm.getPageList(1);
        },
        // 提交上传文件
        submitUpload: function(){
            this.$refs.upload.submit();
        },
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
            httpUtils.sendGet('/sys/user/list', this.queryParams).then(function (res) {
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
            vm.title = "添加用户";
            vm.sysUser = { status: 0 };
            vm.showList = true;

            vm.getUserDeptList();
            vm.getUserPostList();
            vm.getUserRoleList();
        },
        /** 修改 */
        handleUpdate(row) {
            // console.log(this.ids)
            const userId = row.userId || this.ids;
            if(userId == null){
                return;
            }
            vm.title = "修改用户";
            vm.sysUser = {};
            vm.showList = true;

            vm.getUserDeptList();
            vm.getUserPostList();
            vm.getUserRoleList();
            vm.getInfo(userId);
        },
        /** 所属部门 */
        getUserDeptList: function() {
           httpUtils.sendGet('/sys/dept/list').then((res => {
                vm.deptOptions = res.data.page.list;
            })).catch(() => {})
        },
        /** 查找部门名称 */
        getDeptNameSelect: function (deptId) {
            vm.deptOptions.forEach(item => {
                if(item.deptId == deptId){
                    vm.sysUser.deptName = item.deptName;
                }
            })
        },
        /** 分配岗位操作 */
        getUserPostList: function() {
           httpUtils.sendGet('/sys/post/list').then((res => {
                vm.postOptions = res.data.page.list;
            })).catch(() => {})
        },
        /** 分配角色操作 */
        getUserRoleList: function() {
           httpUtils.sendGet('/sys/role/list').then((res => {
                vm.roleOptions = res.data.page.list;
            })).catch(() => {})
        },
        saveOrUpdate(){
            this.$refs.sysUser.validate((valid) => {
                if (valid) {
                    let url = vm.sysUser.userId == null ? '/sys/user/save' : '/sys/user/update';
                    httpUtils.sendPost(url, vm.sysUser).then(function (res) {
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
            const userIds = row.userId == undefined ? this.ids : [row.userId];
            if(userIds.length === 0 || userIds == undefined){
                return;
            }
            this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
                httpUtils.sendPost('/sys/user/delete', userIds).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
                    }
                });
            }).catch(() => {});
        },
        /** 重置密码 */
        handleResetPassWord(row) {
            const userIds = row.userId == undefined ? this.ids : [row.userId];
            if(userIds.length === 0 || userIds == undefined){
                return;
            }
            this.$confirm('此操作将登录密码重置为123456, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
                httpUtils.sendPost('/sys/user/resetPassword', userIds).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '操作成功' });
                        vm.reload();
                    }
                });
            }).catch(() => {});
        },
        /** 导入 */
        importTemplate: function () {

        },
        /** 导出 */
        handleExport: function () {
            httpUtils.sendDownload('/sys/user/export', Qs.stringify(vm.q)).then(res => {
                // console.log(res.data)
            });
        },
        getInfo: function (id) {
           httpUtils.sendGet('/sys/user/info/' + id).then(function (res) {
                vm.sysUser = res.data.sysUser
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