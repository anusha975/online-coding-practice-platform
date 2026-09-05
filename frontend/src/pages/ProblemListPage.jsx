import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { problemApi } from '../api/problemApi';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';

export const ProblemListPage = () => {
  // Data state
  const [problems, setProblems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filter state
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [category, setCategory] = useState('');

  // Sorting state
  const [sortBy, setSortBy] = useState('id');
  const [sortDir, setSortDir] = useState('asc');

  // Loading & Error states
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 1. Fetch available categories on mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await problemApi.getCategories();
        if (res && res.success && Array.isArray(res.data)) {
          setCategories(res.data);
        }
      } catch (err) {
        console.error('Failed to load categories:', err);
      }
    };
    fetchCategories();
  }, []);

  // 2. Fetch paginated, filtered, and sorted problems from database
  const fetchProblems = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        page: currentPage,
        size: pageSize,
        search: search.trim() || undefined,
        difficulty: difficulty || undefined,
        category: category.trim() || undefined,
        sortBy: sortBy || 'id',
        sortDir: sortDir || 'asc',
      };

      const res = await problemApi.getProblems(params);
      if (res && res.success && res.data) {
        setProblems(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      setError(err.message || 'Failed to load problems from server.');
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize, search, difficulty, category, sortBy, sortDir]);

  useEffect(() => {
    fetchProblems();
  }, [fetchProblems]);

  // Handle Search Submission
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setSearch(searchInput.trim());
    setCurrentPage(0);
  };

  // Clear Search
  const handleClearSearch = () => {
    setSearchInput('');
    setSearch('');
    setCurrentPage(0);
  };

  // Handle Difficulty Filter
  const handleDifficultyChange = (newDifficulty) => {
    setDifficulty(newDifficulty);
    setCurrentPage(0);
  };

  // Handle Category Filter
  const handleCategoryChange = (newCategory) => {
    setCategory(newCategory);
    setCurrentPage(0);
  };

  // Handle Page Size Change
  const handlePageSizeChange = (newSize) => {
    setPageSize(Number(newSize));
    setCurrentPage(0);
  };

  // Handle Sorting Column Click
  const handleSortToggle = (column) => {
    if (sortBy === column) {
      setSortDir((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortBy(column);
      setSortDir('asc');
    }
    setCurrentPage(0);
  };

  // Reset all filters
  const handleResetFilters = () => {
    setSearchInput('');
    setSearch('');
    setDifficulty('');
    setCategory('');
    setSortBy('id');
    setSortDir('asc');
    setCurrentPage(0);
  };

  const hasActiveFilters = Boolean(search || difficulty || category);

  // Generate page numbers array for pagination bar
  const getPageNumbers = () => {
    const pages = [];
    const maxVisible = 5;
    let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages, start + maxVisible);

    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }

    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  };

  const startRecord = totalElements === 0 ? 0 : currentPage * pageSize + 1;
  const endRecord = Math.min((currentPage + 1) * pageSize, totalElements);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
      {/* Header Section */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '2.1rem', fontWeight: 800, letterSpacing: '-0.025em', marginBottom: '0.4rem' }}>
            Problem Catalog
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Explore coding challenges, filter by difficulty and category, and master algorithmic problem solving.
          </p>
        </div>

        {/* Global Stats Summary Pill */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '1rem',
            background: 'var(--bg-card)',
            padding: '0.6rem 1.25rem',
            borderRadius: 'var(--radius-md)',
            border: '1px solid var(--border-color)',
          }}
        >
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700 }}>Total Problems</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--primary-light)' }}>{totalElements}</div>
          </div>
          <div style={{ width: '1px', height: '28px', background: 'var(--border-color)' }} />
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700 }}>Categories</div>
            <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--purple)' }}>{categories.length || '—'}</div>
          </div>
        </div>
      </div>

      {/* Control Panel: Search & Multi-Filter Controls */}
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
          background: 'var(--bg-card)',
          padding: '1.25rem 1.5rem',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-color)',
          boxShadow: 'var(--shadow-sm)',
        }}
      >
        {/* Row 1: Search Bar & Primary Actions */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', alignItems: 'center' }}>
          {/* Search Input Box */}
          <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '0.5rem', flex: '1 1 320px', position: 'relative' }}>
            <input
              id="problem-search-input"
              type="text"
              className="form-input"
              placeholder="Search problems by title, keyword, description..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              style={{ paddingRight: searchInput ? '2.5rem' : '0.85rem' }}
            />
            {searchInput && (
              <button
                type="button"
                onClick={handleClearSearch}
                title="Clear search"
                style={{
                  position: 'absolute',
                  right: '90px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'transparent',
                  border: 'none',
                  color: 'var(--text-muted)',
                  cursor: 'pointer',
                  fontSize: '1.1rem',
                  padding: '0.2rem 0.5rem',
                }}
              >
                ✕
              </button>
            )}
            <Button id="problem-search-btn" type="submit" variant="primary">
              Search
            </Button>
          </form>

          {/* Quick Clear All Filters */}
          {hasActiveFilters && (
            <Button size="md" variant="outline" onClick={handleResetFilters}>
              Reset Filters ✕
            </Button>
          )}
        </div>

        {/* Row 2: Dropdowns (Difficulty, Category, Sort, Page Size) */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
            gap: '0.75rem',
            alignItems: 'center',
            paddingTop: '0.75rem',
            borderTop: '1px solid var(--border-subtle)',
          }}
        >
          {/* Difficulty Dropdown */}
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.3rem' }}>
              DIFFICULTY
            </label>
            <select
              id="difficulty-select"
              className="form-input"
              value={difficulty}
              onChange={(e) => handleDifficultyChange(e.target.value)}
              style={{ cursor: 'pointer', background: 'var(--bg-secondary)' }}
            >
              <option value="">All Difficulties</option>
              <option value="EASY">🟢 Easy</option>
              <option value="MEDIUM">🟡 Medium</option>
              <option value="HARD">🔴 Hard</option>
            </select>
          </div>

          {/* Category Dropdown */}
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.3rem' }}>
              CATEGORY
            </label>
            <select
              id="category-select"
              className="form-input"
              value={category}
              onChange={(e) => handleCategoryChange(e.target.value)}
              style={{ cursor: 'pointer', background: 'var(--bg-secondary)' }}
            >
              <option value="">All Categories</option>
              {categories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
          </div>

          {/* Sort By Dropdown */}
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.3rem' }}>
              SORT BY
            </label>
            <div style={{ display: 'flex', gap: '0.4rem' }}>
              <select
                id="sort-by-select"
                className="form-input"
                value={sortBy}
                onChange={(e) => {
                  setSortBy(e.target.value);
                  setCurrentPage(0);
                }}
                style={{ cursor: 'pointer', background: 'var(--bg-secondary)', flex: 1 }}
              >
                <option value="id">Problem ID</option>
                <option value="title">Title (A-Z)</option>
                <option value="difficulty">Difficulty</option>
                <option value="category">Category</option>
                <option value="createdAt">Date Created</option>
              </select>
              <Button
                size="sm"
                variant="outline"
                title={sortDir === 'asc' ? 'Ascending Order' : 'Descending Order'}
                onClick={() => setSortDir((prev) => (prev === 'asc' ? 'desc' : 'asc'))}
                style={{ padding: '0 0.75rem', fontSize: '1rem' }}
              >
                {sortDir === 'asc' ? '↑' : '↓'}
              </Button>
            </div>
          </div>

          {/* Page Size Dropdown */}
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.3rem' }}>
              PER PAGE
            </label>
            <select
              id="page-size-select"
              className="form-input"
              value={pageSize}
              onChange={(e) => handlePageSizeChange(e.target.value)}
              style={{ cursor: 'pointer', background: 'var(--bg-secondary)' }}
            >
              <option value="10">10 problems</option>
              <option value="25">25 problems</option>
              <option value="50">50 problems</option>
            </select>
          </div>
        </div>

        {/* Active Filter Tags */}
        {hasActiveFilters && (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', alignItems: 'center', paddingTop: '0.5rem' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontWeight: 600 }}>Active Filters:</span>
            {search && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  background: 'var(--bg-tertiary)',
                  border: '1px solid var(--primary)',
                  color: 'var(--primary-light)',
                  padding: '0.2rem 0.6rem',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '0.8rem',
                }}
              >
                Search: "{search}"
                <button
                  type="button"
                  onClick={handleClearSearch}
                  style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontWeight: 700 }}
                >
                  ✕
                </button>
              </span>
            )}
            {difficulty && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  background: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-color)',
                  color: 'var(--text-primary)',
                  padding: '0.2rem 0.6rem',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '0.8rem',
                }}
              >
                Difficulty: {difficulty}
                <button
                  type="button"
                  onClick={() => handleDifficultyChange('')}
                  style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontWeight: 700 }}
                >
                  ✕
                </button>
              </span>
            )}
            {category && (
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  background: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-color)',
                  color: 'var(--purple)',
                  padding: '0.2rem 0.6rem',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '0.8rem',
                }}
              >
                Category: {category}
                <button
                  type="button"
                  onClick={() => handleCategoryChange('')}
                  style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontWeight: 700 }}
                >
                  ✕
                </button>
              </span>
            )}
          </div>
        )}
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Problems Table & Results */}
      {loading ? (
        <LoadingSpinner message="Querying problems from database..." />
      ) : problems.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3.5rem 2rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🔍</div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>No Problems Found</h3>
          <p style={{ color: 'var(--text-secondary)', maxWidth: '450px', margin: '0 auto 1.5rem auto' }}>
            We couldn't find any coding problems matching your current filter criteria. Try adjusting your search keyword or clearing filters.
          </p>
          <Button variant="primary" onClick={handleResetFilters}>
            Clear All Filters
          </Button>
        </div>
      ) : (
        <>
          <div className="table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th
                    style={{ width: '80px', cursor: 'pointer', userSelect: 'none' }}
                    onClick={() => handleSortToggle('id')}
                    title="Click to sort by ID"
                  >
                    # {sortBy === 'id' && (sortDir === 'asc' ? '↑' : '↓')}
                  </th>
                  <th
                    style={{ cursor: 'pointer', userSelect: 'none' }}
                    onClick={() => handleSortToggle('title')}
                    title="Click to sort by Title"
                  >
                    Title {sortBy === 'title' && (sortDir === 'asc' ? '↑' : '↓')}
                  </th>
                  <th
                    style={{ width: '180px', cursor: 'pointer', userSelect: 'none' }}
                    onClick={() => handleSortToggle('category')}
                    title="Click to sort by Category"
                  >
                    Category {sortBy === 'category' && (sortDir === 'asc' ? '↑' : '↓')}
                  </th>
                  <th
                    style={{ width: '140px', cursor: 'pointer', userSelect: 'none' }}
                    onClick={() => handleSortToggle('difficulty')}
                    title="Click to sort by Difficulty"
                  >
                    Difficulty {sortBy === 'difficulty' && (sortDir === 'asc' ? '↑' : '↓')}
                  </th>
                  <th style={{ width: '130px', textAlign: 'right' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {problems.map((prob) => (
                  <tr key={prob.id}>
                    <td style={{ color: 'var(--text-muted)', fontWeight: 600 }}>{prob.id}</td>
                    <td>
                      <Link
                        to={`/problems/${prob.id}`}
                        style={{
                          fontWeight: 600,
                          color: 'var(--text-primary)',
                          display: 'inline-block',
                          fontSize: '0.975rem',
                        }}
                      >
                        {prob.title}
                      </Link>
                    </td>
                    <td>
                      <span
                        style={{
                          display: 'inline-block',
                          background: 'var(--bg-secondary)',
                          border: '1px solid var(--border-subtle)',
                          padding: '0.2rem 0.6rem',
                          borderRadius: 'var(--radius-sm)',
                          fontSize: '0.825rem',
                          color: 'var(--text-secondary)',
                        }}
                      >
                        {prob.category || 'General'}
                      </span>
                    </td>
                    <td>
                      <Badge text={prob.difficulty} />
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <Link to={`/problems/${prob.id}`}>
                        <Button size="sm" variant="primary">
                          Solve &rarr;
                        </Button>
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Bottom Pagination & Results Bar */}
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: '1rem',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '0.75rem 0.25rem',
            }}
          >
            {/* Record Counter */}
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Showing <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{startRecord}</span> to{' '}
              <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{endRecord}</span> of{' '}
              <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{totalElements}</span> problems
            </div>

            {/* Pagination Button Group */}
            <div style={{ display: 'flex', gap: '0.35rem', alignItems: 'center' }}>
              {/* First Page */}
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage(0)}
                title="First Page"
              >
                ««
              </Button>

              {/* Previous Page */}
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                title="Previous Page"
              >
                « Prev
              </Button>

              {/* Numbered Page Buttons */}
              {getPageNumbers().map((pageIndex) => (
                <Button
                  key={pageIndex}
                  size="sm"
                  variant={currentPage === pageIndex ? 'primary' : 'outline'}
                  onClick={() => setCurrentPage(pageIndex)}
                  style={{ minWidth: '34px' }}
                >
                  {pageIndex + 1}
                </Button>
              ))}

              {/* Next Page */}
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                title="Next Page"
              >
                Next »
              </Button>

              {/* Last Page */}
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage(Math.max(0, totalPages - 1))}
                title="Last Page"
              >
                »»
              </Button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default ProblemListPage;
