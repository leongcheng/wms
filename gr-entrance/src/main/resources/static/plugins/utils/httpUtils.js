//  axios API文档 http://www.axios-js.com/zh-cn/docs
// Set config defaults when creating the instance
const instance = axios.create({
    // 请求超时 60 秒
    timeout: 60000
})

// Alter defaults after instance has been created
instance.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
instance.defaults.headers['AUTH_TOKEN'] = localStorage.getItem('AUTH_TOKEN');
instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

let baseURL = '../../';

let downloadLoadingInstance;

// 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    return response;
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});

/**
 * http请求封装
 */
function httpUtils() {

}

/**
 * 封装Get的request请求
 */
httpUtils.sendGet = function (url, data = {}, method = "GET", header = 'application/json') {

    return new Promise(function (resolve, reject) {
        instance({
            url: baseURL + url,
            params: data,
            method: method,
            header: {'Content-Type': header}
        }).then(function (res) {
            // console.log(res)
            if(res.status === 200) {
                //请求成功后判断返回状态
                if (res.data.code === 0) {
                    //正常请求访问
                    resolve(res);
                }
                else if (res.data.code === 401 || localStorage.getItem('token'))
                {
                    window.location.href = '/logout'
                }
                else
                {
                    vm.$message.error(res.data.msg);
                }
            }

        }).catch(function (err) {
            // console.log(err)
            vm.$message.error('服务器无响应，请稍后再试');
            // 请求失败
            reject(err)
        });
    });
}

/**
 * 封装Post的request请求
 */
httpUtils.sendPost = function (url, data = {}, method = "POST", header = 'application/x-www-form-urlencoded') {

    return new Promise(function (resolve, reject) {
        instance({
            url: baseURL + url,
            data: data,
            method: method,
            header: {'Content-Type': header}
        }).then(function (res) {
            // console.log(res)
            if(res.status === 200) {
                //请求成功后判断返回状态
                if (res.data.code === 0) {
                    //正常请求访问
                    resolve(res);
                }
                else if (res.data.code === 401 || localStorage.getItem('token'))
                {
                    window.location.href = '/logout'
                }
                else
                {
                    vm.$message.error(res.data.msg);
                }
            }

        }).catch(function (err) {
            // console.log(err)
            vm.$message.error('服务器无响应，请稍后再试');
            // 请求失败
            reject(err)
        });
    });
}


/**
 * 封装下载的request请求
 */
httpUtils.sendDownload = function (url, data = {}, method = "POST", header = 'application/x-www-form-urlencoded') {
    downloadLoadingInstance = vm.$loading({ text: "正在下载数据，请稍候", spinner: "el-icon-loading", background: "rgba(0, 0, 0, 0.7)", })
    instance({
        url: baseURL + url,
        data: data,
        method: method,
        header: {'Content-Type': header},
        responseType: 'blob'
    }).then(function (res) {
        // console.log(res)
        if(res.status === 200){
            var fileName = decodeURIComponent(res.headers["content-disposition"].split(";")[1].split("=")[1]);
            if ('download' in document.createElement('a')) {
                var url = window.URL.createObjectURL(res.data);
                var link = document.createElement("a");
                document.body.appendChild(link);
                link.href = url;
                link.download = fileName;
                link.click();
                window.URL.revokeObjectURL(url);// 释放URL 对象
                document.body.removeChild(link);
            } else {//兼容IE10+下载
                var blob = new Blob([res.data]);
                navigator.msSaveOrOpenBlob(blob, fileName);
            }
        }
        downloadLoadingInstance.close();
    }).catch(function (err) {
        downloadLoadingInstance.close();
        // console.log(err)
        vm.$message.error('下载文件出现错误，请联系管理员');
    });
}

function openFullScreen () {
    this.loading = this.$loading({
        lock: true,
        text: "正在处理...",
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
    });
}

/**
 * 参数处理
 * @param {*} params  参数
 */
function tansParams(params) {
    let result = ''
    for (const propName of Object.keys(params)) {
        const value = params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof (value) !== "undefined") {
            if (typeof value === 'object') {
                for (const key of Object.keys(value)) {
                    if (value[key] !== null && typeof (value[key]) !== 'undefined') {
                        let params = propName + '[' + key + ']';
                        var subPart = encodeURIComponent(params) + "=";
                        result += subPart + encodeURIComponent(value[key]) + "&";
                    }
                }
            } else {
                result += part + encodeURIComponent(value) + "&";
            }
        }
    }
    return result
}