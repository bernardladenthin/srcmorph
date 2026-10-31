### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:23Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Heterogeneous byte-stream and ops utilities for histogram counting, SPSC FIFO transfer, and backup/sales reporting.
#### Purpose
- Low-level byte-stream primitives plus standalone admin/reporting scripts with no shared runtime.
#### Responsibilities
- 256-bin byte histogram: per-group partial counting via `histogram_partial`, global reduction via `histogram_reduce`.
- SPSC circular byte FIFO: power-of-two `ringbuf_t` with wraparound enqueue/dequeue and occupancy tracking.
- Operational utilities: timestamped `tar.gz` backup rotation with `KEEP` retention, CSV monthly regional revenue aggregation.
#### Key units
- `histogram_partial(input, length, partial, local_bins)` — grid-stride counting with `atomic_inc` into `local_bins[256]`.
- `histogram_reduce(partial, group_count, histogram)` — per-bin sum of `partial[g * BINS + bin]` into `histogram[bin]`.
- `BINS 256` — fixed bin count for byte values 0-255.
- `ringbuf_t` — circular storage with `data/capacity/head/tail`.
- `ringbuf_status_t` — `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(rb, capacity)` — validates power-of-two, allocates buffer.
- `ringbuf_free(rb)` — releases buffer, resets state.
- `ringbuf_write(rb, src, len)` / `ringbuf_read(rb, dst, len)` — partial-copy wraparound transfer returning actual counts.
- `ringbuf_used(rb)` / `ringbuf_free_space(rb)` — occupancy from masked `head-tail` distance.
- `rotate_backups.sh(source_dir, backup_dir, [keep])` — creates `{basename}_YYYYMMDDTHHMMSSZ.tar.gz`, enforces newest-N retention.
- `Order` frozen dataclass — `order_date`, `region`, `amount` typed record.
- `parse_orders(path, currency)` — `csv.DictReader` iterator validating `REQUIRED_COLUMNS`, `order_date`, `amount`.
- `revenue_by_month_and_region(orders)` — `defaultdict(Decimal)` grouping by `YYYY-MM` and `region`.
- `main(argv)` — aggregates `csv_files`, prints TSV `YYYY-MM region revenue currency`.
#### Data flow
- Histogram: `input` + `length` -> strided loop `i = gid; i < length; i += gsize` -> `local_bins[input[i]]` -> `partial[group_count * BINS]` -> `histogram[256]`.
- Ringbuf: `ringbuf_init` -> heap `data` buffer -> `ringbuf_write` clamped to `free_space` via two-chunk `memcpy`, advances `head` -> `ringbuf_read` clamped to `used` via two-chunk `memcpy`, advances `tail`.
- Scripts: `SOURCE_DIR` -> `tar -czf` timestamped archive in `BACKUP_DIR` -> `ls -1t` newest-N `KEEP` pruning; CSV orders -> `parse_orders` -> `revenue_by_month_and_region` -> TSV stdout via atomic `.partial` + `mv`.
#### Dependencies
- OpenCL C 1.2 builtins: `get_local_id`, `get_global_id`, `get_group_id`, `get_local_size`, `get_global_size`, `barrier(CLK_LOCAL_MEM_FENCE)`, `atomic_inc`.
- C stdlib: `stdlib.h malloc/free`, `string.h memcpy`, `stddef.h size_t`, `stdint.h uint8_t`; internal `ringbuf.c` -> `ringbuf.h`.
- Bash/GNU coreutils: `bash set -euo pipefail`, `tar -czf`, `mapfile`, `trap`, `basename`, `dirname`, `date -u`, `ls -1t`, `du -h`, `mkdir -p`, `mv`, `rm`.
- Python stdlib only: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- No internal cross-file collaboration between `kernels/`, `ringbuf/`, `scripts/`; independently executable.
#### Cross-cutting
- Bitmask modulo `capacity-1` wraparound, one-slot-reserved full/empty distinction, SPSC `head`/`tail` volatile split.
- Two-kernel split to avoid global atomics; `barrier(CLK_LOCAL_MEM_FENCE)` after zero/count phases.
- Partial-copy/clamped transfer semantics and grid-stride access patterns.
- Strict CLI validation: `KEEP` regex `^[1-9][0-9]*$`, `SOURCE_DIR` existence, `REQUIRED_COLUMNS`, `date.fromisoformat`, `Decimal`; exit 2/1 and `ValueError path:line_no`, `RINGBUF_EINVAL`/`RINGBUF_ENOMEM` error signaling.
