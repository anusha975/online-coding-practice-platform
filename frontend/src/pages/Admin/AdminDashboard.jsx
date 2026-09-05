import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { statsApi } from '../../api/statsApi';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';

export const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchAdminStats = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await statsApi.getAdminStats();
        if (res && res.success && res.data) {
          setStats(res.data);
        }
      } catch (err) {
        setError(err.message || 'Failed to load administrator statistics.');
      } finally {
        setLoading(false);
      }
    };

    fetchAdminStats();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Loading administrator dashboard..." />;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      {/* Admin Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 800, letterSpacing: '-0.025em', margin: 0 }}>
            Administrator Dashboard
          </h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
            Manage coding challenges, test cases, users, and audit platform submissions.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <Link to="/admin/problems">
            <Button variant="primary">+ Manage Problems</Button>
          </Link>
          <Link to="/admin/users">
            <Button variant="secondary">Manage Users</Button>
          </Link>
        </div>
      </div>

      {error && <Alert type="error" message={error} />}

      {stats && (
        <>
          {/* Metrics Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem' }}>
            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary-light)' }}>
                👥
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.totalUsers}</div>
                <div className="stat-label">Registered Users</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)' }}>
                🧩
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.totalProblems}</div>
                <div className="stat-label">Coding Problems</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(56, 189, 248, 0.15)', color: 'var(--info)' }}>
                🧪
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.totalTestCases}</div>
                <div className="stat-label">Total Test Cases</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(245, 158, 11, 0.15)', color: 'var(--warning)' }}>
                ⚡
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.totalSubmissions}</div>
                <div className="stat-label">Evaluated Submissions</div>
              </div>
            </div>
          </div>

          {/* Quick Management Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem' }}>
            <Card title="Problems &amp; Content Management">
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                Create, update, or remove coding challenges. Configure time/memory limits, constraints, and test cases.
              </p>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <Link to="/admin/problems">
                  <Button variant="primary" size="sm">Problem Manager &rarr;</Button>
                </Link>
              </div>
            </Card>

            <Card title="Users &amp; Permissions">
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                View all registered accounts, change roles between USER and ADMIN, and manage user accounts.
              </p>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <Link to="/admin/users">
                  <Button variant="secondary" size="sm">User Directory &rarr;</Button>
                </Link>
              </div>
            </Card>

            <Card title="Global Submission Audit">
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                Inspect real-time code executions across all users, analyze compiler/runtime failures, and review source codes.
              </p>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <Link to="/admin/submissions">
                  <Button variant="outline" size="sm">Submission Logs &rarr;</Button>
                </Link>
              </div>
            </Card>
          </div>
        </>
      )}
    </div>
  );
};

export default AdminDashboard;
