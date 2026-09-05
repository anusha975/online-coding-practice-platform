import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { statsApi } from '../api/statsApi';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';

export const ProfilePage = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await statsApi.getMyStats();
        if (res && res.success && res.data) {
          setStats(res.data);
        }
      } catch (err) {
        setError(err.message || 'Failed to load user analytics.');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return <LoadingSpinner message="Calculating problem solving analytics..." />;
  }

  const easyPercent = stats?.totalEasyProblems > 0 ? (stats.easySolved / stats.totalEasyProblems) * 100 : 0;
  const mediumPercent = stats?.totalMediumProblems > 0 ? (stats.mediumSolved / stats.totalMediumProblems) * 100 : 0;
  const hardPercent = stats?.totalHardProblems > 0 ? (stats.hardSolved / stats.totalHardProblems) * 100 : 0;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      {/* Profile Header */}
      <div
        className="card"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '1.5rem',
          background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(17, 23, 38, 0.8) 100%)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div
            style={{
              width: '64px',
              height: '64px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, var(--primary), #ec4899)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '1.75rem',
              fontWeight: 800,
              color: 'white',
              boxShadow: '0 0 15px rgba(99, 102, 241, 0.4)',
            }}
          >
            {user?.username?.charAt(0)?.toUpperCase() || 'U'}
          </div>

          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 800, margin: 0 }}>{user?.username}</h2>
              <Badge text={user?.role} />
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '0.25rem' }}>
              {user?.email} &bull; Member #{user?.id}
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <Link to="/submissions">
            <Button variant="outline">View Submissions</Button>
          </Link>
          <Link to="/problems">
            <Button variant="primary">Practice Problems</Button>
          </Link>
        </div>
      </div>

      {error && <Alert type="error" message={error} />}

      {stats && (
        <>
          {/* Top Metrics Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem' }}>
            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)' }}>
                🏆
              </div>
              <div className="stat-info">
                <div className="stat-value" style={{ color: 'var(--success)' }}>
                  {stats.totalSolved}
                </div>
                <div className="stat-label">Problems Solved</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary-light)' }}>
                📝
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.totalSubmissions}</div>
                <div className="stat-label">Total Submissions</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(56, 189, 248, 0.15)', color: 'var(--info)' }}>
                ✅
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.acceptedSubmissions}</div>
                <div className="stat-label">Accepted Solutions</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'rgba(245, 158, 11, 0.15)', color: 'var(--warning)' }}>
                🎯
              </div>
              <div className="stat-info">
                <div className="stat-value">{stats.acceptanceRate}%</div>
                <div className="stat-label">Acceptance Rate</div>
              </div>
            </div>
          </div>

          {/* Solved Problems Breakdown */}
          <Card title="Difficulty Progress">
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', padding: '0.5rem 0' }}>
              {/* Easy */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.9rem' }}>
                  <span style={{ color: '#34d399', fontWeight: 600 }}>Easy</span>
                  <span style={{ color: 'var(--text-secondary)' }}>
                    <strong>{stats.easySolved}</strong> / {stats.totalEasyProblems} solved ({easyPercent.toFixed(1)}%)
                  </span>
                </div>
                <div style={{ width: '100%', height: '10px', background: 'var(--bg-tertiary)', borderRadius: 'var(--radius-full)', overflow: 'hidden' }}>
                  <div style={{ width: `${easyPercent}%`, height: '100%', background: 'linear-gradient(90deg, #10b981, #34d399)', borderRadius: 'var(--radius-full)' }} />
                </div>
              </div>

              {/* Medium */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.9rem' }}>
                  <span style={{ color: '#fbbf24', fontWeight: 600 }}>Medium</span>
                  <span style={{ color: 'var(--text-secondary)' }}>
                    <strong>{stats.mediumSolved}</strong> / {stats.totalMediumProblems} solved ({mediumPercent.toFixed(1)}%)
                  </span>
                </div>
                <div style={{ width: '100%', height: '10px', background: 'var(--bg-tertiary)', borderRadius: 'var(--radius-full)', overflow: 'hidden' }}>
                  <div style={{ width: `${mediumPercent}%`, height: '100%', background: 'linear-gradient(90deg, #f59e0b, #fbbf24)', borderRadius: 'var(--radius-full)' }} />
                </div>
              </div>

              {/* Hard */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.9rem' }}>
                  <span style={{ color: '#f87171', fontWeight: 600 }}>Hard</span>
                  <span style={{ color: 'var(--text-secondary)' }}>
                    <strong>{stats.hardSolved}</strong> / {stats.totalHardProblems} solved ({hardPercent.toFixed(1)}%)
                  </span>
                </div>
                <div style={{ width: '100%', height: '10px', background: 'var(--bg-tertiary)', borderRadius: 'var(--radius-full)', overflow: 'hidden' }}>
                  <div style={{ width: `${hardPercent}%`, height: '100%', background: 'linear-gradient(90deg, #ef4444, #f87171)', borderRadius: 'var(--radius-full)' }} />
                </div>
              </div>
            </div>
          </Card>
        </>
      )}
    </div>
  );
};

export default ProfilePage;
