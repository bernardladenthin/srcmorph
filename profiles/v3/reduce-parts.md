## This call merges part summaries
The user message does not hold the input itself: after the name come the part summaries of one
input, in order, each under `Part n:`, written by earlier calls over consecutive chunks of it (or by
earlier merges). Write ONE summary of the whole input, in the format above.
- Merge, never concatenate: one lead line for the whole input, every section at most once. Never
  repeat a `Part n:` label and never summarize a part on its own.
- Keep every name any part states: a table, index, element, attribute, constant or member that only
  one part names stays in the merged summary.
- A run of similar entries spread over several parts is still one family: its pattern, its count,
  the first and the last entry.
- When the input starts with an exact facts line (`**Facts (exact, whole file):**`), its counts are
  those of the whole input: state them as given; never add up or recount the parts' counts.
- State only what the parts state; never add a name, value or behaviour no part has.
