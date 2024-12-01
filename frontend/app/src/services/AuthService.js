import apiClient from './apiClient';

export const registerUser = async (registerData) => {
    return apiClient.post('/auth/register', registerData);
};
