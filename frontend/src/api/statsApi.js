import axiosClient from './axiosClient';

export const statsApi = {
  getMyStats: () => axiosClient.get('/users/me/stats'),
  getUserStats: (userId) => axiosClient.get(`/users/${userId}/stats`),
  getAdminStats: () => axiosClient.get('/admin/stats'),
};

export default statsApi;
