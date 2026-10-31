### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:21Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A multi-part toolkit that counts byte frequencies on the GPU, queues bytes through a lock-free single-producer/single-consumer ring buffer, and automates directory backups and per-region currency-filtered sales reporting.

#### Purpose
- Byte-frequency analysis: GPU histogram of image or raw byte data via two OpenCL passes.
- Data plumbing and operations: a C byte ring buffer for producer/consumer transfer, plus shell and Python CLI tools for dated backup rotation and monthly revenue reports.

#### Responsibilities
- GPU histogramming (`kernels/`): per-work-group partial 256-bin counts, then a global per-bin reduction.
- Byte queuing (`ringbuf/`): fixed power-of-two circular buffer with partial read/write semantics and occupancy queries.
- Backup management (`scripts/rotate_backups.sh`): UTC-timestamped `.tar.gz` snapshots, atomic rename, retention pruning per source name.
- Sales reporting (`scripts/sales_report.py`): CSV ingestion, currency filtering, `Decimal` aggregation by year-month and region, minimum-revenue cut-off.

#### Key units
- `histogram_partial`: OpenCL `__kernel`, pass 1; local-memory `atomic_inc` counting into `partial[group_id * BINS + bin]`.
- `histogram_reduce`: OpenCL `__kernel`, pass 2; sums partials into `histogram[bin]`.
- `BINS`: macro, 256.
- `ringbuf_t`: C struct with `data`, `capacity`, `volatile size_t head` (producer), `volatile size_t tail` (consumer).
- `ringbuf_status_t`: enum `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free`: allocate and validate power-of-two capacity; release and reset.
- `ringbuf_write` / `ringbuf_read`: wrapping byte copy returning bytes moved.
- `ringbuf_used` / `ringbuf_free_space`: occupancy via `(head - tail) & (capacity - 1)`.
- `rotate_backups.sh`: Bash script creating `<name>_<YYYYMMDDTHHMMSSZ>.tar.gz` and pruning beyond `keep`.
- `Order`: frozen dataclass (`order_date`, `region`, `amount`).
- `parse_orders`: generator yielding `Order` for matching currency; raises `ValueError` on bad header or rows.
- `revenue_by_month_and_region`: sums into `dict[tuple[str, str], Decimal]`, sorted.
- `main`: Python `main(argv)` returns exit code; Bash script body serves as entry point.

#### Data flow
- GPU: input bytes → local histogram via `atomic_inc` → `partial` (BINS counters per group) → per-bin sum → `histogram`. Pass 1 must launch before pass 2, with `group_count` equal to pass 1 group count.
- Ring buffer: producer `ringbuf_write` advances `head`; consumer `ringbuf_read` advances `tail`; each call clamps to free or used bytes and returns a short count.
- Backup: source dir → `tar -czf` into `.partial` → `mv` to final archive → `ls -1t` ranks archives → entries past `keep` deleted.
- Sales: CSV files → `parse_orders` (header check, currency filter) → `Order` stream → `revenue_by_month_and_region` → `min_revenue` filter → stdout report.

#### Dependencies
- Internal: `ringbuf.c` implements `ringbuf.h`; `sales_report.py` feeds `revenue_by_month_and_region`. Kernels and ring buffer are independent; scripts share no runtime code.
- OpenCL C built-ins: `get_local_id`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- C standard library: `malloc`, `free`, `memcpy`, `size_t`, `uint8_t`.
- Bash and coreutils: `tar`, `date -u`, `du`, `ls`, `mapfile` (bash 4+), `mv`, `rm`, `mkdir`.
- Python standard library: `argparse`, `csv`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.
- Host-side OpenCL buffer allocation and launch sequencing are outside this package.

#### Cross-cutting
- Concurrency: kernels rely on work-group barriers (`CLK_LOCAL_MEM_FENCE`) that every work-item must reach; ring buffer is lock-free SPSC using `volatile` indices with no atomics or memory barriers, so thread safety is implicit.
- Error handling: kernels have none in-kernel (undersized buffers or wrong `group_count` cause out-of-bounds access); ring buffer returns a status enum only from `ringbuf_init` and signals limits via short counts; Python raises `ValueError` chained with `from e` including `path:line_no`; Bash exits 2 on bad args.
- Exit codes: `0` success; `2` bad args (Bash); `1` empty result (Python).
- Memory sizing: `local_bins` needs BINS uints per group; `partial` needs group_count × BINS counters; ring buffer memory is caller-owned via `ringbuf_free`.
- Safety gaps: ring buffer read/write do not validate pointers, so use before `ringbuf_init` is undefined behavior.
- Shell safety: `set -euo pipefail`, EXIT trap removes `.partial`, atomic `mv`; known limits are `ls -1t` breaking on newline filenames and zero-match listings aborting under pipefail.
- Reporting: no currency conversion; amounts shown with two decimals in original currency; retention scoped by source name.
