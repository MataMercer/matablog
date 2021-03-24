import axios from 'axios';

axios.interceptors.request.use(
    (config) => {
        //CSRF Protection
        const elementToken = document.querySelector('meta[name="_csrf"]');
        const csrfToken = elementToken && elementToken.getAttribute('content');
        if (!csrfToken) {
            throw new Error(
                'Missing or unable to grab CSRF token from HTML page.'
            );
        }
        config.headers['X-XSRF-TOKEN'] = csrfToken;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);
