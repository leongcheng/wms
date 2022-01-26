var vm = new Vue({
    el: '#webApp',
    data: {
        pageList: [],
        loading: false,
        showList: false,
        // 查询参数
        queryParams: {
            page: 1,
            pageSize: 10,
            totalCount: 0,
            information: null,
        },
        // 是否展开，默认全部展开
        isExpandAll: false,
        // 重新渲染表格状态
        refreshTable: true,
        title: '',
        sysDept: {},
        deptList: [],
        rules: {
            parentName: [
                { required: true, message: '上级部门不能为空', trigger: 'change' }
            ],
            deptName: [
                { required: true, message: '部门名称不能为空', trigger: 'blur' }
            ],
            deptCode: [
                { required: true, message: '部门编号不能为空', trigger: 'blur' }
            ],
        },
        defaultProps: {
            label: 'deptName',
            children: 'children'
        },
        statusList: [{label: '显示', value: 0}, {label: '隐藏', value: 1}]
    },
    created: function () {
        this.getPageList(1);
    },
    methods : {
        /** 搜索参数 */
        handleQuery: function () {
            vm.getPageList(1);
        },
        /** 获取数据列表 */
        getPageList: function (page) {
            this.pageList = [];
            this.loading = true;
            this.queryParams.page = page;
            httpUtils.sendGet('/sys/dept/deptTree', this.queryParams).then(function (res) {
                vm.loading = false;
                vm.pageList = res.data.deptList;
                vm.deptList = res.data.deptList;
            }).catch(function (err) {
                vm.loading = false;
            });
        },
        /** 新增 */
        handleAdd(row) {
            vm.title = "新增部门";
            vm.showList = true;
            vm.sysDept = { parentName: '', sort: 0, status: 0 }

            if (row.deptId != undefined) {
                vm.sysDept.parentId = row.deptId;
                vm.sysDept.parentName = row.deptName;
            }
        },
        /** 展开/折叠操作 */
        toggleExpandAll() {
            vm.refreshTable = false;
            vm.isExpandAll = !vm.isExpandAll;
            this.$nextTick(() => {
                this.refreshTable = true;
            });
        },
        /** 修改 */
        handleUpdate(row) {
            vm.title = "修改部门";
            vm.showList = true;

            vm.getInfo(row);
        },
        // 表单提交
        saveOrUpdate () {
            this.$refs.sysDept.validate((valid) => {
                if (valid) {
                    let url = vm.sysDept.menuId == null ? '/sys/dept/save' : '/sys/dept/update';
                    httpUtils.sendPost(url, vm.sysDept).then(function (res) {
                        if(res.data.code === 0){
                            vm.$message({ type: 'success', message: '操作成功' });
                            vm.reload();
                        }
                    });
                }
            });
        },
        // 删除
        handleDelete (menuId) {
            this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
               httpUtils.sendGet('/sys/dept/delete?menuId=' + menuId).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
                    }
                });
            }).catch(() => {});
        },
        getInfo (row) {
           httpUtils.sendGet('/sys/dept/info/' + row.deptId).then(res => {
                vm.sysDept = res.data.sysDept;
                vm.sysDept.parentName = row.parentName == null ? '一级部门' : row.parentName;
            });
        },
        // 部门树选中
        menuListTreeCurrentChangeHandle (data, node) {
            this.sysDept.parentId = data.deptId
            this.sysDept.parentName = data.deptName
        },
        // 部门树设置当前选中节点
        menuListTreeSetCurrentNode () {
            //this.$refs.menuListTree.setCurrentKey(vm.sysDept.parentId)
            //this.sysDept.parentName = (this.$refs.menuListTree.getCurrentNode() || {})['title']
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