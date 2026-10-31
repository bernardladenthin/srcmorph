### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:14:43Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Computes byte histograms from streams using OpenCL parallel kernels and provides a ring buffer for byte storage; also creates timestamped backups and generates monthly regional revenue reports from CSV order data.

#### Purpose
- Parallel histogram computation for byte streams via OpenCL kernels.
- Fixed-capacity byte ring buffer for single producer/single consumer use.
- Automated backup of source directories with pruning of old archives.
- Revenue reporting from CSV order data filtered by currency and minimum revenue.

#### Responsibilities
- Histogram calculation: per-group counting and global reduction of byte frequencies.
- Ring buffer management: initialization, atomic-like writes/reads, space queries.
- Backup automation: directory validation, compression, temporary file handling, cleanup of old archives.
- Report generation: CSV parsing, filtering by currency/min revenue, aggregation by month/region, output printing.

#### Key units
- `histogram_partial`: OpenCL kernel that initializes local bins, increments counts for input bytes in a work group, writes partial results, and synchronizes with barrier.
- `histogram_reduce`: OpenCL kernel that sums partial bin counts across all groups and writes the final 256-bin histogram; returns early for bins outside range.
- `ringbuf_t`: struct holding `data`, `capacity`, `head`, `tail` fields for the ring buffer.
- `ringbuf_init`: initializes buffer; validates power-of-two capacity and allocates storage.
- `ringbuf_write`: writes up to `len` bytes with wrap-around; returns bytes written.
- `ringbuf_read`: reads up to `len` bytes with wrap-around; returns bytes read.
- `rotate_backups.sh`: shell script that backs up a source directory into a timestamped tar.gz archive and deletes archives older than the keep count.
- `sales_report.py`: Python script with `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, and `main` CLI entry point.
- `Order`: dataclass representing a parsed order row with `order_date`, `region`, `amount`, `currency`.

#### Data flow
- Input byte stream (`input`) and length (`length`) are processed by `histogram_partial` in parallel work groups, producing a `partial` array (`group_count * BINS`).
- `histogram_reduce` reads `partial` and `group_count`, computes the sum per bin across groups, and writes the consolidated `histogram` array (`BINS` size).
- `ringbuf_init` creates a `ringbuf_t` instance with power-of-two capacity; `ringbuf_write` writes data with wrap-around; `ringbuf_read` reads data with wrap-around; `ringbuf_used`/`ringbuf_free_space` query usage/free space.
- `rotate_backups.sh` receives `source_dir`, `backup_dir`, `keep`; validates inputs; creates temporary archive; moves to final name; lists archives; deletes old ones.
- `sales_report.py` receives CSV file paths via `main`; `parse_orders` filters by `currency`; `revenue_by_month_and_region` aggregates `amount` by `(month, region)`; `main` prints results or exits on failure.

#### Dependencies
- OpenCL C 1.2 primitives (`get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`) for kernels.
- `ringbuf.h` (defines `ringbuf_t`, `ringbuf_status_t`, constants) for ring buffer.
- Unix utilities (`date`, `mkdir`, `tar`, `ls`, `mv`, `rm`, `echo`, `printf`) for `rotate_backups.sh`.
- Python standard library (`argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses`, `datetime.date`, `decimal`) for `sales_report.py`.
- `<stdlib.h>` (`malloc`, `free`), `<string.h>` (`memcpy`), `<stddef.h>`/`<stdint.h>` for `ringbuf.c`.

#### Cross-cutting
- Uses `barrier(CLK_LOCAL_MEM_FENCE)` after initialization and counting in pass 1 for local synchronization in kernels.
- Relies on atomic `uint` increment safety in OpenCL 1.2.
- Early return in `histogram_reduce` for bins beyond `BINS-1` to avoid unnecessary work.
- Ring buffer capacity restricted to power-of-two values (`(n & (n-1)) == 0`) for efficient index wrapping; index updates use `(index + n) & (capacity - 1)`.
- Used space calculated via modular arithmetic (`(head - tail) & (capacity - 1)` in `ringbuf.c` and `(head - tail + capacity) % capacity` in `ringbuf.h`).
- All errors returned via `ringbuf_status_t`; no runtime exceptions.
- `rotate_backups.sh` uses `set -e`, `set -u`, `pipefail`, and a `trap` for cleanup; `sales_report.py` uses standard error propagation.
- Timestamp format is UTC `%Y%m%dT%H%M%SZ` for backups; report month string uses `YYYY-MM` format.
- Designed for single producer/single consumer usage (no explicit locking mentioned, but "thread-safe" noted in header).
