import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { userApi } from '../../api/userApi';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';
import { Modal } from '../../components/common/Modal';

export const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Edit Role Modal
  const [editingUser, setEditingUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState('USER');
  const [roleLoading, setRoleLoading] = useState(false);

  // Delete Modal
  const [deleteUserObj, setDeleteUserObj] = useState(null);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await userApi.getAllUsers();
      if (res && res.success && res.data) {
        setUsers(res.data || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to load user directory.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const openRoleModal = (user) => {
    setEditingUser(user);
    setSelectedRole(user.role);
  };

  const handleRoleUpdate = async () => {
    if (!editingUser) return;
    try {
      setRoleLoading(true);
      await userApi.updateUser(editingUser.id, {
        username: editingUser.username,
        email: editingUser.email,
        role: selectedRole,
      });
      setSuccess(`Role updated for user '${editingUser.username}' to ${selectedRole}.`);
      setEditingUser(null);
      fetchUsers();
    } catch (err) {
      setError(err.message || 'Failed to update user role.');
    } finally {
      setRoleLoading(false);
    }
  };

  const handleDeleteUser = async () => {
    if (!deleteUserObj) return;
    try {
      await userApi.deleteUser(deleteUserObj.id);
      setSuccess(`User '${deleteUserObj.username}' deleted.`);
      setDeleteUserObj(null);
      fetchUsers();
    } catch (err) {
      setError(err.message || 'Failed to delete user.');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header */}
      <div>
        <Link to="/admin" style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
          &larr; Admin Dashboard
        </Link>
        <h1 style={{ fontSize: '2rem', fontWeight: 800, margin: '0.25rem 0 0 0' }}>
          User Directory &amp; Roles
        </h1>
        <p style={{ color: 'var(--text-secondary)' }}>
          Manage user credentials, view registrations, and configure administrator privileges.
        </p>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}

      {loading ? (
        <LoadingSpinner message="Loading user directory..." />
      ) : (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th style={{ width: '70px' }}>ID</th>
                <th>Username</th>
                <th>Email Address</th>
                <th style={{ width: '120px' }}>Role</th>
                <th>Created At</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td style={{ color: 'var(--text-muted)', fontWeight: 600 }}>#{u.id}</td>
                  <td style={{ fontWeight: 600 }}>{u.username}</td>
                  <td style={{ color: 'var(--text-secondary)' }}>{u.email}</td>
                  <td>
                    <Badge text={u.role} />
                  </td>
                  <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                    {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                      <Button size="sm" variant="outline" onClick={() => openRoleModal(u)}>
                        Change Role
                      </Button>
                      <Button size="sm" variant="danger" onClick={() => setDeleteUserObj(u)}>
                        Delete
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Change Role Modal */}
      <Modal
        isOpen={!!editingUser}
        onClose={() => setEditingUser(null)}
        title={`Change Role: ${editingUser?.username}`}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div className="form-group">
            <label className="form-label">Select Role Authority</label>
            <select
              className="form-select"
              value={selectedRole}
              onChange={(e) => setSelectedRole(e.target.value)}
            >
              <option value="USER">USER (Standard Member)</option>
              <option value="ADMIN">ADMIN (Full Privileges)</option>
            </select>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
            <Button variant="outline" onClick={() => setEditingUser(null)}>
              Cancel
            </Button>
            <Button variant="primary" onClick={handleRoleUpdate} isLoading={roleLoading}>
              Update Role
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete User Modal */}
      <Modal
        isOpen={!!deleteUserObj}
        onClose={() => setDeleteUserObj(null)}
        title="Confirm User Account Deletion"
      >
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
          Are you sure you want to delete user account <strong>{deleteUserObj?.username}</strong> ({deleteUserObj?.email})?
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <Button variant="outline" onClick={() => setDeleteUserObj(null)}>Cancel</Button>
          <Button variant="danger" onClick={handleDeleteUser}>Delete Account</Button>
        </div>
      </Modal>
    </div>
  );
};

export default AdminUsers;
