import React from 'react';
import { Button } from './Button';

/**
 * Global React Error Boundary to catch render crashes gracefully.
 */
export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Unhandled React error captured by ErrorBoundary:', error, errorInfo);
    this.setState({ errorInfo });
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
    window.location.href = '/';
  };

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: '80vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem',
          }}
        >
          <div
            className="card"
            style={{
              maxWidth: '560px',
              width: '100%',
              textAlign: 'center',
              padding: '2.5rem',
              border: '1px solid var(--danger-border)',
              boxShadow: 'var(--shadow-lg)',
            }}
          >
            <div style={{ fontSize: '3.5rem', marginBottom: '1rem' }}>⚠️</div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.75rem', color: 'var(--text-primary)' }}>
              Something went wrong
            </h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', fontSize: '0.95rem' }}>
              An unexpected client-side error occurred. You can reload the application or return to the problem catalog.
            </p>

            {this.state.error && (
              <div
                style={{
                  background: '#090d13',
                  padding: '0.85rem',
                  borderRadius: 'var(--radius-md)',
                  color: '#f87171',
                  fontFamily: 'Fira Code, monospace',
                  fontSize: '0.8rem',
                  textAlign: 'left',
                  marginBottom: '1.5rem',
                  overflowX: 'auto',
                  border: '1px solid #30363d',
                }}
              >
                {this.state.error.toString()}
              </div>
            )}

            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
              <Button variant="outline" onClick={() => window.location.reload()}>
                ↻ Reload Page
              </Button>
              <Button variant="primary" onClick={this.handleReset}>
                Go to Home
              </Button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
