import React, { useState, useEffect, useRef, useMemo } from 'react';
import { highlightCode } from './SyntaxHighlighter';
import { Button } from '../common/Button';

export const DEFAULT_JAVA_BOILERPLATE = `import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Write your solution logic here
        
    }
}
`;

export const DEFAULT_PYTHON_BOILERPLATE = `import sys

def main():
    # Read inputs from standard input
    input_data = sys.stdin.read().split()
    # Write your solution logic here
    

if __name__ == "__main__":
    main()
`;

/**
 * Professional, modular code editor component for Java & Python.
 */
export const CodeEditor = ({
  problemId,
  language = 'JAVA',
  onLanguageChange,
  code,
  onChange,
  onSubmit,
  onRun,
  isSubmitting = false,
  isRunning = false,
  readOnly = false,
}) => {
  const [fontSize, setFontSize] = useState(14);
  const [copied, setCopied] = useState(false);
  const [savedStatus, setSavedStatus] = useState(false);

  const textareaRef = useRef(null);
  const highlightRef = useRef(null);
  const gutterRef = useRef(null);

  // Line count
  const lines = useMemo(() => {
    return (code || '').split('\n');
  }, [code]);

  // Synchronize Scroll between Textarea, Highlight Layer, and Gutter
  const handleScroll = (e) => {
    const { scrollTop, scrollLeft } = e.target;
    if (highlightRef.current) {
      highlightRef.current.scrollTop = scrollTop;
      highlightRef.current.scrollLeft = scrollLeft;
    }
    if (gutterRef.current) {
      gutterRef.current.scrollTop = scrollTop;
    }
  };

  // Keyboard Navigation: Tab Indentation & Ctrl+Enter to Submit
  const handleKeyDown = (e) => {
    // 1. Ctrl + Enter or Cmd + Enter to submit code
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      e.preventDefault();
      if (onSubmit && !isSubmitting) {
        onSubmit();
      }
      return;
    }

    const textarea = textareaRef.current;
    if (!textarea) return;

    // 2. Tab and Shift+Tab key indentation
    if (e.key === 'Tab') {
      e.preventDefault();
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const value = textarea.value;

      if (e.shiftKey) {
        // Dedent 4 spaces
        const beforeCursor = value.substring(0, start);
        const lastNewLine = beforeCursor.lastIndexOf('\n');
        const lineStart = lastNewLine === -1 ? 0 : lastNewLine + 1;
        const line = value.substring(lineStart, start);

        if (line.startsWith('    ')) {
          const newValue = value.substring(0, lineStart) + value.substring(lineStart + 4);
          onChange(newValue);
          setTimeout(() => {
            textarea.selectionStart = textarea.selectionEnd = Math.max(lineStart, start - 4);
          }, 0);
        }
      } else {
        // Indent 4 spaces
        const tabSpaces = '    ';
        const newValue = value.substring(0, start) + tabSpaces + value.substring(end);
        onChange(newValue);
        setTimeout(() => {
          textarea.selectionStart = textarea.selectionEnd = start + 4;
        }, 0);
      }
      return;
    }

    // 3. Auto-indentation on Enter
    if (e.key === 'Enter') {
      const start = textarea.selectionStart;
      const value = textarea.value;
      const beforeCursor = value.substring(0, start);
      const lastNewLine = beforeCursor.lastIndexOf('\n');
      const currentLine = lastNewLine === -1 ? beforeCursor : beforeCursor.substring(lastNewLine + 1);

      const matchIndent = currentLine.match(/^\s*/);
      let indent = matchIndent ? matchIndent[0] : '';

      // Add extra indent if line ends with { or :
      if (currentLine.trim().endsWith('{') || currentLine.trim().endsWith(':')) {
        indent += '    ';
      }

      if (indent.length > 0) {
        e.preventDefault();
        const newValue = value.substring(0, start) + '\n' + indent + value.substring(start);
        onChange(newValue);
        setTimeout(() => {
          textarea.selectionStart = textarea.selectionEnd = start + 1 + indent.length;
        }, 0);
      }
    }
  };

  // Reset to default boilerplate for current language
  const handleResetTemplate = () => {
    const defaultTemplate = language === 'JAVA' ? DEFAULT_JAVA_BOILERPLATE : DEFAULT_PYTHON_BOILERPLATE;
    onChange(defaultTemplate);
    if (problemId) {
      localStorage.setItem(`oj_code_${problemId}_${language}`, defaultTemplate);
    }
  };

  // Clear code
  const handleClearCode = () => {
    onChange('');
    if (problemId) {
      localStorage.removeItem(`oj_code_${problemId}_${language}`);
    }
  };

  // Copy code to clipboard
  const handleCopyCode = async () => {
    try {
      await navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy code:', err);
    }
  };

  // Highlighted HTML output
  const highlightedHtml = useMemo(() => {
    return highlightCode(code, language);
  }, [code, language]);

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
        borderRadius: 'var(--radius-lg)',
        overflow: 'hidden',
        border: '1px solid var(--border-color)',
        background: '#0d1117',
      }}
    >
      {/* Editor Top Toolbar */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '0.65rem 1rem',
          background: 'var(--bg-tertiary)',
          borderBottom: '1px solid var(--border-color)',
          flexWrap: 'wrap',
          gap: '0.5rem',
        }}
      >
        {/* Left Toolbar Controls: Language Selection */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)' }}>
            Language:
          </label>
          <select
            id="editor-language-select"
            className="form-input"
            style={{
              padding: '0.3rem 0.65rem',
              fontSize: '0.85rem',
              width: 'auto',
              background: 'var(--bg-secondary)',
              cursor: 'pointer',
            }}
            value={language}
            onChange={(e) => onLanguageChange && onLanguageChange(e.target.value)}
          >
            <option value="JAVA">Java 17 (OpenJDK)</option>
            <option value="PYTHON">Python 3</option>
          </select>

          {/* Autosave Pill */}
          <span
            style={{
              fontSize: '0.75rem',
              color: 'var(--text-muted)',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.3rem',
            }}
          >
            <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: 'var(--success)' }} />
            Saved locally
          </span>
        </div>

        {/* Right Toolbar Controls: Font Size, Reset, Clear, Copy */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          {/* Font Size Selector */}
          <select
            className="form-input"
            style={{
              padding: '0.25rem 0.5rem',
              fontSize: '0.75rem',
              width: 'auto',
              background: 'var(--bg-secondary)',
              cursor: 'pointer',
            }}
            value={fontSize}
            onChange={(e) => setFontSize(Number(e.target.value))}
            title="Font Size"
          >
            <option value="12">12px</option>
            <option value="14">14px</option>
            <option value="16">16px</option>
            <option value="18">18px</option>
          </select>

          <Button size="sm" variant="outline" onClick={handleResetTemplate} title="Reset to Starter Boilerplate">
            ↺ Template
          </Button>

          <Button size="sm" variant="outline" onClick={handleClearCode} title="Clear all code">
            Clear
          </Button>

          <Button size="sm" variant="outline" onClick={handleCopyCode} title="Copy code to clipboard">
            {copied ? '✓ Copied' : '📋 Copy'}
          </Button>
        </div>
      </div>

      {/* Editor Body with Gutter & Synchronized Highlight Layer */}
      <div className="editor-wrapper" style={{ fontSize: `${fontSize}px` }}>
        {/* Line Numbers Gutter */}
        <div ref={gutterRef} className="editor-gutter">
          {lines.map((_, idx) => (
            <div key={idx} style={{ height: `${fontSize * 1.6}px` }}>
              {idx + 1}
            </div>
          ))}
        </div>

        {/* Textarea + Syntax Highlight Layer Container */}
        <div className="editor-scroll-container">
          {/* Highlight Layer (Rendered underneath textarea) */}
          <div
            ref={highlightRef}
            className="editor-highlight"
            dangerouslySetInnerHTML={{ __html: highlightedHtml }}
          />

          {/* Interactive Textarea */}
          <textarea
            ref={textareaRef}
            id="code-editor-input"
            className="editor-textarea"
            value={code}
            onChange={(e) => onChange(e.target.value)}
            onScroll={handleScroll}
            onKeyDown={handleKeyDown}
            placeholder="Write your solution here..."
            spellCheck="false"
            autoCapitalize="off"
            autoComplete="off"
            autoCorrect="off"
            readOnly={readOnly}
          />
        </div>
      </div>

      {/* Editor Action Footer */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '0.75rem 1.25rem',
          background: 'var(--bg-tertiary)',
          borderTop: '1px solid var(--border-color)',
          flexWrap: 'wrap',
          gap: '0.75rem',
        }}
      >
        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <span>{lines.length} lines</span> &bull; <span>{code ? code.length : 0} characters</span> &bull;{' '}
          <span style={{ color: 'var(--text-secondary)' }}>Shortcut: <kbd style={{ background: '#21262d', padding: '0.1rem 0.35rem', borderRadius: '4px' }}>Ctrl + Enter</kbd> to submit</span>
        </div>

        <div style={{ display: 'flex', gap: '0.6rem' }}>
          {onRun && (
            <Button
              id="run-code-btn"
              variant="outline"
              size="md"
              isLoading={isRunning}
              disabled={isRunning || isSubmitting}
              onClick={onRun}
              title="Run code against sample test cases"
            >
              {isRunning ? 'Running...' : '▶ Run Sample'}
            </Button>
          )}

          <Button
            id="submit-code-btn"
            variant="success"
            size="md"
            isLoading={isSubmitting}
            disabled={isSubmitting || isRunning}
            onClick={onSubmit}
            title="Submit solution to the full test case judge"
          >
            {isSubmitting ? 'Evaluating Solution...' : 'Submit Solution \u25B6'}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default CodeEditor;
