import React from 'react';

export const Alert = ({ type = 'info', message, children, className = '', onClose }) => {
  const alertClass = `alert alert-${type} ${className}`;

  if (!message && !children) return null;

  return (
    <div className={alertClass}>
      <div style={{ flex: 1 }}>{message || children}</div>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          style={{
            background: 'none',
            border: 'none',
            color: 'inherit',
            cursor: 'pointer',
            fontSize: '1rem',
          }}
        >
          &times;
        </button>
      )}
    </div>
  );
};

export default Alert;
