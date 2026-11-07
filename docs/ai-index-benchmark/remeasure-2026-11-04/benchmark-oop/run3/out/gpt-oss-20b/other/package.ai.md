### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:56Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low‑level utilities for byte‑level data processing, circular buffering, and simple command‑line tooling for backups and revenue reporting.

#### Purpose
- Deliver reusable OpenCL kernels for histogram generation.
- Offer a lock‑free ring buffer for byte streams.
- Supply shell/Python scripts for backup rotation and CSV revenue analysis.

#### Responsibilities
- **Histogram kernels**: compute 256‑bin byte histograms via two‑pass OpenCL reduction.
- **Ring buffer**: manage a power‑of‑two circular FIFO for single‑producer/single‑consumer byte streams.
- **Scripts**: automate tar‑gzip backup rotation and generate monthly revenue reports from CSV orders.

#### Key units
- **OpenCL**  
  - `histogram_partial` – per‑group local histogram builder.  
  - `histogram_reduce` – global aggregation of partial histograms.  
  - `#define BINS 256` – histogram size constant.
- **Ring buffer**  
  - `ringbuf_t` – buffer descriptor (`data`, `capacity`, `head`, `tail`).  
  - `ringbuf_status_t` – status enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- **Scripts**  
  - `rotate_backups.sh` – Bash backup rotation utility.  
  - `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Histogram**  
   - Input byte stream → `histogram_partial` writes per‑group partials → `histogram_reduce` aggregates into final 256‑bin histogram.
2. **Ring buffer**  
   - `ringbuf_init` allocates buffer → `ringbuf_write` copies data to `head` → `ringbuf_read` retrieves from `tail` → `ringbuf_used`/`ringbuf_free_space` report usage.
3. **Scripts**  
   - `rotate_backups.sh`: source dir → tar‑gzip → timestamped archive → prune old archives.  
   - `sales_report.py`: CSV files → `parse_orders` → filter by currency → `revenue_by_month_and_region` aggregates → stdout report.

#### Dependencies
- **OpenCL**: C99 built‑ins (`get_local_id`, `atomic_inc`, `barrier`, etc.).
- **Ring buffer**: C standard library (`malloc`, `free`, `memcpy`, `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`).
- **Scripts**:  
  - Bash: `tar`, `date`, `du`, `ls`, `rm`, `mv`.  
  - Python: `argparse`, `csv`, `sys`, `collections`, `dataclasses`, `datetime`, `decimal`.

#### Cross‑cutting
- Power‑of‑two ring buffer uses bitwise modulo (`capacity - 1`) for wrap‑around.
- Single‑producer/single‑consumer design eliminates locks; indices updated by distinct threads.
- Two‑pass reduction pattern (local accumulation → global aggregation) in histogram kernels.
- Consistent error handling: ring buffer returns `ringbuf_status_t`; scripts use exit codes 0/1/2 and clear usage messages.
- Timestamp formatting follows UTC ISO‑8601 (`YYYYMMDDTHHMMSSZ`) for backup names.
