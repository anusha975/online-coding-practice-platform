import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';

export const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        {/* Brand Logo */}
        <Link to="/" className="navbar-brand">
          <div className="brand-icon">&lt;/&gt;</div>
          <span>CodePulse</span>
        </Link>

        {/* Navigation Links */}
        <ul className="nav-links">
          <li>
            <NavLink to="/problems" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Problems
            </NavLink>
          </li>
          {isAuthenticated && (
            <>
              <li>
                <NavLink to="/submissions" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                  Submissions
                </NavLink>
              </li>
              <li>
                <NavLink to="/profile" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                  Profile
                </NavLink>
              </li>
            </>
          )}
          {isAdmin && (
            <li>
              <NavLink to="/admin" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                <Badge text="Admin Panel" type="admin" />
              </NavLink>
            </li>
          )}
        </ul>

        {/* User / Auth Controls */}
        <div className="navbar-auth">
          {isAuthenticated ? (
            <>
              <div className="user-pill">
                <div className="user-avatar">
                  {user?.username?.charAt(0)?.toUpperCase() || 'U'}
                </div>
                <span>{user?.username}</span>
                {isAdmin && <Badge text="ADMIN" type="admin" />}
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                Logout
              </Button>
            </>
          ) : (
            <>
              <Link to="/login">
                <Button variant="outline" size="sm">
                  Sign In
                </Button>
              </Link>
              <Link to="/register">
                <Button variant="primary" size="sm">
                  Register
                </Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
