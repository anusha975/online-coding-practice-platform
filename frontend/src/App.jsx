import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import ProtectedRoute from './components/auth/ProtectedRoute';
import AdminRoute from './components/auth/AdminRoute';
import ErrorBoundary from './components/common/ErrorBoundary';

// User Pages
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProblemListPage from './pages/ProblemListPage';
import ProblemDetailPage from './pages/ProblemDetailPage';
import SubmissionHistoryPage from './pages/SubmissionHistoryPage';
import ProfilePage from './pages/ProfilePage';

// Admin Pages
import AdminDashboard from './pages/Admin/AdminDashboard';
import AdminProblems from './pages/Admin/AdminProblems';
import AdminTestCases from './pages/Admin/AdminTestCases';
import AdminUsers from './pages/Admin/AdminUsers';
import AdminSubmissions from './pages/Admin/AdminSubmissions';

export const App = () => {
  return (
    <div className="app-container">
      <Navbar />

      <main className="main-content">
        <ErrorBoundary>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/problems" element={<ProblemListPage />} />
            <Route path="/problems/:id" element={<ProblemDetailPage />} />

            {/* Authenticated User Routes */}
            <Route
              path="/submissions"
              element={
                <ProtectedRoute>
                  <SubmissionHistoryPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            {/* Administrator Routes */}
            <Route
              path="/admin"
              element={
                <AdminRoute>
                  <AdminDashboard />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/problems"
              element={
                <AdminRoute>
                  <AdminProblems />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/problems/:id/testcases"
              element={
                <AdminRoute>
                  <AdminTestCases />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/users"
              element={
                <AdminRoute>
                  <AdminUsers />
                </AdminRoute>
              }
            />
            <Route
              path="/admin/submissions"
              element={
                <AdminRoute>
                  <AdminSubmissions />
                </AdminRoute>
              }
            />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </ErrorBoundary>
      </main>

      <Footer />
    </div>
  );
};

export default App;
