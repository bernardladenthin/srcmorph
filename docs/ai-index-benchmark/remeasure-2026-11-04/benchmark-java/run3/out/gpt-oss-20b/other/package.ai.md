### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:51Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU‑accelerated byte‑histogram computation, a lock‑free ring buffer for streaming data, and small shell/Python utilities for backup and sales reporting.

#### Purpose
- Deliver high‑performance byte‑histogram kernels for OpenCL.
- Offer a lightweight, power‑of‑two ring buffer for single‑producer/single‑consumer byte streams.
- Supply command‑line tools for directory backups and CSV‑based sales aggregation.

#### Responsibilities
- **GPU kernels**: split work into per‑group partial histograms and a global reduction.
- **Ring buffer**: allocate, manage head/tail indices, read/write with wrap‑around, report space.
- **Scripts**: rotate backups with retention policy; parse CSV orders, filter by currency, aggregate revenue by month/region.

#### Key units
- **OpenCL**  
  - `histogram_partial` – per‑group local histogram builder.  
  - `histogram_reduce` – global reduction of partial histograms.  
  - `#define BINS 256` – constant for histogram size.
- **Ring buffer**  
  - `ringbuf_t` – buffer descriptor.  
  - `ringbuf_status_t` – status enum.  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- **Scripts**  
  - `rotate_backups.sh` – tar‑compress, timestamp, prune old archives.  
  - `sales_report.py` – `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Histogram**  
   - Input byte buffer → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.
2. **Ring buffer**  
   - Producer writes via `ringbuf_write`; consumer reads via `ringbuf_read`; status queried with `ringbuf_used`/`ringbuf_free_space`.
3. **Scripts**  
   - `rotate_backups.sh`: source → tar → archive → retention.  
   - `sales_report.py`: CSV files → parse → filter → aggregate → tab‑delimited output.

#### Dependencies
- OpenCL runtime APIs (`get_local_id`, `atomic_inc`, `barrier`, etc.).
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`).
- Bash utilities (`tar`, `date`, `du`, `ls`, `rm`, `mv`).
- Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`).

#### Cross-cutting
- Atomic increments and barriers ensure thread safety in kernels.
- Power‑of‑two capacity enforcement and wrap‑around logic in ring buffer.
- Consistent error handling: status enums in C, exit codes in Bash, exceptions in Python.
- Input validation and informative console output across all components.
