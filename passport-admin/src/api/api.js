import axios from 'axios'
import qs from 'qs';

// 用户登录
export const login = params => { return axios.post('/api/login',qs.stringify(params),{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}}).then( res => res.data);};
// 用户退出
export const logout = () => { axios.get('/api/logout').then((response) => { window.vue.$router.replace('/login');window.localStorage.clear()})}

// 应用管理
export const getApplicationsListPage = params => { return axios.get('/api/client', {params:params}).then(res => res.data); };
export const removeApplication = params => { return axios.delete('/api/client/' + params.id, { params: params }).then(res => res.data); };
export const addApplication = params => { return axios.post('/api/client', params ).then(res => res.data); };
export const editApplication = params => { return axios.put('/api/client/' + params.id, params).then(res => res.data); };

// 应用与用户进行绑定
export const clientAndUser = params => { return axios.post('/api/clientUser', params).then(res => res.data);};
// 查找应用可用用户
export const findClientUser = params => { return axios.get('/api/clientUser/' + params.id, { params: params } ).then(res => res.data);};

// 用户管理
export const getUsersListPage = params => { return axios.get('/api/user', {params:params}).then(res => res.data); };
export const removeUser = params => { return axios.delete('/api/user/' + params.id, {params:params} ).then(res => res.data); };
export const addUser = params => { return axios.post('/api/user', params ).then(res => res.data);  };
export const editUser = params => { return axios.put('/api/user/' + params.id, params ).then(res => res.data); };
// 激活或禁用用户
export const activeUser = params => { return axios.put('/api/user/active/' + params.id, params ).then(res => res.data); };
// 用户密码修改
export const pwsChange = params => { return axios.put('/api/user/' + params.id + "/changePwd",  params ).then(res => res.data); };
// 用户密码重置
export const passwordReset = params => { return axios.post('/api/user/' + params.id + "/restPwd", params ).then(res => res.data); };

// 权限日志
export const getOauthLogListPage = params => { return axios.get('/api/oauthLog', { params: params }).then(res => res.data); };

/***
 * 统一认证登录
 */
export const oAuth2Login = params=> { return axios.post('/oauth2/authorize',qs.stringify(params),{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}}).then( res => res.data);};
