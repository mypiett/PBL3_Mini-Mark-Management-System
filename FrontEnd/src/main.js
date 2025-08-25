import './assets/main.css';

import { createApp } from 'vue';
import App from './App.vue';
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faTwitter, faFacebook } from '@fortawesome/free-brands-svg-icons';
import { faBookmark } from '@fortawesome/free-solid-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import router from './router/index.js';
library.add(faTwitter, faFacebook, faBookmark, fas);
import { faEnvelope, faLock } from '@fortawesome/free-solid-svg-icons';

library.add(faEnvelope, faLock);

const app = createApp(App);
app.component('font-awesome-icon', FontAwesomeIcon);

// createApp(App)
app.use(router);
app.mount('#app');
