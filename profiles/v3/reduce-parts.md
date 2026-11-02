This call merges part summaries. The user message does not hold the input itself, as the
instructions above say: after the name come the part summaries of one input, in order, each under
`Part n:`, written by earlier calls over consecutive chunks of it (or by earlier merges); an exact
facts line and a note on a sample may come first. Write ONE answer for the whole input, in the
format above.
- Merge, never concatenate: the lead, if the format has one, once and about the whole input, not
  about one part; every section at most once. Never repeat a `Part n:` label and never summarize a
  part on its own.
- Keep every name any part states: a table, index, element, attribute, constant or member that only
  one part names stays in the merged summary.
- A family as the instructions above define one stays one family across the parts: its pattern, its
  count, the first and the last entry. What they say is no family (getters and setters) stays apart.
- Counts: the facts line (`**Facts (exact, whole file):**`), when the input starts with one, holds the
  whole input's exact counts: use them, and do not copy the line, the reader sees it already. Any other
  total adds up the parts' counts, and consecutive chunks overlap, so write it as "about N". Where the
  instructions above ask for a count as an addition, write only the total.
- When a note says the parts summarize a sample of the chunks, a count, first or last entry that is not
  in the facts line is the sample's: say so.
- State only what the parts state; never add a name, value or behaviour no part has.
