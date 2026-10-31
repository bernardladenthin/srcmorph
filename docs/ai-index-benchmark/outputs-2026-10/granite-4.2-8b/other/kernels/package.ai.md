### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:11:08Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin byte histogram from a byte stream using two OpenCL passes: per-group counting and global reduction.

#### Purpose
- Counts byte frequencies in parallel work groups.
- Reduces partial histograms to a single 256-bin histogram.

#### Responsibilities
- Parallel per-group byte counting (`histogram_partial` kernel).
- Global reduction of partial counts (`histogram_reduce` kernel).
- Coordination via barriers and atomic operations for correctness.

#### Key units
- `histogram_partial`: kernel that initializes local bins, increments counts for input bytes in a work group, writes partial results, and synchronizes with barrier.
- `histogram_reduce`: kernel that sums partial bin counts across all groups and writes the final 256-bin histogram; returns early for bins outside range.
- `BINS`: macro constant defining histogram size (256).
- `local_bins`: local array used in pass 1 for per-group counting.

#### Data flow
- Input byte stream (`input`) and length (`length`) are processed by `histogram_partial` in parallel work groups, producing `partial` array (`group_count * BINS`).
- `histogram_reduce` reads `partial` and `group_count`, computes the sum per bin across groups, and writes the consolidated `histogram` array (`BINS` size).

#### Dependencies
- OpenCL C 1.2 primitives: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Built-in types: `uchar` (byte), `uint` (unsigned int).
- Macro `BINS` (256).

#### Cross-cutting
- Uses `barrier(CLK_LOCAL_MEM_FENCE)` after initialization and counting in pass 1 for local synchronization.
- Relies on atomic `uint` increment safety in OpenCL 1.2.
- Early return in `histogram_reduce` for bins beyond `BINS-1` to avoid unnecessary work.
- Two distinct kernels (`histogram_partial` and `histogram_reduce`) with clear separation: one for local counting, one for global reduction.
