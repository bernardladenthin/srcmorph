### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:22:51Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low‑level byte‑stream utilities (histogram kernels, ring buffer) and higher‑level maintenance scripts (backup rotation, sales reporting) for data processing pipelines.

#### Purpose
- Deliver efficient byte‑frequency analysis via OpenCL kernels.
- Offer a lock‑free ring buffer for single‑producer/single‑consumer byte streams.
- Automate backup retention and generate monthly sales revenue reports.

#### Responsibilities
- **GPU Acceleration** – two‑pass histogram kernels (`histogram_partial`, `histogram_reduce`) for 256‑bin byte histograms.
- **Concurrent Buffering** – power‑of‑two ring buffer API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`, etc.) with status codes.
- **Maintenance Automation** – Bash backup rotation (`rotate_backups.sh`) and Python sales reporting (`sales_report.py`) with CLI and CSV parsing.

#### Key units
- `histogram_partial` / `histogram_reduce` kernels – local accumulation and global reduction.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring buffer types and operations.
- `rotate_backups.sh` – backup rotation script.
- `sales_report.py` – CLI tool; `parse_orders`, `revenue_by_month_and_region`, `main`; `Order` dataclass.

#### Data flow
1. **Histogram** – `histogram_partial` reads input bytes → updates local bins → writes partials; `histogram_reduce` aggregates partials → final histogram.
2. **Ring Buffer** – `ringbuf_init` allocates buffer; producer calls `ringbuf_write` (two‑segment copy, head advance); consumer calls `ringbuf_read` (two‑segment copy, tail advance); metrics via `ringbuf_used` / `ringbuf_free_space`; `ringbuf_free` cleans up.
3. **Scripts** – `rotate_backups.sh` validates paths, tars source, timestamps, deletes excess archives; `sales_report.py` parses CSVs into `Order`, aggregates revenue by month/region, outputs TSV.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.
- Standard C library (`malloc`, `free`, `memcpy`, etc.) for ring buffer.
- Bash utilities (`tar`, `date`, `du`, `basename`, `ls`) for backup script.
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- **Atomicity & Lock‑free** – ring buffer uses distinct head/tail indices, no locks.
- **Power‑of‑two Modulo** – efficient wrap‑around via bitwise AND.
- **Two‑pass Reduction** – common pattern in histogram kernels.
- **Error Handling** – status enums in C, `set -euo pipefail` in Bash, `ValueError` propagation in Python.
- **Logging** – console output for status and cleanup actions.
