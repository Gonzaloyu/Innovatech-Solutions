import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import { MiAuthLib } from 'libreria_vue_auth'

const app = createApp(App)

app.use(MiAuthLib, {
  domain: 'dev-8z6khd0386cfag6s.us.auth0.com', 
  clientId: 'uAsxc7Uyc9pD4PKXVWnRHHQP9F1Y9tuu',
  authorizationParams: {
    redirect_uri: window.location.origin
  }
})

app.mount('#app') 