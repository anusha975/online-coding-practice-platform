import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { problemApi } from '../../api/problemApi';
import { testCaseApi } from '../../api/testCaseApi';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { Alert } from '../../components/common/Alert';
import { Modal } from '../../components/common/Modal';

export const AdminTestCases = () => {
  const { id } = useParams();
  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentTestCaseId, setCurrentTestCaseId] = useState(null);
  const [formData, setFormData] = useState({
    input: '',
    expectedOutput: '',
    hidden: true,
  });
  const [formLoading, setFormLoading] = useState(false);

  // Delete State
  const [deleteId, setDeleteId] = useState(null);

  const fetchProblemAndTestCases = async () => {
    try {
      setLoading(true);
      setError(null);
      const [probRes, tcRes] = await Promise.all([
        problemApi.getProblemById(id),
        testCaseApi.getTestCasesForProblem(id),
      ]);

      if (probRes && probRes.success) {
        setProblem(probRes.data);
      }
      if (tcRes && tcRes.success) {
        setTestCases(tcRes.data || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to load test cases.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProblemAndTestCases();
  }, [id]);

  const openCreateModal = () => {
    setIsEditing(false);
    setCurrentTestCaseId(null);
    setFormData({ input: '', expectedOutput: '', hidden: true });
    setIsModalOpen(true);
  };

  const openEditModal = (tc) => {
    setIsEditing(true);
    setCurrentTestCaseId(tc.id);
    setFormData({
      input: tc.input || '',
      expectedOutput: tc.expectedOutput || '',
      hidden: tc.hidden ?? true,
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setFormLoading(true);

    try {
      if (isEditing) {
        await testCaseApi.updateTestCase(currentTestCaseId, formData);
        setSuccess(`Test Case #${currentTestCaseId} updated.`);
      } else {
        await testCaseApi.createTestCase(id, formData);
        setSuccess('New test case added successfully.');
      }
      setIsModalOpen(false);
      fetchProblemAndTestCases();
    } catch (err) {
      setError(err.message || 'Failed to save test case.');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await testCaseApi.deleteTestCase(deleteId);
      setSuccess(`Test Case #${deleteId} deleted.`);
      setDeleteId(null);
      fetchProblemAndTestCases();
    } catch (err) {
      setError(err.message || 'Failed to delete test case.');
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading problem test cases..." />;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <Link to="/admin/problems" style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            &larr; Back to Problem Manager
          </Link>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, margin: '0.25rem 0 0 0' }}>
            Test Cases for: {problem?.title || `Problem #${id}`}
          </h1>
        </div>

        <Button variant="primary" onClick={openCreateModal}>
          + Add New Test Case
        </Button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}
      {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}

      {/* Test Cases Table */}
      {testCases.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', marginBottom: '1rem' }}>
            No test cases configured for this problem yet.
          </p>
          <Button variant="primary" onClick={openCreateModal}>+ Add First Test Case</Button>
        </div>
      ) : (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th style={{ width: '80px' }}># ID</th>
                <th>Input Data (stdin)</th>
                <th>Expected Output (stdout)</th>
                <th style={{ width: '120px' }}>Visibility</th>
                <th style={{ width: '160px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {testCases.map((tc) => (
                <tr key={tc.id}>
                  <td style={{ color: 'var(--text-muted)', fontWeight: 600 }}>#{tc.id}</td>
                  <td>
                    <pre style={{ background: '#0d1117', padding: '0.5rem', borderRadius: '4px', fontSize: '0.8rem', maxHeight: '80px', overflowY: 'auto' }}>
                      {tc.input}
                    </pre>
                  </td>
                  <td>
                    <pre style={{ background: '#0d1117', padding: '0.5rem', borderRadius: '4px', fontSize: '0.8rem', maxHeight: '80px', overflowY: 'auto' }}>
                      {tc.expectedOutput}
                    </pre>
                  </td>
                  <td>
                    <Badge text={tc.hidden ? 'HIDDEN' : 'SAMPLE'} type={tc.hidden ? 'wa' : 'ac'} />
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                      <Button size="sm" variant="secondary" onClick={() => openEditModal(tc)}>
                        Edit
                      </Button>
                      <Button size="sm" variant="danger" onClick={() => setDeleteId(tc.id)}>
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

      {/* Create / Edit Test Case Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={isEditing ? `Edit Test Case #${currentTestCaseId}` : 'Add New Test Case'}
        size="md"
      >
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div className="form-group">
            <label className="form-label">Input Data (Standard In) *</label>
            <textarea
              className="form-textarea"
              style={{ fontFamily: 'Fira Code, monospace', minHeight: '90px' }}
              value={formData.input}
              onChange={(e) => setFormData({ ...formData, input: e.target.value })}
              required
              placeholder="e.g. 5&#10;1 2 3 4 5"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Expected Output (Standard Out) *</label>
            <textarea
              className="form-textarea"
              style={{ fontFamily: 'Fira Code, monospace', minHeight: '90px' }}
              value={formData.expectedOutput}
              onChange={(e) => setFormData({ ...formData, expectedOutput: e.target.value })}
              required
              placeholder="e.g. 15"
            />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginTop: '0.25rem' }}>
            <input
              type="checkbox"
              id="hiddenCheck"
              checked={formData.hidden}
              onChange={(e) => setFormData({ ...formData, hidden: e.target.checked })}
              style={{ width: '18px', height: '18px', cursor: 'pointer' }}
            />
            <label htmlFor="hiddenCheck" style={{ fontSize: '0.9rem', cursor: 'pointer' }}>
              Hidden Test Case (Judge only — not visible in public problem descriptions)
            </label>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
            <Button variant="outline" type="button" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" isLoading={formLoading}>
              {isEditing ? 'Save Test Case' : 'Add Test Case'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!deleteId}
        onClose={() => setDeleteId(null)}
        title="Confirm Test Case Deletion"
      >
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
          Are you sure you want to permanently delete Test Case #{deleteId}?
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <Button variant="outline" onClick={() => setDeleteId(null)}>Cancel</Button>
          <Button variant="danger" onClick={handleDelete}>Delete</Button>
        </div>
      </Modal>
    </div>
  );
};

export default AdminTestCases;
