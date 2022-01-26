layui.use(['admin', 'jquery', 'layer', 'popup'], function () {
    let admin = layui.admin;
    let $ = layui.jquery;
    let layer = layui.layer;
    let popup = layui.popup;

    // 框架初始化时会读取 根目录下 pear.config.yml 文件作为初始化配置
    // 你可以通过 admin.setConfigPath 方法修改配置文件位置
    // 你可以通过 admin.setConfigType 方法修改配置文件类型
    admin.setConfigType("yml");
    admin.setConfigPath("pear.config.yml");
    admin.render();

    // 注销实现
    admin.logout(function () {
        let loading = layer.load();
        $.ajax({
            url: '/logout',
            dataType: 'json',
            type: 'post',
            success: function (result) {
                layer.close(loading);
                if (result.success) {
                    popup.success(result.msg, function () {
                        location.href = "/";
                    });
                    return true;
                }
            }
        })
    })
})

var vm = new Vue({
    el: '#webApp',
    data: {
        // 查询参数
        queryParams: {
            page: 1,
            pageSize: 10,
            totalCount: 0,
            type: 1,
            status: 0,
            information: null,
        },

        feedback: {},
        pageList: [],
        activeName: 'first',
        messageVisible: false,
        innerVisible: false,

        loginUser: {},
        passwordVisible: false,
        rules: {
            password: [
                { required: true, message: "原密码不能为空", trigger: "blur" },
                { min: 5, max: 24, message: '密码长度必须介于 6 和 24 之间', trigger: 'blur' }
            ],
            newPassword: [
                { required: true, message: "新密码不能为空", trigger: "blur" },
                { min: 5, max: 24, message: '密码长度必须介于 6 和 24 之间', trigger: 'blur' }
            ],
            checkPass: [
                { required: true, message: '请再次输入密码', trigger: 'blur' },
                { min: 5, max: 24, message: '密码密码长度必须介于 6 和 24 之间', trigger: 'blur' }
            ],
            age: [
                { required: true, message: "年龄不能为空", trigger: "blur" }
            ]
        },

        feedbacks: {
            title: [
                { required: true, message: "标题不能为空", trigger: "blur" }
            ],
            content: [
                { required: true, message: "内容不能为空", trigger: "blur" }
            ]
        }
    },
    created: function () {

    },
    methods: {
        /** 意见反馈 */
        handleClick: function(tab, event) {
            vm.queryParams.status = vm.activeName == 'first' ? 0 : ' '
            vm.handMessage();
        },
        /** 查询table数据 */
        handMessage: function() {
            this.messageVisible = true;
            var url = this.queryParams.type == 3 ? "sys/feedback/list" : "sys/notice/list";
            axios.get(url, {
                params: {
                    ...this.queryParams
                }
            }).then(function (res) {
                vm.pageList = res.data.page.list;
            }).catch(() => {});
        },
        /** 切换消息中心 */
        handleOpen: function(event) {
            vm.queryParams.type = event
            vm.handMessage();
        },
        onSubmit: function () {
            this.$refs.feedback.validate((valid) => {
                if (valid) {
                    axios.post('sys/feedback/update', vm.feedback).then(function (res) {
                        if(res.data.code === 0){
                            vm.innerVisible = false;
                            vm.$message({ type: 'success', message: '操作成功' });
                        }else {
                            vm.$message.error(res.data.msg);
                        }
                    });
                }
            });
        },
        updatePassWord: function(event){
            this.$refs.loginUser.validate((valid) => {
                if (valid) {
                    if(vm.loginUser.checkPass == vm.loginUser.newPassword){
                        axios.get('sys/user/updatePassword', {
                            params: {
                                password: vm.loginUser.password,
                                newPassword: vm.loginUser.newPassword
                            }
                        }).then(function (res) {
                            if(res.data.code === 0){
                                vm.$confirm('密码修改成功，请重新登录', '温馨提示', {
                                    confirmButtonText: '确定',
                                    cancelButtonText: '取消',
                                    type: 'success'
                                }).then(() => {
                                    window.location.href = "logout";
                                }).catch(() => {});
                            }else {
                                vm.$message.error(res.data.msg);
                            }
                        });
                    }else {
                        vm.$message.error('两次输入密码不一致');
                    }
                }
            });
        },
        logout: function () {
            this.$confirm('确定注销并退出系统吗？', '系统提示', { type: 'warning' }).then(() => {
                window.location.href = "logout";
            }).catch(() => {});
        }
    },
});