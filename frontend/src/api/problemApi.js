import axiosClient from './axiosClient';

export const problemApi = {
  getProblems: (params = {}) => axiosClient.get('/problems', { params }),
  getCategories: () => axiosClient.get('/problems/categories'),
  getProblemById: (id) => axiosClient.get(`/problems/${id}`),
  getSampleTestCases: (problemId) => axiosClient.get(`/problems/${problemId}/testcases/sample`),
  createProblem: (data) => axiosClient.post('/problems', data),
  updateProblem: (id, data) => axiosClient.put(`/problems/${id}`, data),
  deleteProblem: (id) => axiosClient.delete(`/problems/${id}`),
};

export default problemApi;
