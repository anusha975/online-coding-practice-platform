import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';

export const HomePage = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '3rem', padding: '1rem 0' }}>
      {/* Hero Section */}
      <div
        style={{
          textAlign: 'center',
          padding: '4rem 1.5rem',
          background: 'linear-gradient(180deg, rgba(99, 102, 241, 0.12) 0%, transparent 100%)',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-color)',
        }}
      >
        <div
          style={{
            display: 'inline-block',
            padding: '0.35rem 1rem',
            background: 'rgba(99, 102, 241, 0.15)',
            color: 'var(--primary-light)',
            borderRadius: 'var(--radius-full)',
            fontSize: '0.85rem',
            fontWeight: 600,
            marginBottom: '1.25rem',
            border: '1px solid rgba(99, 102, 241, 0.3)',
          }}
        >
          🚀 Level Up Your Problem Solving Skills
        </div>
        <h1
          style={{
            fontSize: '3rem',
            fontWeight: 800,
            letterSpacing: '-0.03em',
            lineHeight: 1.15,
            marginBottom: '1.25rem',
          }}
        >
          Master Data Structures &amp; <br />
          <span style={{ background: 'linear-gradient(135deg, #818cf8, #ec4899)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Algorithms in Java &amp; Python
          </span>
        </h1>
        <p
          style={{
            maxWidth: '650px',
            margin: '0 auto 2rem auto',
            color: 'var(--text-secondary)',
            fontSize: '1.1rem',
          }}
        >
          Practice algorithmic challenges, test solutions in an isolated execution engine, and track your progress with analytics.
        </p>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', flexWrap: 'wrap' }}>
          <Link to="/problems">
            <Button variant="primary" size="lg">
              Browse Problems &rarr;
            </Button>
          </Link>
          {!isAuthenticated ? (
            <Link to="/register">
              <Button variant="outline" size="lg">
                Create Free Account
              </Button>
            </Link>
          ) : (
            <Link to="/profile">
              <Button variant="secondary" size="lg">
                View My Profile ({user?.username})
              </Button>
            </Link>
          )}
        </div>
      </div>

      {/* Feature Highlights Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem' }}>
        <Card>
          <div style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>⚡</div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
            Fast Code Execution
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.925rem' }}>
            Multi-threaded judge evaluating Java and Python code against public and hidden test cases with timeouts and memory monitoring.
          </p>
        </Card>

        <Card>
          <div style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>📊</div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
            Analytics &amp; Solved Tracker
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.925rem' }}>
            Track your submission history, distinct problem count breakdown by difficulty (Easy, Medium, Hard), and acceptance rates.
          </p>
        </Card>

        <Card>
          <div style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>🛡️</div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
            Secure &amp; Isolated
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.925rem' }}>
            Process-isolated sandbox environments protecting server integrity with strict CPU time limits and output buffer caps.
          </p>
        </Card>
      </div>
    </div>
  );
};

export default HomePage;
