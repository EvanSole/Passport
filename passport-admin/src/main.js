import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import VueAxios from 'vue-axios'
import VueRouter from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import routes from './routes'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import App from './App'
import * as Auth from 'common/auth'

Vue.use(Vuex)
Vue.use(VueAxios, axios)
Vue.use(ElementUI)
Vue.use(VueRouter)

NProgress.inc(0.2)
NProgress.configure({ easing: 'ease', speed: 500, showSpinner: false })


/*******************************************
 ******  axios  request interceptors  ******
 *******************************************/
// Add a request interceptor
axios.interceptors.request.use(function (config) {
  NProgress.start()
  return config
}, function (error) {
  NProgress.done()
  // Do something with request error
  return Promise.reject(error)
})
// Add a response interceptor
axios.interceptors.response.use(function (response) {
  NProgress.done()
  return response
}, function (error) {
  NProgress.done()
  if (error.response.status === 401) {
    window.vue.$message({
       title: '提示',
       message: `您无权限访问该资源!`,
       type: 'error'
    })
    Auth.redirectLogin()
  }
  else if (error.response.status === 404) {
    window.vue.$message({
      title: '提示',
      message: `您访问的资源不存在!`,
      type: 'error'
    })
  }
  else if(error.response.status  === 403){
    window.vue.$message('您的会话已经失效，即将返回到登录!',
      '提示', {
        type: 'warning'
      })
    Auth.redirectLogin();
  }
  else if (error.response.status === 500) {
    window.vue.$message({
      title: '提示',
      message: `服务端错误!`,
      type: 'error'
    })
  }
  else if (error.response.status === 504) {
    window.vue.$message({
      title: '提示',
      message: `请求超时,请检查服务是否正常!`,
      type: 'error'
    })
  }
  else {
    window.vue.$message({
      title: '提示',
      message: `${error.response.data.messages}`,
      type: 'error'
    })
  }
  return Promise.reject(error)
})


/************************************
 ******  routes  interceptors  ******
 ************************************/
const router = new VueRouter({
  routes
})
router.beforeEach((to,from,next) => {
  NProgress.start()
  // anonymous ==> next
  if (to.path === '/login'||to.path ==='/oauthlogin') {
    next()
    return
  }
  // need login adn no login ==> login page
  if (!Auth.hasLogin()) {
    next('/login')
    return
  }
  next()
})
router.afterEach(() => {
  NProgress.done()
})

/* eslint-disable no-new */
const  VueComponent = new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
window.vue = VueComponent
