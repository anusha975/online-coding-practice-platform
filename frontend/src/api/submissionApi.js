import axiosClient from './axiosClient';

export const submissionApi = {
  submitCode: (submissionData) => axiosClient.post('/submissions', submissionData),
  getSubmissionById: (id) => axiosClient.get(`/submissions/${id}`),
  getMySubmissions: (params = {}) => axiosClient.get('/submissions/my', { params }),
  getProblemSubmissions: (problemId, params = {}) => axiosClient.get(`/submissions/problem/${problemId}`, { params }),
  getAllAdminSubmissions: (params = {}) => axiosClient.get('/admin/submissions', { params }),
};

export default submissionApi;
