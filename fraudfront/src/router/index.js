import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: MainLayout,
        // component: () => import('../views/Home.vue') },
        children: [
            {path: '/rule', name: 'Rule', component: () => import('../views/Rule.vue')},
            {path: '/blacklist', name: 'Blacklist', component: () => import('../views/Blacklist.vue')},
            {path: '/blacklist/:id', name: 'BlacklistDetail', component: () => import('../views/BlacklistDetail.vue')},

            {path: '/transaction', name: 'Transaction', component: () => import('../views/Transaction.vue')},
            {
                path: '/transaction/:id',
                name: 'TransactionDetail',
                component: () => import('../views/TransactionDetail.vue')
            },
            {path: '/notify/users', name: 'NotifyUser', component: () => import('../views/NotifyUser.vue')},
            {path: '/notify/users/:id', name: 'NotifyUserDetail', component: () => import('../views/NotifyUserDetail.vue')},

            {path: '/notify/records', name: 'NotifyRecord', component: () => import('../views/NotifyRecord.vue')},
            {path: '/notify/detail/:id', name: 'NotifyDetail', component: () => import('../views/NotifyDetail.vue')},
            {path: '/test', name: 'Test', component: () => import('../views/Test.vue')}
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
