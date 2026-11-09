### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:16Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low‑level utilities for byte‑level data processing, including an OpenCL histogram kernel, a lock‑free ring buffer, and small shell/Python tools for backup and sales reporting.  

#### Purpose  
- Deliver efficient, reusable components for byte‑stream analysis and buffering.  
- Offer simple command‑line helpers for data backup and CSV‑based reporting.  

#### Responsibilities  
- **Histogram computation** – split counting and reduction across OpenCL work‑groups.  
- **Ring buffer management** – power‑of‑two circular storage with single‑producer/single‑consumer semantics.  
- **Utility scripts** – rotating tarball backups and CSV sales aggregation.  

#### Key units  
- `histogram_partial`, `histogram_reduce` (OpenCL kernels) – build and reduce 256‑bin histograms.  
- `ringbuf_t`, `ringbuf_status_t` – data structure and status enum for the ring buffer.  
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – core API.  
- `rotate_backups.sh` – Bash script for timestamped archive rotation.  
- `sales_report.py` – Python module exposing `parse_orders`, `revenue_by_month_and_region`, `main`.  
- `Order` dataclass – represents an order record.  

#### Data flow  
1. **Histogram**: input byte stream → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.  
2. **Ring buffer**: `ringbuf_init` allocates buffer → `ringbuf_write` enqueues bytes → `ringbuf_read` dequeues bytes → status queries report used/free space.  
3. **Scripts**:  
   - `rotate_backups.sh`: source → tarball → target directory → prune old archives.  
   - `sales_report.py`: CSV rows → `Order` objects → aggregation by month/region → tab‑separated report.  

#### Dependencies  
- OpenCL 1.2 built‑ins (`get_local_id`, `atomic_inc`, etc.) for histogram kernels.  
- Standard C headers (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`) for ring buffer.  
- Shell utilities (`tar`, `date`, `du`, `ls`, `rm`, `mkdir`) for backup script.  
- Python standard library (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.  

#### Cross-cutting  
- **Power‑of‑two sizing**: ring buffer capacity must be a power of two; one slot unused to distinguish full/empty.  
- **Modulo arithmetic**: head/tail wrap via `& (capacity - 1)` for efficiency.  
- **Atomicity & barriers**: histogram kernels use local memory, atomic increments, and barriers to avoid race conditions.  
- **Error handling**: ring buffer functions return status codes or byte counts; scripts exit non‑zero on failure with clear messages.  
- **Input validation**: backup script checks numeric `KEEP`; sales report validates CSV columns and currency.
