import axios from 'axios';

const apiClient = axios.create({
    baseURL: process.env.VUE_APP_BACKEND_URL || 'http://localhost:8080', // Use variáveis de ambiente para flexibilidade
    headers: {
        'Content-Type': 'application/json',
    },
});

export default apiClient;
