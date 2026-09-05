import axios from 'axios';

/**
 * Centralized Axios HTTP client.
 *
 * Configured with baseURL (from VITE_API_BASE_URL or fallback to /api),
 * request interceptor for JWT token injection, and response interceptor for unified error parsing.
 */
const rawBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').trim().replace(/\/+$/, '');
let API_BASE_URL = '/api';

if (rawBaseUrl) {
  API_BASE_URL = rawBaseUrl.endsWith('/api') ? rawBaseUrl : `${rawBaseUrl}/api`;
} else if (typeof window !== 'undefined' && window.location.hostname.includes('onrender.com')) {
  API_BASE_URL = 'https://online-coding-practice-platform-2.onrender.com/api';
}

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request Interceptor: Attach JWT token if present in localStorage
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Uniform error formatting
axiosClient.interceptors.response.use(
  (response) => {
    // Return standard response data directly
    return response.data;
  },
  (error) => {
    let errorMessage = 'An unexpected error occurred. Please try again.';

    if (!error.response) {
      // Network / Connection Error
      errorMessage = 'Unable to connect to the server. Please check your network connection or verify the backend server is running.';
      return Promise.reject(new Error(errorMessage));
    }

    const { status, data } = error.response;

    if (data && data.message) {
      errorMessage = data.message;
    } else if (data && data.error) {
      errorMessage = data.error;
    }

    // Specific Status Handlers
    switch (status) {
      case 400:
        if (data.validationErrors) {
          const validationList = Object.entries(data.validationErrors)
            .map(([field, msg]) => `${field}: ${msg}`)
            .join(' | ');
          errorMessage = `Validation error: ${validationList}`;
        }
        break;
      case 401:
        // Handle token expiration or bad credentials
        if (localStorage.getItem('token')) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          // If expired during user session, redirect to login
          if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
            window.location.href = '/login?expired=true';
          }
        }
        break;
      case 403:
        errorMessage = 'Access Denied: You do not have permission to perform this action.';
        break;
      case 404:
        errorMessage = errorMessage || 'The requested resource could not be found.';
        break;
      case 500:
        errorMessage = errorMessage || 'Internal Server Error. Please contact support or try again later.';
        break;
      default:
        break;
    }

    const customError = new Error(errorMessage);
    customError.status = status;
    customError.data = data;
    return Promise.reject(customError);
  }
);

export default axiosClient;
