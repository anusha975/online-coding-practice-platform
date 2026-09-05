import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Alert } from '../components/common/Alert';

export const LoginPage = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/problems';
  const queryParams = new URLSearchParams(location.search);
  const isExpired = queryParams.get('expired') === 'true';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!usernameOrEmail.trim() || !password) {
      setError('Please enter both your username/email and password.');
      return;
    }

    try {
      setIsLoading(true);
      await login(usernameOrEmail.trim(), password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.message || 'Login failed. Please verify your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '450px', margin: '3rem auto 0 auto' }}>
      <Card title="Sign in to CodePulse">
        {isExpired && !error && (
          <Alert type="warning" message="Your session has expired. Please sign in again." />
        )}

        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="usernameOrEmail">
              Username or Email
            </label>
            <input
              id="usernameOrEmail"
              type="text"
              className="form-input"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              placeholder="e.g. alex or alex@example.com"
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <Button type="submit" variant="primary" size="lg" isLoading={isLoading} style={{ width: '100%', marginTop: '0.5rem' }}>
            Sign In
          </Button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ fontWeight: 600 }}>
            Create one for free
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;
