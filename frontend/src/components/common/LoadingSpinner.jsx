import React from 'react';

export const LoadingSpinner = ({ size = 'md', message = 'Loading...' }) => {
  const sizeClass = size === 'sm' ? 'spinner-sm' : size === 'lg' ? 'spinner-lg' : '';

  return (
    <div className="loading-container">
      <div className={`spinner ${sizeClass}`} />
      {message && <p>{message}</p>}
    </div>
  );
};

export default LoadingSpinner;
