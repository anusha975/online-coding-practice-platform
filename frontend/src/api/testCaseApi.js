import axiosClient from './axiosClient';

export const testCaseApi = {
  getTestCasesForProblem: (problemId) => axiosClient.get(`/admin/problems/${problemId}/testcases`),
  createTestCase: (problemId, data) => axiosClient.post(`/admin/problems/${problemId}/testcases`, data),
  updateTestCase: (id, data) => axiosClient.put(`/admin/testcases/${id}`, data),
  deleteTestCase: (id) => axiosClient.delete(`/admin/testcases/${id}`),
};

export default testCaseApi;
