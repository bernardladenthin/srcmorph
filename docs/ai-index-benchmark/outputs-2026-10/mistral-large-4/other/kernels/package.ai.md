### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:49:19Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin frequency histogram of uchar byte streams (e.g., image pixels) on GPU using a two-pass OpenCL partial-accumulate-then-reduce kernel pipeline.

#### Purpose
- Deliver GPU-accelerated histogram statistics for byte-valued input data via a two-kernel OpenCL pipeline

#### Responsibilities
- `histogram_partial`: per-work-group accumulation of byte frequencies into 256 local-memory bins using atomic increments, then export to global partial buffers
- `histogram_reduce`: final reduction summing all work-group partials into a single global 256-bin histogram

#### Key units
- `histogram_partial` — `__kernel` taking `__global const uchar *input`, `const uint length`, `__global uint *partial`, `__local uint *local_bins`; performs grid-stride traversal with `atomic_inc`
- `histogram_reduce` — `__kernel` taking `__global const uint *partial`, `const uint group_count`, `__global uint *histogram`; sums partials per bin
- `BINS` — macro constant (256) defining histogram resolution and local array size
- `local_bins` — `__local uint[256]` work-group shared counter array synchronized via `barrier(CLK_LOCAL_MEM_FENCE)`

#### Data flow
- Byte stream (`input`, `length`) → `histogram_partial` grid-stride loop → `atomic_inc(&local_bins[input[i]])` → per-group partials at `partial[get_group_id(0) * BINS + bin]` → `histogram_reduce` sums `partial[g * BINS + bin]` across `group_count` → final `histogram[bin]`

#### Dependencies
- OpenCL 1.2 C built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`
- Host launch configuration must align `length`, buffer sizes, and work-group count

#### Cross-cutting
- Double `barrier(CLK_LOCAL_MEM_FENCE)` synchronization around local-memory accumulation phase
- Grid-stride loop pattern (`i = gid; i < length; i += gsize`) for input traversal
- Early-return bounds guard (`bin >= BINS`) in reduce kernel
- No explicit error handling; assumes valid launch geometry and buffer sizing
