import Vue from 'vue'
import Router from 'vue-router'
import mainBody from '@/pages/mainBody'
import register from '@/pages/register'
import userPage from '@/pages/userPage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'mainBody',
      component: mainBody
    },{
      path: '/register',
      name: 'register',
      component: register
    },{
      path: '/my',
      name: 'userPage',
      component: userPage
    },{
      path: '*',
      name: 'mainBody',
      component: mainBody
    }
    
  ]
})
