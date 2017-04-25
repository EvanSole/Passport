// routes & menu
import Main from "./components/layouts/Main.vue";
import applications from "./components/views/Applications.vue";
import users from "./components/views/Users.vue";
import loginLog from "./components/views/LoginLog.vue";
import logMain from "./components/views/LogMain.vue";
import oauthLog from "./components/views/OauthLog.vue";
import login from "./components/views/Login.vue";
import oauth2Login from "./components/views/Oauth2Login.vue";

let routes = [
  {
	    path:'/login',
      name:'',
      component:login,
      hidden:true,
      leaf:false,
  },
  {
    path:'/oauthlogin',
    name:'',
    component:oauth2Login,
    hidden:true,
    leaf:false,
  },
  {
	    path:'/',
      name:'',
	    component:Main,
      hidden:false,
      leaf:true,
      iconCls: 'el-icon-menu',
      children:[
        { path:'/applications',component:applications,name:'应用管理',iconCls: 'el-icon-menu',hidden:false,leaf:true },
        { path:'/users',component:users,name:'用户管理',iconCls: 'el-icon-setting',hidden:false,leaf:true },
        { path:'/log',component:logMain,name:'日志管理',iconCls: 'el-icon-message',hidden:false,leaf:false,
             children:[
                { path:'/loginLog',component:loginLog,name:'登录日志',hidden:false,leaf:false },
                { path:'/oauthLog',component:oauthLog,name:'授权日志',hidden:false,leaf:false }
             ]
        }
      ]
  },
  {
    path:'/*',
    name:'',
    component:login,
    hidden:true,
    leaf:false,
  },
]

export default routes;
