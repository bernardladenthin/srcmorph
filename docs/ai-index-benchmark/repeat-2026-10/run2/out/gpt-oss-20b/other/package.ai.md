### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:17:56Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a GPU‑accelerated histogram builder, a lock‑free byte ring buffer, and command‑line utilities for backup rotation and sales reporting.

#### Purpose
- Deliver high‑performance byte histogramming via OpenCL kernels.
- Offer a lightweight, single‑producer/single‑consumer ring buffer for byte streams.
- Supply reusable shell and Python scripts for backup management and revenue aggregation.

#### Responsibilities
- **GPU Histogram** – split input into per‑group partial histograms, then reduce to a global 256‑bin result.
- **Ring Buffer** – initialize, enqueue, dequeue, and query a power‑of‑two sized byte queue without locks.
- **Scripts** – rotate backups with retention policy; parse CSV orders and compute monthly/region revenue.

#### Key units
- `histogram_partial`, `histogram_reduce` kernels (OpenCL) – compute and reduce 256‑bin histograms.
- `ringbuf_t`, `ringbuf_status_t` – data structure and status enum.
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` – core API functions.
- `rotate_backups.sh` – backup rotation logic.
- `sales_report.py` – `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, `main`.

#### Data flow
1. **Histogram** – `histogram_partial` reads a byte stream, atomically updates local bins, writes per‑group partials; `histogram_reduce` aggregates these into the final histogram array.
2. **Ring Buffer** – `ringbuf_init` allocates memory; `ringbuf_write` copies producer data into the buffer, advancing head; `ringbuf_read` copies consumer data out, advancing tail; occupancy queried via `ringbuf_used`/`ringbuf_free_space`; `ringbuf_free` releases memory.
3. **Scripts** – `rotate_backups.sh` tar‑compresses a source directory, names the archive with a UTC timestamp, moves it to a backup directory, and deletes archives older than a keep count; `sales_report.py` reads CSV files, yields `Order` objects, aggregates revenue by month and region, and prints tab‑separated results.

#### Dependencies
- **OpenCL** – built‑in functions (`get_local_id`, `atomic_inc`, etc.) for histogram kernels.
- **C Standard Library** – `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>` for ring buffer implementation.
- **Shell utilities** – `tar`, `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `trap` for backup script.
- **Python standard library** – `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal` for sales reporting.

#### Cross-cutting
- Fixed 256‑bin histogram size ensures deterministic array sizes across kernels.
- Power‑of‑two capacity enforcement in the ring buffer guarantees efficient modulo arithmetic and wrap‑around handling.
- Both the ring buffer and scripts use simple status codes or exit codes for error reporting, avoiding exceptions or logging frameworks.
