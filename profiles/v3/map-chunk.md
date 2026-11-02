This call reads one chunk. The user message does not hold the whole input, as the instructions
above say, only one chunk of it: after the name a label line, `Chunk i of n:` (for a sample
`Chunk i of n, sampled from m chunks:`, for a chunk read in halves with `, half 1` or `, half 2`),
then the chunk, cut at line boundaries; its first lines may repeat the last lines of the chunk
before. Your answer is a part summary; a later call merges the parts into the summary of the whole
input.
- Index only what this chunk literally shows. What lies outside it does not exist for this answer:
  never infer the rest of the input, never complete a declaration, table, element or block the chunk
  only begins or ends.
- Omit every section this chunk gives nothing for: a chunk of data rows has no schema, routines or
  API of its own.
- Keep every name, number and count exactly as the chunk shows it, and name what only this chunk
  has (another table, prefix, element or attribute than the run around it).
- Keep it short: what the merge needs, each name once, never a run of entries one by one.
- The lead, if the format above has one, says what this chunk holds.
