### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:11:59Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A collection of low‑level utilities for image histogramming, byte‑stream buffering, and command‑line data processing.

#### Purpose
- Provide reusable, high‑performance components for image analysis, streaming I/O, and data‑engineering tasks.

#### Responsibilities
- **Image processing** – compute 256‑bin byte histograms on GPU via OpenCL kernels.  
- **Stream buffering** – lock‑free, power‑of‑two ring buffer for single‑producer/single‑consumer byte streams.  
- **Command‑line tooling** – backup rotation and monthly sales reporting from CSV exports.

#### Key units
- **OpenCL kernels**  
  - `histogram_partial` – per‑group partial histogram accumulation.  
  - `histogram_reduce` – global reduction of partial histograms.  
- **Ring buffer API**  
  - `ringbuf_t` – buffer descriptor.  
  - `ringbuf_status_t` – status enum.  
  - `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`.  
- **Scripts**  
  - `rotate_backups.sh` – backup rotation Bash script.  
  - `sales_report.py` – Python module with `parse_orders`, `revenue_by_month_and_region`, and CLI entry point.

#### Data flow
1. **Histogram** – input byte stream → `histogram_partial` (local bins) → `histogram_reduce` (final 256‑bin array).  
2. **Ring buffer** – `ringbuf_init` → `ringbuf_write` (producer) → `ringbuf_read` (consumer) → status queries.  
3. **Backup** – source directory → `tar` → compressed archive → `BACKUP_DIR`; old archives pruned.  
4. **Sales report** – CSV → `parse_orders` → `Order` objects → `revenue_by_month_and_region` → tab‑separated output.

#### Dependencies
- **OpenCL** runtime and standard kernel functions.  
- **C standard library** (`stdlib.h`, `string.h`, etc.) for ring buffer.  
- **Shell utilities** (`tar`, `date`, `du`, `ls`, `rm`) for backup script.  
- **Python standard library** (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`) for sales report.

#### Cross-cutting
- **Atomicity & concurrency** – OpenCL kernels use `atomic_inc`; ring buffer relies on single‑producer/single‑consumer assumption.  
- **Power‑of‑two sizing** – ring buffer capacity validated via `is_power_of_two`; modulo operations use bitmask.  
- **Error handling** – ring buffer returns `ringbuf_status_t`; scripts use exit codes and exception propagation.  
- **Performance** – OpenCL kernels split work into local and global phases; ring buffer minimizes memory copies with two‑chunk logic.  
- **Time handling** – backup script uses UTC timestamps; sales report aggregates by month using `datetime`.
