### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:54Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL reduction.

#### Purpose
- Counts byte value frequencies across an input buffer in parallel.
- Produces a final 256-element histogram by reducing partial per-work-group histograms.

#### Responsibilities
- Parallel byte counting via `histogram_partial` using local memory and atomic increments.
- Global reduction of partial histograms via `histogram_reduce` into a single 256-bin output.

#### Key units
- `histogram_partial`: OpenCL kernel that initializes local bins, atomically counts bytes per work-group, and writes partial histograms to global memory.
- `histogram_reduce`: OpenCL kernel that sums partial histograms across all work-groups, one work-item per bin, into the final 256-element histogram.

#### Data flow
- Input byte stream is processed by `histogram_partial` into per-work-group partial histograms in global memory.
- `histogram_reduce` reads all partial histograms and sums them per bin to produce the final 256-bin histogram.

#### Dependencies
- OpenCL C 1.2 builtins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`.
- Macro `BINS` defined as 256.

#### Cross-cutting
- Two-pass reduction pattern: partial counting followed by global reduction.
- Local memory fence synchronization between counting and copying phases in `histogram_partial`.
- Out-of-range bin guard with early return in `histogram_reduce`.
