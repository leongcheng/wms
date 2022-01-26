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
        defaultProps: {
            children: "children",
            label: "label"
        },
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
        sysRole: {},

        // 菜单列表
        menuOptions: [],
        // 部门列表
        deptOptions: [],
        // 默认选中的节点
        checkedKeys: [],
        // 表单校验
        rules: {
            roleName: [
                { required: true, message: "角色名称不能为空", trigger: "blur" }
            ],
            roleKey: [
                { required: true, message: "权限字符不能为空", trigger: "blur" }
            ],
            roleSort: [
                { required: true, message: "角色顺序不能为空", trigger: "blur" }
            ]
        },
        menuExpand: false,
        menuNodeAll: false,
        statusList: [{label: '正常', value: 0}, {label: '停用', value: 1}]
    },
    created: function () {
        this.getPageList(1);
    },
    computed: {

    },
    methods : {
        /** 查询菜单树结构 */
        getMenuTreeselect() {
           httpUtils.sendGet('/sys/menu/menuTree').then((res => {
                vm.menuOptions = res.data.menuTrees;
            })).catch(() => {})
        },
        /** 根据角色ID查询菜单树结构 */
        getRoleMenuTreeselect(roleId) {
           httpUtils.sendGet('/sys/menu/roleMenuTreeSelect/' + roleId).then((res => {
                // console.log(res.data.map)
                vm.menuOptions = res.data.map.menus;
                res.data.map.checkedKeys.forEach((v) => {
                    this.$nextTick(()=>{
                        this.$refs.menu.setChecked(v, true ,false);
                    })
                })
            })).catch(() => {})
        },
        // 所有菜单节点数据
        getMenuAllCheckedKeys() {
            // 目前被选中的菜单节点
            let checkedKeys = this.$refs.menu.getCheckedKeys();
            // 半选中的菜单节点
            let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
            checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
            return checkedKeys;
        },
        // 树权限（展开/折叠）
        handleCheckedTreeExpand(value, type) {
            if (type == 'menu') {
                let treeList = this.menuOptions;
                for (let i = 0; i < treeList.length; i++) {
                    this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value;
                }
            } else if (type == 'dept') {
                let treeList = this.deptOptions;
                for (let i = 0; i < treeList.length; i++) {
                    this.$refs.dept.store.nodesMap[treeList[i].id].expanded = value;
                }
            }
        },
        // 树权限（全选/全不选）
        handleCheckedTreeNodeAll(value, type) {
            if (type == 'menu') {
                this.$refs.menu.setCheckedNodes(value ? this.menuOptions: []);
            } else if (type == 'dept') {
                this.$refs.dept.setCheckedNodes(value ? this.deptOptions: []);
            }
        },
        /** 多选框选中数据 */
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.roleId);
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
            httpUtils.sendGet('/sys/role/list', this.queryParams).then(function (res) {
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
            vm.title = "添加角色";
            vm.showList = true;
            vm.sysRole = { roleSort: 0, status: 0 };
            // $("#modalSaveOrUpdate").modal('show');

            //查询菜单树结构
            vm.getMenuTreeselect();
        },
        /** 修改 */
        handleUpdate(row) {
            const roleId = row.roleId || this.ids;
            if(roleId == null){
                return;
            }
            vm.title = "修改角色";
            vm.showList = true;
            vm.sysRole = {};
            // $("#modalSaveOrUpdate").modal('show');

            vm.getRoleMenuTreeselect(roleId);

            vm.getInfo(roleId);
        },
        saveOrUpdate(){
            this.$refs.sysRole.validate((valid) => {
                if (valid) {
                    vm.sysRole.menuIdList = vm.getMenuAllCheckedKeys();
                    let url = vm.sysRole.roleId == null ? '/sys/role/save' : '/sys/role/update';
                    httpUtils.sendPost(url, vm.sysRole).then(function (res) {
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
            const roleIds = row.roleId == undefined ? this.ids : [row.roleId];
            if(roleIds.length === 0 || roleIds == undefined){
                return;
            }
            this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
                httpUtils.sendPost('/sys/role/delete', roleIds).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
                    }
                });
            }).catch(() => {});
        },
        /** 导出 */
        handleExport: function () {
            httpUtils.sendDownload('/sys/role/export', Qs.stringify(vm.q)).then(res => {
                // console.log(res.data)
            });
        },
        getInfo: function (id) {
           httpUtils.sendGet('/sys/role/info/' + id).then(function (res) {
                vm.sysRole = res.data.sysRole
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
        reload: function () {
            vm.showList = false;
            // $("#modalSaveOrUpdate").modal('hide');

            vm.getPageList(1);
        },
        resetQuery: function () {
            window.location.reload();
        },
    }
});