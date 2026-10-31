### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:35Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU‑accelerated histogram computation, a lock‑free ring buffer for byte streams, and command‑line utilities for backing up directories and summarizing sales data.

#### Purpose
- Deliver high‑performance byte‑frequency analysis via OpenCL kernels.
- Offer a thread‑safe, power‑of‑two circular buffer for single‑producer/single‑consumer byte streams.
- Supply lightweight shell and Python scripts for automated backups and sales reporting.

#### Responsibilities
- **GPU Histogram Engine** – two‑pass OpenCL kernels (`histogram_partial`, `histogram_reduce`) that build per‑group partial histograms and reduce them into a 256‑bin result.
- **Ring Buffer Core** – C API (`ringbuf_init`, `ringbuf_write`, `ringbuf_read`, etc.) that manages a power‑of‑two sized circular buffer with head/tail indices and space queries.
- **Utility Scripts** – Bash (`rotate_backups.sh`) for rotating tar.gz backups, and Python (`sales_report.py`) for parsing CSV orders and aggregating revenue by month and region.

#### Key units
- **OpenCL Kernels**  
  - `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group local histogram builder.  
  - `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction into final 256‑bin histogram.  
  - `#define BINS 256` – constant for histogram size.
- **Ring Buffer API**  
  - `ringbuf_t` – struct with buffer pointer, capacity, head, tail.  
  - `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.
- **Scripts**  
  - `rotate_backups.sh` – rotates tar.gz archives, keeps a configurable number of recent backups.  
  - `sales_report.py` – functions `parse_orders`, `revenue_by_month_and_region`, and CLI entry `main`.

#### Data flow
1. **Histogram** – `histogram_partial` reads input bytes, updates local bins atomically, writes per‑group partials; `histogram_reduce` aggregates all partials into the final 256‑bin histogram.  
2. **Ring Buffer** – `ringbuf_init` allocates buffer; `ringbuf_write` copies producer data into buffer, advancing head; `ringbuf_read` copies consumer data out, advancing tail; `ringbuf_used`/`ringbuf_free_space` compute usage via `(head - tail) & (capacity - 1)`.  
3. **Scripts** – `rotate_backups.sh` tars source directory, names archive with UTC timestamp, deletes older archives beyond keep count; `sales_report.py` parses CSV rows into `Order` objects, filters by currency, aggregates revenue per `(YYYY‑MM, region)`, outputs tab‑separated summary.

#### Dependencies
- **OpenCL** – C99 OpenCL 1.2 built‑ins (`get_local_id`, `atomic_inc`, `barrier`, etc.).  
- **C Standard Library** – `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>` for ring buffer.  
- **Shell Utilities** – `bash`, `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, etc. for backup script.  
- **Python Standard Library** – `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal` for sales report.

#### Cross-cutting
- **Power‑of‑two ring buffer** – capacity validated with `is_power_of_two`; indices wrap via bitwise AND with `capacity - 1`.  
- **Atomic local memory** – OpenCL kernels use `atomic_inc` on `__local` bins to avoid global atomics.  
- **Error handling** – ring buffer returns `ringbuf_status_t`; scripts enforce strict input validation (`set -euo pipefail`, `ValueError`).  
- **UTC timestamps** – both backup and reporting utilities use UTC for naming and aggregation.
