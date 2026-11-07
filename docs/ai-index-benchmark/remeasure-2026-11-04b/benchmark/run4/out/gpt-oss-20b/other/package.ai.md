### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:52:56Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A collection of utilities for efficient byte‑level data handling, a lock‑free ring buffer for streaming, and small backup/reporting scripts.

#### Purpose
- Provide GPU‑accelerated byte histogram computation.
- Offer a lightweight, single‑producer/single‑consumer ring buffer.
- Supply shell/Python helpers for directory backups and sales reporting.

#### Responsibilities
- **GPU kernels** – two‑pass histogram generation and reduction.
- **Ring buffer** – allocation, wrap‑around copy, capacity checks, metrics.
- **Scripts** – backup rotation, CSV parsing, revenue aggregation, CLI.

#### Key units
- **kernels**  
  - `histogram_partial` – builds per‑group local 256‑bin histograms.  
  - `histogram_reduce` – aggregates partials into final histogram.  
  - `#define BINS 256` – histogram size constant.
- **ringbuf**  
  - `ringbuf_t` – buffer struct (`data`, `capacity`, `head`, `tail`).  
  - `ringbuf_status_t` – status enum (`OK`, `EINVAL`, `ENOMEM`).  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- **scripts**  
  - `rotate_backups.sh` – tar‑compresses directories, timestamps, prunes old archives.  
  - `sales_report.py` – `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Histogram** – host → device global memory → `histogram_partial` (local histograms) → `histogram_reduce` (final 256‑bin histogram).  
2. **Ring buffer** – init allocates memory; producer writes via `ringbuf_write`; consumer reads via `ringbuf_read`; metrics derived from head/tail indices.  
3. **Scripts** – backup: source → tar.gz → archive directory → prune; sales: CSV files → `Order` objects → filter by currency → aggregate by month/region → output TSV.

#### Dependencies
- OpenCL 1.2 built‑ins for kernels.
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.
- Bash utilities (`tar`, `date`, etc.) for backup script.
- Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`, `sys`) for sales report.

#### Cross-cutting
- **Power‑of‑two sizing** – ring buffer capacity must be a power of two; modulo arithmetic (`& (capacity-1)`) for wrap‑around.  
- **Two‑stage reduction** – kernels use local partials then global sum.  
- **Error handling** – ring buffer returns status codes; scripts exit with specific codes or raise `ValueError`.  
- **Thread safety** – ring buffer designed for single‑producer/single‑consumer; no locks.  
- **UTC timestamps** – both scripts use UTC for consistency.
