import React from 'react';

export const Badge = ({ children, type = 'default', text, className = '' }) => {
  let badgeClass = 'badge';

  const lower = (text || children || '').toString().toUpperCase();

  switch (lower) {
    case 'EASY':
      badgeClass += ' badge-easy';
      break;
    case 'MEDIUM':
      badgeClass += ' badge-medium';
      break;
    case 'HARD':
      badgeClass += ' badge-hard';
      break;
    case 'ACCEPTED':
      badgeClass += ' badge-ac';
      break;
    case 'WRONG_ANSWER':
      badgeClass += ' badge-wa';
      break;
    case 'PENDING':
      badgeClass += ' badge-pending';
      break;
    case 'RUNNING':
      badgeClass += ' badge-running';
      break;
    case 'COMPILATION_ERROR':
    case 'RUNTIME_ERROR':
      badgeClass += ' badge-error';
      break;
    case 'TIME_LIMIT_EXCEEDED':
      badgeClass += ' badge-tle';
      break;
    case 'ADMIN':
      badgeClass += ' badge-admin';
      break;
    case 'USER':
      badgeClass += ' badge-user';
      break;
    case 'JAVA':
      badgeClass += ' badge-java';
      break;
    case 'PYTHON':
      badgeClass += ' badge-python';
      break;
    default:
      if (type) badgeClass += ` badge-${type}`;
      break;
  }

  return <span className={`${badgeClass} ${className}`}>{children || text}</span>;
};

export default Badge;
