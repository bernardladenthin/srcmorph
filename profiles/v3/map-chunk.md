## This call reads one chunk
The user message does not hold the whole input, only one chunk of it: after the name, a line
`Chunk i of n:`, then the chunk, cut at line boundaries (consecutive chunks overlap by a few lines).
Your answer is a part summary; a later call merges the parts into the summary of the whole input.
- Index only what this chunk literally shows. What lies outside it does not exist for this answer:
  never infer the rest of the input, never complete a declaration, table, element or block the chunk
  only begins or ends.
- Omit every section this chunk gives nothing for: a chunk of data rows has no schema, routines or
  API of its own.
- Keep every name, number and count exactly as the chunk shows it, and name what only this chunk
  has (another table, prefix, element or attribute than the run around it).
- The lead line says what this chunk holds.
