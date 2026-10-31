### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:34:32Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream in two OpenCL passes

#### Purpose
- Builds a byte histogram of an input stream
- Uses two-pass OpenCL kernel strategy for scalability

#### Type
- OpenCL kernel file (histogram.cl)
- Two kernels: histogram_partial, histogram_reduce

#### Input
- input: global uchar buffer of byte stream
- length: total byte count of input
- partial: global uint buffer for per-group partial histograms
- local_bins: local uint buffer for per-work-group bin counts
- group_count: total number of work-groups
- histogram: global uint output buffer for final histogram

#### Output
- histogram: global uint array of 256 bin counts
- partial: global uint array of group_count * 256 partial counts

#### Core logic
- Pass 1: each work-group zeroes local bins, then atomically increments local bins for each byte it processes
- Pass 1: each work-group writes its local histogram to its slice of the partial array
- Pass 2: each work-item sums the corresponding bin across all group partials
- Pass 2: each work-item writes the summed bin count to the final histogram

#### Public API
- histogram_partial(input, length, partial, local_bins) -> void: partial histogram per work-group
- histogram_reduce(partial, group_count, histogram) -> void: sums partials into final histogram

#### Dependencies
- OpenCL C 1.2 built-ins: get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier
- CLK_LOCAL_MEM_FENCE barrier flag

#### Exceptions / Errors
- None explicitly handled in source
