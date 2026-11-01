### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:33Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low‑latency byte‑stream processing utilities: an OpenCL histogram kernel, a lock‑free ring buffer, and small shell/Python helpers for backup and sales reporting.

#### Purpose
- Deliver efficient, reusable components for byte‑stream manipulation and auxiliary tooling.

#### Responsibilities
- **GPU‑accelerated histogram** – two‑pass OpenCL kernels for 256‑bin byte frequency.
- **Lock‑free FIFO** – power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.
- **Utility scripts** – backup rotation and CSV‑based sales reporting.

#### Key units
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – compute per‑group and global histograms.
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – C API for ring buffer.
- `rotate_backups.sh`, `sales_report.py` – shell and Python scripts for maintenance tasks.

#### Data flow
1. **Histogram**: input byte array → per‑group local histograms → global reduction → final 256‑bin histogram.
2. **Ring buffer**: `ringbuf_init` → `ringbuf_write` (enqueue) → `ringbuf_read` (dequeue) → `ringbuf_used` / `ringbuf_free_space` (state queries).
3. **Scripts**:  
   - `rotate_backups.sh`: source directory → tar archive → backup directory → cleanup old archives.  
   - `sales_report.py`: CSV files → `parse_orders` → `revenue_by_month_and_region` → formatted report.

#### Dependencies
- OpenCL built‑ins (`get_local_id`, `atomic_inc`, etc.) for kernels.
- Standard C library (`malloc`, `memcpy`) for ring buffer.
- Shell utilities (`tar`, `date`) and Python modules (`csv`, `decimal`) for scripts.

#### Cross-cutting
- Power‑of‑two size enforcement and modulo arithmetic in ring buffer.
- Two‑pass reduction pattern with local memory aggregation in kernels.
- Non‑blocking semantics: write/read return actual bytes processed.
- Explicit error codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) and clear exit codes in scripts.
