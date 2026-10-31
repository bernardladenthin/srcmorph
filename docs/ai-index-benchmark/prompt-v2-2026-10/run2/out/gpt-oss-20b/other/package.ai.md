### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:55:40Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A lightweight, GPU‑accelerated histogram engine, a lock‑free ring buffer, and a set of automation scripts for backup rotation and sales reporting.

#### Purpose
- Deliver high‑performance byte‑frequency analysis on the GPU.
- Provide a reusable, single‑producer/single‑consumer ring buffer in C.
- Automate backup retention and generate monthly sales summaries from CSV exports.

#### Responsibilities
- **GPU Histogram** – compute 256‑bin histograms via two‑pass OpenCL kernels.
- **Ring Buffer** – allocate, manage, and transfer byte streams without locks.
- **Automation Scripts** – rotate backups, compress archives, and produce sales reports.

#### Key units
- **OpenCL Kernels**  
  - `histogram_partial` – local accumulation of byte counts.  
  - `histogram_reduce` – global reduction into final 256‑bin array.  
  - Constant `BINS = 256`.
- **Ring Buffer API**  
  - `ringbuf_t` (buffer pointer, capacity, head/tail).  
  - `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
  - `ringbuf_init()`, `ringbuf_free()`, `ringbuf_write()`, `ringbuf_read()`, `ringbuf_used()`, `ringbuf_free_space()`.
- **Scripts**  
  - `rotate_backups.sh` – Bash backup rotation.  
  - `sales_report.py` – Python module with `parse_orders()`, `revenue_by_month_and_region()`, `main()`.

#### Data flow
1. **Histogram**  
   - Input `uchar*` → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates → final `uint*` histogram.
2. **Ring Buffer**  
   - `ringbuf_init()` sets up buffer → `ringbuf_write()` copies producer data → `ringbuf_read()` copies consumer data → status queries via `ringbuf_used()` / `ringbuf_free_space()`.
3. **Scripts**  
   - `rotate_backups.sh`: source dir → tar.gz → backup dir → cleanup old archives.  
   - `sales_report.py`: CSV → `Order` objects → revenue aggregation → TSV output.

#### Dependencies
- **OpenCL** runtime functions (`get_local_id`, `atomic_inc`, etc.) and local memory.  
- **C Standard Library** (`stddef.h`, `stdint.h`, `stdlib.h`, `string.h`).  
- **Bash utilities** (`tar`, `date`, `du`, etc.).  
- **Python standard library** (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`).

#### Cross‑cutting
- Two‑pass reduction pattern and atomic bin updates in GPU kernels.  
- Power‑of‑two capacity enforcement and modulo arithmetic in ring buffer.  
- Status‑code error handling in C; exit codes and exceptions in scripts.  
- Single‑producer/single‑consumer design mandates lock‑free operation.
