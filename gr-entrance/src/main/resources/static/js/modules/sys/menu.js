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
        sysMenu: {
            menuId: 0,
            type: 1,
            typeList: ['目录', '菜单', '按钮'],
            title: '',
            parentId: 0,
            parentName: '',
            url: '',
            params: '',
            sort: 0,
            icon: '',
            iconList: []
        },
        typeList: ['目录', '菜单', '按钮'],
        menuList: [],
        menuListTree: [],
        rules: {
            title: [
                { required: true, message: '菜单名称不能为空', trigger: 'blur' }
            ],
            parentName: [
                { required: true, message: '上级菜单不能为空', trigger: 'change' }
            ],
            url: [
                { required: true, message: '菜单URL不能为空', trigger: 'change' }
            ],
        },
        defaultProps: {
            label: 'title',
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
            httpUtils.sendGet('/sys/menu/list', this.queryParams).then(function (res) {
                vm.loading = false;
                vm.pageList = res.data.menuList;
            }).catch(function (err) {
                vm.loading = false;
            });
        },
        /** 展开/折叠操作 */
        toggleExpandAll() {
            this.refreshTable = false;
            this.isExpandAll = !this.isExpandAll;
            this.$nextTick(() => {
                this.refreshTable = true;
            });
        },
        /** 新增 */
        handleAdd(row) {
            vm.title = "新增菜单";
            vm.showList = true;
            vm.sysMenu = { url: '', type: 1, typeList: ['目录', '菜单', '按钮'], parentName: '', sort: 0, status: 0 }

            if (row.menuId != undefined) {
                vm.sysMenu.parentId = row.menuId;
                vm.sysMenu.parentName = row.title;
            }

            vm.getMenuListTree();
        },
        /** 修改 */
        handleUpdate(row) {
            vm.title = "修改菜单";
            vm.showList = true;

            vm.getInfo(row);
            vm.getMenuListTree();
        },
        // 获取不包含按钮的菜单列表
        getMenuListTree(){
           httpUtils.sendGet('/sys/menu/select').then(res => {
                vm.menuList = res.data.menuList;
                // vm.menuListTree = res.data.menuList;
                // vm.menuListTreeSetCurrentNode();
            })
        },
        // 菜单树选中
        menuListTreeCurrentChangeHandle (data, node) {
            this.sysMenu.parentId = data.menuId
            this.sysMenu.parentName = data.title
        },
        // 菜单树设置当前选中节点
        menuListTreeSetCurrentNode () {
            this.$refs.menuListTree.setCurrentKey(vm.sysMenu.parentId)
            this.sysMenu.parentName = (this.$refs.menuListTree.getCurrentNode() || {})['title']
        },
        // 图标选中
        iconActiveHandle (iconName) {
            this.sysMenu.icon = iconName
        },
        // 表单提交
        saveOrUpdate () {
            this.$refs.sysMenu.validate((valid) => {
                if (valid) {
                    if(vm.sysMenu.icon && vm.sysMenu.type !== 2 && vm.sysMenu.menuId == undefined){
                        vm.sysMenu.icon = vm.sysMenu.type === 0 ? 'pear-icon pear-icon-navigation' : 'layui-icon layui-icon-vercode';
                    }
                    let url = vm.sysMenu.menuId == null ? '/sys/menu/save' : '/sys/menu/update';
                    httpUtils.sendPost(url, vm.sysMenu).then(function (res) {
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
               httpUtils.sendGet('/sys/menu/delete?menuId=' + menuId).then(function (res) {
                    if(res.data.code === 0){
                        vm.$message({ type: 'success', message: '删除成功' });
                        vm.reload();
                    }else {
                        vm.$message.error(res.data.msg);
                    }
                });
            }).catch(() => {});
        },
        getInfo (row) {
           httpUtils.sendGet('/sys/menu/info/' + row.menuId).then(res => {
                vm.sysMenu = res.data.sysMenu;
                vm.sysMenu.parentName = row.parentName == null ? '一级菜单' : row.parentName;
                vm.sysMenu.typeList = vm.typeList;
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