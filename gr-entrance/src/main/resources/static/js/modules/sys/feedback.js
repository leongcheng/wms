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
		feedback: {},

		// 表单校验
		rules: {
			title: [
				{ required: true, message: "标题不能为空", trigger: "blur" }
			],
			content: [
				{ required: true, message: "内容不能为空", trigger: "blur" }
			],
		}
	},
	created: function () {
		this.getPageList(1);
	},
	computed: {

	},
	methods : {
		/** 多选框选中数据 */
		handleSelectionChange(selection) {
			this.ids = selection.map(item => item.feedbackId);
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
			httpUtils.sendGet('/sys/feedback/list', this.queryParams).then(function (res) {
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
			vm.title = "意见反馈";
			vm.showList = true;
			vm.feedback = {};
		},
		/** 修改 */
		handleUpdate(row) {
			const feedbackId = row.feedbackId || this.ids;
			if(feedbackId == null){
				return;
			}
			vm.title = "意见反馈";
			vm.showList = true;
			vm.feedback = {};

			vm.getInfo(feedbackId);
		},
		//表单提交
		saveOrUpdate(){
			this.$refs.feedback.validate((valid) => {
				if (valid) {
					this.openFullScreen();
					let url = vm.feedback.feedbackId == null ? "/sys/feedback/save" : "/sys/feedback/update";
					httpUtils.sendPost(url, vm.feedback).then(function (res) {
						vm.loading.close();
						if(res.data.code === 0){
							layer.msg('操作成功', {icon: 1});
							vm.reload();
						}
					});
				}
			});
		},
		/** 删除 */
		handleDelete(row) {
			const feedbackIds = row.feedbackId == undefined ? this.ids : [row.feedbackId];
			if(feedbackIds.length === 0 || feedbackId == undefined){
				return;
			}
			this.$confirm('此操作将永久删除该记录, 是否继续?', '系统提示', { type: 'warning' }).then(() => {
				this.openFullScreen();
				httpUtils.sendPost('/sys/feedback/delete', feedbackIds).then(function (res) {
					vm.loading.close();
					if(res.data.code === 0){
						layer.msg('操作成功', {icon: 1});
						vm.reload();
					}
				});
			}).catch(() => {});
		},
		/** 导出 */
		handleExport: function () {
			httpUtils.sendDownload('/sys/feedback/export', Qs.stringify(vm.q)).then(res => {
				// console.log(res.data)
			});
		},
		getInfo: function (feedbackId) {
			axios.get("/sys/feedback/info/" + feedbackId).then(function (res) {
				vm.feedback = res.data.feedback
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