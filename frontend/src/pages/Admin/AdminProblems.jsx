import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { problemApi } from '../../api/problemApi';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';
import { Modal } from '../../components/common/Modal';

export const AdminProblems = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Modal State: Create / Edit
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentProblemId, setCurrentProblemId] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    difficulty: 'EASY',
    category: 'Arrays',
    description: '',
    constraints: '',
    inputFormat: '',
    outputFormat: '',
    sampleInput: '',
    sampleOutput: '',
    timeLimitMs: 2000,
    memoryLimitMb: 256,
  });
  const [formLoading, setFormLoading] = useState(false);

  // Delete Confirmation State
  const [deleteId, setDeleteId] = useState(null);

  const fetchProblems = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await problemApi.getProblems({ page: 0, size: 100, sortBy: 'id', sortDir: 'asc' });
      if (res && res.success && res.data) {
        setProblems(res.data.content || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to load problems.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProblems();
  }, []);

  const openCreateModal = () => {
    setIsEditing(false);
    setCurrentProblemId(null);
    setFormData({
      title: '',
      difficulty: 'EASY',
      category: 'Arrays',
      description: '',
      constraints: '',
      inputFormat: '',
      outputFormat: '',
      sampleInput: '',
      sampleOutput: '',
      timeLimitMs: 2000,
      memoryLimitMb: 256,
    });
    setIsModalOpen(true);
  };

  const openEditModal = (problem) => {
    setIsEditing(true);
    setCurrentProblemId(problem.id);
    setFormData({
      title: problem.title || '',
      difficulty: problem.difficulty || 'EASY',
      category: problem.category || 'General',
      description: problem.description || '',
      constraints: problem.constraints || '',
      inputFormat: problem.inputFormat || '',
      outputFormat: problem.outputFormat || '',
      sampleInput: problem.sampleInput || '',
      sampleOutput: problem.sampleOutput || '',
      timeLimitMs: problem.timeLimitMs || 2000,
      memoryLimitMb: problem.memoryLimitMb || 256,
    });
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setFormLoading(true);

    try {
      if (isEditing) {
        await problemApi.updateProblem(currentProblemId, formData);
        setSuccess(`Problem #${currentProblemId} updated successfully.`);
      } else {
        await problemApi.createProblem(formData);
        setSuccess('New problem created successfully.');
      }
      setIsModalOpen(false);
      fetchProblems();
    } catch (err) {
      setError(err.message || 'Operation failed.');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await problemApi.deleteProblem(deleteId);
      setSuccess(`Problem #${deleteId} deleted successfully.`);
      setDeleteId(null);
      fetchProblems();
    } catch (err) {
      setError(err.message || 'Failed to delete problem.');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Link to="/admin" style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              &larr; Admin Dashboard
            </Link>
          </div>
          <h1 style={{ fontSize: '2rem', fontWeight: 800, margin: '0.25rem 0 0 0' }}>
            Problem Management
          </h1>
        </div>

        <Button variant="primary" onClick={openCreateModal}>
          + Create New Problem
        </Button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Problems Table */}
      {loading ? (
        <LoadingSpinner message="Loading problem catalog..." />
      ) : (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th style={{ width: '70px' }}>ID</th>
                <th>Title</th>
                <th>Category</th>
                <th>Difficulty</th>
                <th>Limits</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {problems.map((prob) => (
                <tr key={prob.id}>
                  <td style={{ color: 'var(--text-muted)', fontWeight: 600 }}>#{prob.id}</td>
                  <td style={{ fontWeight: 600 }}>{prob.title}</td>
                  <td>{prob.category}</td>
                  <td><Badge text={prob.difficulty} /></td>
                  <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                    {prob.timeLimitMs}ms / {prob.memoryLimitMb}MB
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                      <Link to={`/admin/problems/${prob.id}/testcases`}>
                        <Button size="sm" variant="outline">Test Cases</Button>
                      </Link>
                      <Button size="sm" variant="secondary" onClick={() => openEditModal(prob)}>
                        Edit
                      </Button>
                      <Button size="sm" variant="danger" onClick={() => setDeleteId(prob.id)}>
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

      {/* Create / Edit Problem Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={isEditing ? `Edit Problem #${currentProblemId}` : 'Create New Coding Problem'}
        size="lg"
      >
        <form onSubmit={handleFormSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Problem Title *</label>
              <input
                type="text"
                className="form-input"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                required
                placeholder="e.g. Binary Tree Inorder Traversal"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Difficulty *</label>
              <select
                className="form-select"
                value={formData.difficulty}
                onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
              >
                <option value="EASY">Easy</option>
                <option value="MEDIUM">Medium</option>
                <option value="HARD">Hard</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Category *</label>
              <input
                type="text"
                className="form-input"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                required
                placeholder="e.g. Trees"
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Description (Markdown / Text) *</label>
            <textarea
              className="form-textarea"
              style={{ minHeight: '120px' }}
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              required
              placeholder="Detailed problem statement..."
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Input Format</label>
              <textarea
                className="form-textarea"
                style={{ minHeight: '70px' }}
                value={formData.inputFormat}
                onChange={(e) => setFormData({ ...formData, inputFormat: e.target.value })}
                placeholder="Description of stdin format..."
              />
            </div>

            <div className="form-group">
              <label className="form-label">Output Format</label>
              <textarea
                className="form-textarea"
                style={{ minHeight: '70px' }}
                value={formData.outputFormat}
                onChange={(e) => setFormData({ ...formData, outputFormat: e.target.value })}
                placeholder="Description of stdout format..."
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Constraints</label>
            <input
              type="text"
              className="form-input"
              value={formData.constraints}
              onChange={(e) => setFormData({ ...formData, constraints: e.target.value })}
              placeholder="e.g. 1 <= N <= 10^5, -10^9 <= A[i] <= 10^9"
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Sample Input</label>
              <textarea
                className="form-textarea"
                style={{ minHeight: '70px', fontFamily: 'Fira Code, monospace' }}
                value={formData.sampleInput}
                onChange={(e) => setFormData({ ...formData, sampleInput: e.target.value })}
                placeholder="Sample input data..."
              />
            </div>

            <div className="form-group">
              <label className="form-label">Sample Output</label>
              <textarea
                className="form-textarea"
                style={{ minHeight: '70px', fontFamily: 'Fira Code, monospace' }}
                value={formData.sampleOutput}
                onChange={(e) => setFormData({ ...formData, sampleOutput: e.target.value })}
                placeholder="Expected sample output..."
              />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Time Limit (ms)</label>
              <input
                type="number"
                className="form-input"
                value={formData.timeLimitMs}
                onChange={(e) => setFormData({ ...formData, timeLimitMs: Number(e.target.value) })}
                min={100}
                max={10000}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Memory Limit (MB)</label>
              <input
                type="number"
                className="form-input"
                value={formData.memoryLimitMb}
                onChange={(e) => setFormData({ ...formData, memoryLimitMb: Number(e.target.value) })}
                min={16}
                max={1024}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
            <Button variant="outline" type="button" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" isLoading={formLoading}>
              {isEditing ? 'Save Changes' : 'Create Problem'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        title="Confirm Problem Deletion"
      >
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
          Are you sure you want to permanently delete Problem #{deleteId}? This will remove all associated test cases and submissions.
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <Button variant="outline" onClick={() => setDeleteId(null)}>Cancel</Button>
          <Button variant="danger" onClick={handleDelete}>Confirm Delete</Button>
        </div>
      </Modal>
    </div>
  );
};

export default AdminProblems;
