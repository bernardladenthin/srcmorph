### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:25Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a lightweight, lock‑free ring buffer for byte streams, a two‑pass OpenCL histogram kernel for image data, and command‑line utilities for backup rotation and sales reporting.

#### Purpose
- Offer a reusable, power‑of‑two ring buffer API for single‑producer/single‑consumer byte streams.
- Compute a 256‑bin histogram of arbitrary byte streams using efficient two‑pass OpenCL kernels.
- Automate backup rotation and generate monthly revenue reports from CSV exports.

#### Responsibilities
- **Ring buffer**: initialization, memory management, lock‑free read/write, capacity queries.
- **Histogram kernels**: per‑group partial histogram construction, global reduction to final 256‑bin result.
- **Scripts**: backup rotation (`rotate_backups.sh`), sales analytics (`sales_report.py`).

#### Key units
- `ringbuf_t`, `ringbuf_status_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- OpenCL kernels: `histogram_partial`, `histogram_reduce`; constant `BINS = 256`.
- `rotate_backups.sh` (Bash), `sales_report.py` (Python module with `parse_orders`, `revenue_by_month_and_region`, `main`).

#### Data flow
1. **Ring buffer**: `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries.
2. **Histogram**: `histogram_partial` reads input bytes, writes per‑group partials → `histogram_reduce` aggregates partials into final histogram.
3. **Scripts**:  
   - `rotate_backups.sh`: validate dirs → tar‑compress → timestamp → move → delete old archives.  
   - `sales_report.py`: parse CSV → create `Order` objects → aggregate by month/region → output TSV.

#### Dependencies
- Ring buffer: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`, `malloc`, `free`, `memcpy`.
- Histogram kernels: OpenCL built‑ins (`get_local_id`, `atomic_inc`, `barrier`, etc.).
- Scripts: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `echo`; Python `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- Power‑of‑two size enforcement and wrap‑around arithmetic in ring buffer.
- Two‑pass reduction pattern (local partials → global aggregation) in histogram kernels.
- Explicit error/status codes and exit codes across utilities.
- Minimal, self‑contained implementations with no external libraries beyond standard C/OpenCL and shell utilities.
