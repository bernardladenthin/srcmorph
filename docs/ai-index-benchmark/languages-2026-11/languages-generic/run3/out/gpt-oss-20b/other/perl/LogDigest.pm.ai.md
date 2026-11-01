### LogDigest.pm
- H: 1.0
- C: 38589334
- D: 2026-10-31T21:26:11Z
- T: 2026-10-31T21:40:10Z
- G: 
- A: 0.0.0
- X: file
---
> Condenses web‑server access logs into per‑host and per‑status counts.

#### Purpose
- Count requests per host and per HTTP status.
- Track number of slow requests (>1500 ms).
- Generate a summary with totals and timestamps.

#### Type
- Perl module (`class`) `LogDigest` (package).  
- Exports `parse_line`, `top_n`.

#### Input
- `new(level)` – log level string (`DEBUG`, `INFO`, `WARN`, `ERROR`).  
- `add($line)` – raw log line string.  
- `parse_line($line)` – raw log line string.  
- `top_n($counts, $n)` – hashref of counts, integer n.

#### Output
- `new` – blessed object with `level`, `hosts`, `statuses`, `slow`, `line_count`.  
- `add` – 1 if line parsed and counted, 0 otherwise.  
- `summarize` – hashref: `generated`, `total`, `hosts`, `busiest`, `slow`, `statuses`.  
- `top_n` – list of keys sorted by count descending, ties alphabetically.  
- `parse_line` – hashref with `host`, `time`, `method`, `path`, `status`, `millis`.

#### Core logic
- `parse_line`: regex extracts host, time, method, path, status, millis; normalizes host to lowercase, removes trailing dot.  
- `add`: limits to `MAX_LINES` (50 000); updates host and status counters; increments `slow` if `millis` > `SLOW_REQUEST_MS`.  
- `summarize`: totals hosts, counts busiest host, records current UTC time, returns status counts.  
- `top_n`: sorts counts descending, returns top `n` keys.

#### Public API
- `new(level)` → object – create digest with log level.  
- `add(line)` → int – add log line, return 1 if counted.  
- `summarize()` → hashref – summary statistics.  
- `parse_line(line)` → hashref – parsed log fields.  
- `top_n(counts, n)` → list – top n keys by count.

#### Constants and configuration
- `MAX_LINES` = 50_000  
- `DEFAULT_LEVEL` = 'WARN'  
- `SLOW_REQUEST_MS` = 1500  
- `%LEVEL_RANK` = (DEBUG=>0, INFO=>1, WARN=>2, ERROR=>3)

#### Dependencies
- `POSIX qw(strftime)`  
- `List::Util qw(max sum)`  
- `Carp qw(croak)`  

#### Exceptions / Errors
- `croak` on unknown log level in `new`.  
- `croak` if more than `MAX_LINES` added in `add`.  
- `parse_line` silently returns `undef` on regex mismatch.
