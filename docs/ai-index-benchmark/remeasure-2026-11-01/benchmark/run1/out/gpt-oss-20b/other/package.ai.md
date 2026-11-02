### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:30Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lightweight, high‑performance toolkit for byte‑stream processing, including GPU‑accelerated histograms, a lock‑free ring buffer, and command‑line utilities for data backup and sales reporting.  

#### Purpose  
- Deliver efficient byte‑stream manipulation primitives.  
- Offer reusable utilities for backup and reporting tasks.  

#### Responsibilities  
- **GPU Histogram** – compute 256‑bin histograms via two‑pass OpenCL kernels.  
- **Ring Buffer** – lock‑free, power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.  
- **Scripts** – shell and Python tools for directory backups and CSV‑based sales aggregation.  

#### Key units  
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – per‑group partial and global reduction passes.  
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` – core ring‑buffer API.  
- `rotate_backups.sh` – Bash script for timestamped tar.gz archives with retention.  
- `sales_report.py` – Python module: `parse_orders`, `revenue_by_month_and_region`, `main`.  

#### Data flow  
1. **Histogram**: byte array → `histogram_partial` (local bins) → `histogram_reduce` (final 256‑bin histogram).  
2. **Ring Buffer**: producer writes via `ringbuf_write` → consumer reads via `ringbuf_read`; status via `ringbuf_used`/`ringbuf_free_space`.  
3. **Scripts**:  
   - Backup: source directory → `tar` → temp archive → rename → retention check → delete old archives.  
   - Report: CSV files → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → formatted output.  

#### Dependencies  
- OpenCL built‑ins (`get_local_id`, `atomic_inc`, etc.) for histogram kernels.  
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.  
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `mv`) for backup script.  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.  

#### Cross‑cutting  
- **Two‑pass reduction** pattern in kernels; atomic increments and local barriers for thread safety.  
- **Power‑of‑two capacity** enforced in ring buffer; error codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
- **Single‑producer/single‑consumer** assumption eliminates locks in ring buffer.  
- **Explicit error handling**: status codes, return counts, and clear exit codes in scripts.  
- **Configuration**: minimal, via command‑line arguments or constants (`KEEP`, `EUR`).
