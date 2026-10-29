### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:40:55Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Implements parallel byte histogram kernels, circular buffer streaming, and automated backup and financial reporting scripts.

#### Purpose
*   Compute frequency distribution of byte values in parallel.
*   Manage memory-managed circular buffers and automate system/financial tasks.

#### Responsibilities
*   Parallel histogram generation via OpenCL C 1.2 kernels.
*   Memory-managed circular buffer operations for byte streams.
*   Automated directory backup rotation and CSV sales data aggregation.

#### Key units
*   `histogram_partial`: Generates group-specific 256-bin counts using atomic increments.
*   `histogram_reduce`: Sums partial histograms across all groups into final 256-bin output.
*   `ringbuf_t`: Struct with `head` (producer) and `tail` (consumer) indices.
*   `ringbuf_write`: Pushes bytes to buffer.
*   `rotate_backups.sh`: Bash script managing archive lifecycle and cleanup.
*   `sales_report.py`: Python CLI entry point for sales data processing.
*   `revenue_by_month_and_region`: Aggregation function mapping `(YYYY-MM, region)` to `Decimal`.

#### Data flow
*   Input byte stream `input` processed by `histogram_partial` to populate `partial`.
*   `histogram_reduce` reads `partial` and `group_count` to compute `histogram`.
*   `ringbuf_write` copies `src` to `rb->data` updating `head` with wrap-around.
*   `ringbuf_read` copies `rb->data` to `dst` updating `tail` with wrap-around.
*   `csv_files` -> `parse_orders` -> `defaultdict` aggregation -> `min-revenue` filter -> tab-separated output.
*   `source_dir` -> `tar` archive -> `backup_dir` -> retention pruning by modification time.

#### Dependencies
*   OpenCL C 1.2 runtime environment.
*   `stdlib.h`, `string.h`, `stddef.h`, `stdint.h` (ringbuf).
*   `bash`, `tar`, `date`, `ls`, `mv`, `rm` (backup).
*   `argparse`, `csv`, `decimal`, `datetime` (sales).

#### Cross-cutting
*   Two-pass kernel execution model (partial then reduce).
*   Power-of-two capacity validation enforced across all buffer operations.
*   Error handling via `ringbuf_status_t` codes and exit codes.
*   Atomic memory operations for local bin counting.
