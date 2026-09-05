import React from 'react';

/**
 * Escapes HTML characters to prevent XSS.
 */
function escapeHtml(text) {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

const JAVA_KEYWORDS = new Set([
  'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch', 'char', 'class', 'const',
  'continue', 'default', 'do', 'double', 'else', 'enum', 'extends', 'final', 'finally', 'float',
  'for', 'goto', 'if', 'implements', 'import', 'instanceof', 'int', 'interface', 'long', 'native',
  'new', 'package', 'private', 'protected', 'public', 'return', 'short', 'static', 'strictfp',
  'super', 'switch', 'synchronized', 'this', 'throw', 'throws', 'transient', 'try', 'void',
  'volatile', 'while', 'record', 'sealed', 'permits', 'var'
]);

const JAVA_BUILTINS = new Set([
  'System', 'String', 'Scanner', 'Math', 'Integer', 'Double', 'Boolean', 'Character', 'Long',
  'List', 'ArrayList', 'LinkedList', 'Map', 'HashMap', 'TreeMap', 'Set', 'HashSet', 'TreeSet',
  'Queue', 'Deque', 'ArrayDeque', 'PriorityQueue', 'Stack', 'Arrays', 'Collections', 'Objects',
  'StringBuilder', 'StringBuffer', 'Exception', 'RuntimeException', 'Override'
]);

const PYTHON_KEYWORDS = new Set([
  'and', 'as', 'assert', 'async', 'await', 'break', 'class', 'continue', 'def', 'del', 'elif',
  'else', 'except', 'finally', 'for', 'from', 'global', 'if', 'import', 'in', 'is', 'lambda',
  'nonlocal', 'not', 'or', 'pass', 'raise', 'return', 'try', 'while', 'with', 'yield', 'match', 'case'
]);

const PYTHON_BUILTINS = new Set([
  'abs', 'all', 'any', 'bin', 'bool', 'bytearray', 'bytes', 'callable', 'chr', 'classmethod',
  'compile', 'complex', 'delattr', 'dict', 'dir', 'divmod', 'enumerate', 'eval', 'exec', 'filter',
  'float', 'format', 'frozenset', 'getattr', 'globals', 'hasattr', 'hash', 'help', 'hex', 'id',
  'input', 'int', 'isinstance', 'issubclass', 'iter', 'len', 'list', 'locals', 'map', 'max',
  'memoryview', 'min', 'next', 'object', 'oct', 'open', 'ord', 'pow', 'print', 'property',
  'range', 'repr', 'reversed', 'round', 'set', 'setattr', 'slice', 'sorted', 'staticmethod',
  'str', 'sum', 'super', 'tuple', 'type', 'vars', 'zip', 'sys', 'collections', 'heapq', 'math'
]);

/**
 * Tokenizes and highlights code into HTML string.
 */
export function highlightCode(code, language) {
  if (!code) return '';

  const isPython = language?.toUpperCase() === 'PYTHON';
  const lines = code.split('\n');
  const highlightedLines = [];

  let inMultiLineComment = false; // For Java /* ... */
  let inMultiLineString = false; // For Python """ ... """ or ''' ... '''
  let multiLineStringDelimiter = '';

  for (let line of lines) {
    let output = '';
    let i = 0;
    const len = line.length;

    while (i < len) {
      // 1. Handle Java multiline comment continuation
      if (inMultiLineComment) {
        const endIdx = line.indexOf('*/', i);
        if (endIdx !== -1) {
          const commentPart = line.substring(i, endIdx + 2);
          output += `<span class="tok-comment">${escapeHtml(commentPart)}</span>`;
          i = endIdx + 2;
          inMultiLineComment = false;
        } else {
          output += `<span class="tok-comment">${escapeHtml(line.substring(i))}</span>`;
          i = len;
        }
        continue;
      }

      // 2. Handle Python triple-quote multiline string continuation
      if (inMultiLineString) {
        const endIdx = line.indexOf(multiLineStringDelimiter, i);
        if (endIdx !== -1) {
          const strPart = line.substring(i, endIdx + 3);
          output += `<span class="tok-string">${escapeHtml(strPart)}</span>`;
          i = endIdx + 3;
          inMultiLineString = false;
        } else {
          output += `<span class="tok-string">${escapeHtml(line.substring(i))}</span>`;
          i = len;
        }
        continue;
      }

      // 3. Start of Java Multiline Comment
      if (!isPython && line.substring(i, i + 2) === '/*') {
        const endIdx = line.indexOf('*/', i + 2);
        if (endIdx !== -1) {
          const comment = line.substring(i, endIdx + 2);
          output += `<span class="tok-comment">${escapeHtml(comment)}</span>`;
          i = endIdx + 2;
        } else {
          output += `<span class="tok-comment">${escapeHtml(line.substring(i))}</span>`;
          inMultiLineComment = true;
          i = len;
        }
        continue;
      }

      // 4. Single-line Comment
      if ((!isPython && line.substring(i, i + 2) === '//') || (isPython && line[i] === '#')) {
        output += `<span class="tok-comment">${escapeHtml(line.substring(i))}</span>`;
        i = len;
        continue;
      }

      // 5. Python Triple-Quote Multiline String Start
      if (isPython && (line.substring(i, i + 3) === '"""' || line.substring(i, i + 3) === "'''")) {
        const delim = line.substring(i, i + 3);
        const endIdx = line.indexOf(delim, i + 3);
        if (endIdx !== -1) {
          const str = line.substring(i, endIdx + 3);
          output += `<span class="tok-string">${escapeHtml(str)}</span>`;
          i = endIdx + 3;
        } else {
          output += `<span class="tok-string">${escapeHtml(line.substring(i))}</span>`;
          inMultiLineString = true;
          multiLineStringDelimiter = delim;
          i = len;
        }
        continue;
      }

      // 6. Strings (Single & Double Quotes)
      if (line[i] === '"' || (isPython && line[i] === "'")) {
        const quote = line[i];
        let j = i + 1;
        while (j < len) {
          if (line[j] === '\\') {
            j += 2; // skip escaped character
            continue;
          }
          if (line[j] === quote) {
            j++;
            break;
          }
          j++;
        }
        const str = line.substring(i, j);
        output += `<span class="tok-string">${escapeHtml(str)}</span>`;
        i = j;
        continue;
      }

      // 7. Numbers (integers, hex, floats)
      if (/\d/.test(line[i]) && (i === 0 || !/[a-zA-Z0-9_$]/.test(line[i - 1]))) {
        let j = i;
        while (j < len && /[0-9a-fA-FxXbBoOeE_.]/.test(line[j])) {
          j++;
        }
        const num = line.substring(i, j);
        output += `<span class="tok-number">${escapeHtml(num)}</span>`;
        i = j;
        continue;
      }

      // 8. Word Identifiers (Keywords, Builtins, Functions, Constants)
      if (/[a-zA-Z_$]/.test(line[i])) {
        let j = i;
        while (j < len && /[a-zA-Z0-9_$]/.test(line[j])) {
          j++;
        }
        const word = line.substring(i, j);

        // Check lookahead for function call `word(`
        let k = j;
        while (k < len && /\s/.test(line[k])) k++;
        const isFunctionCall = k < len && line[k] === '(';

        if (word === 'True' || word === 'False' || word === 'true' || word === 'false') {
          output += `<span class="tok-boolean">${word}</span>`;
        } else if (word === 'None' || word === 'null') {
          output += `<span class="tok-null">${word}</span>`;
        } else if (word === 'self' || word === 'this' || word === 'super') {
          output += `<span class="tok-keyword">${word}</span>`;
        } else if (isPython ? PYTHON_KEYWORDS.has(word) : JAVA_KEYWORDS.has(word)) {
          output += `<span class="tok-keyword">${word}</span>`;
        } else if (isPython ? PYTHON_BUILTINS.has(word) : JAVA_BUILTINS.has(word)) {
          output += `<span class="tok-builtin">${word}</span>`;
        } else if (isFunctionCall) {
          output += `<span class="tok-function">${escapeHtml(word)}</span>`;
        } else if (/^[A-Z][a-zA-Z0-9_$]*$/.test(word)) {
          // PascalCase -> Likely Class/Type
          output += `<span class="tok-type">${escapeHtml(word)}</span>`;
        } else {
          output += escapeHtml(word);
        }

        i = j;
        continue;
      }

      // 9. Operators and Punctuation
      if (/[+\-*/%=&|<>!?:;,~^]/.test(line[i])) {
        output += `<span class="tok-operator">${escapeHtml(line[i])}</span>`;
        i++;
        continue;
      }

      // 10. Default single character
      output += escapeHtml(line[i]);
      i++;
    }

    highlightedLines.push(output);
  }

  // Preserve trailing newline for accurate rendering
  return highlightedLines.join('\n') + '\n';
}
