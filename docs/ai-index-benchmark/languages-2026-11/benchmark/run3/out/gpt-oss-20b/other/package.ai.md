### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:06:38Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low‑level utilities for byte‑stream processing, a lock‑free ring buffer, and simple backup/reporting scripts.

#### Purpose
- Compute byte histograms via OpenCL kernels.
- Offer a single‑producer/single‑consumer ring buffer for byte data.
- Automate directory backup rotation and CSV sales reporting.

#### Responsibilities
- **Histogram kernels**: two‑pass reduction of 256‑bin histograms on GPU.
- **Ring buffer**: power‑of‑two circular storage with bulk read/write and capacity queries.
- **Scripts**: shell backup rotation and Python sales report generation.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels, `#define BINS 256`).
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- `rotate_backups.sh`, `sales_report.py` (functions `parse_orders`, `revenue_by_month_and_region`, `main`), `Order` dataclass.

#### Data flow
1. **Histogram**: input byte stream → per‑group local bins → global partial buffer → final 256‑bin histogram.
2. **Ring buffer**: `ringbuf_init` allocates buffer → `ringbuf_write` copies producer data → `ringbuf_read` retrieves consumer data → queries via `ringbuf_used`/`ringbuf_free_space` → `ringbuf_free` cleans up.
3. **Scripts**: backup script compresses source → timestamps → moves to backup dir → deletes old archives; sales script parses CSV → filters → aggregates by month/region → prints TSV.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.
- Standard C library (`stdlib.h`, `string.h`, etc.) for ring buffer.
- Shell utilities (`tar`, `date`, `rm`, etc.) for backup script.
- Python standard library (`argparse`, `csv`, `datetime`, `decimal`, etc.) for sales report.

#### Cross-cutting
- Two‑stage reduction pattern in histogram to avoid global atomics.
- Power‑of‑two capacity enforcement and modulo arithmetic in ring buffer.
- Consistent UTC timestamp format `YYYYMMDDTHHMMSSZ` for backups.
- Strict input validation and error handling across all components.
