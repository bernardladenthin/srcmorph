### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:36Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Heterogeneous utilities for GPU byte-frequency histograms, lock-free streaming FIFO, and backup/revenue ops reporting.
#### Purpose
- Mixed utility package with no single pipeline: parallel histogram kernels, SPSC byte transport, ops/reporting scripts
#### Responsibilities
- GPU histogram counting: strided parallel `input` scan with per-work-group partials plus per-bin reduction to 256-bin `histogram`
- Lock-free streaming FIFO: fixed power-of-two byte ring buffer with clamped wrap-around enqueue/dequeue and occupancy accounting
- Ops utilities: timestamped `tar -czf` backup rotation with newest-N pruning, CSV monthly regional revenue aggregation
#### Key units
- `histogram_partial` — per-group counters into `partial[group_id * BINS + bin]` via `local_bins` + `atomic_inc`
- `histogram_reduce` — per-bin sum over `group_count` into `histogram[bin]`
- `histogram.cl` — OpenCL C 1.2 file with `BINS=256`, two `__kernel` entry points
- `ringbuf_t` / `struct ringbuf` — `data`, `capacity`, volatile `head`/`tail` FIFO state
- `ringbuf_init` / `ringbuf_free` — power-of-two validation + `malloc`, release and reset
- `ringbuf_write` / `ringbuf_read` — clamped partial copies with two-chunk `memcpy`, index advance via `& (capacity-1)`
- `ringbuf_used` / `ringbuf_free_space` — `(head-tail) & (capacity-1)` occupied count, `capacity-1-used` writable count
- `ringbuf_status_t` — `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` init result
- `rotate_backups.sh` — `$BACKUP_DIR/<name>_YYYYMMDDTHHMMSSZ.tar.gz` creation, `KEEP` pruning
- `Order` — frozen dataclass `order_date`, `region`, `amount` sales record
- `parse_orders` — yields matching-currency `Order` from `order_date, region, amount, currency` CSV
- `revenue_by_month_and_region` — `dict[(month, region)] -> Decimal` grouped by `%Y-%m`
- `main(argv)` — `--currency`, `--min-revenue` thresholded `YYYY-MM region revenue currency` report
#### Dependencies
- No cross-subpackage collaboration; `kernels/`, `ringbuf/`, `scripts/` independent
- OpenCL runtime: global `uchar input`, `uint partial/histogram`, local `uint[256] local_bins`, `get_global_id`, `get_local_id`, `get_global_size`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`
- C stdlib: `stdlib.h` `malloc`/`free`, `string.h` `memcpy`, `stddef.h` `size_t`, `stdint.h` `uint8_t`
- Bash tools: `set -euo pipefail`, `tar`, `date -u`, `ls -1t`, `du -h`, `mkdir`, `mv`, `rm`, `basename`, `dirname`, `mapfile`, `trap`
- Python stdlib: `argparse`, `csv.DictReader`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `sys`
#### Cross-cutting
- Parallelism patterns: cooperative `lid`-strided zeroing, one work-item per `bin`, `CLK_LOCAL_MEM_FENCE` sync; SPSC split producer-`head` / consumer-`tail` volatile indices
- Invariants: `BINS=256` loop bounds; power-of-two `capacity` via `n & (n-1)`, wrap via `& (capacity-1)`, one-slot reserve `capacity-1` max
- Fail-fast validation: `RINGBUF_EINVAL`/`ENOMEM`, `min_size` clamp; `KEEP` regex `^[1-9][0-9]*$`, `SOURCE_DIR` check exit 2; `REQUIRED_COLUMNS`, `date.fromisoformat`, `Decimal` `ValueError`, empty-orders STDERR exit 1
- CLI hygiene: standalone executables with defaults `KEEP=7`, `--currency EUR`, `--min-revenue 0`; `$archive.partial` + `trap` cleanup and atomic `mv`
