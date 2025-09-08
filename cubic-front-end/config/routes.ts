export default [
  { path: '/', name: '主页', icon: 'smile', component: './Index' },
  {
    path: '/interface_info/:id',
    name: '查看接口',
    icon: 'smile',
    component: './InterfaceInfo',
    hideInMenu: true,
  },
  {
    path: '/account/center',
    name: '个人中心',
    icon: 'UserOutlined',
    component: './User/UserInfo',
    hideInMenu: true,
  },
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      {
        name: '注册',
        path: '/user/register',
        component: './User/Register',
      },
    ],
  },
  {
    path: '/interface/list',
    name: '接口广场',
    icon: 'RedditOutlined',
    component: './InterfaceInfoSquare',
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      // { path: '/admin', redirect: '/admin/sub-page' },
      {
        icon: 'table',
        path: '/admin/interface_info',
        name: '接口管理',
        component: './Admin/InterfaceInfo',
      },
      {
        icon: 'analysis',
        name: '接口分析',
        path: 'admin/interface_analysis',
        component: './Admin/InterfaceAnalysis',
      },
    ],
  },
  // { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
