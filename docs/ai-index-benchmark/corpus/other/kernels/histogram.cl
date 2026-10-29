// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

// 256-bin byte histogram of an image or any byte stream, in two passes (OpenCL C 1.2).

#define BINS 256

// Pass 1: each work-group counts its slice into local memory, then writes one partial
// histogram of BINS counters to partial[group_id * BINS + bin].
__kernel void histogram_partial(__global const uchar *input,
                                const uint length,
                                __global uint *partial,
                                __local uint *local_bins)
{
    const uint lid = get_local_id(0);
    const uint lsize = get_local_size(0);
    const uint gid = get_global_id(0);
    const uint gsize = get_global_size(0);

    for (uint b = lid; b < BINS; b += lsize) {
        local_bins[b] = 0;
    }
    barrier(CLK_LOCAL_MEM_FENCE);

    for (uint i = gid; i < length; i += gsize) {
        atomic_inc(&local_bins[input[i]]);
    }
    barrier(CLK_LOCAL_MEM_FENCE);

    __global uint *out = partial + get_group_id(0) * BINS;
    for (uint b = lid; b < BINS; b += lsize) {
        out[b] = local_bins[b];
    }
}

// Pass 2: one work-item per bin sums the partial histograms of group_count work-groups.
__kernel void histogram_reduce(__global const uint *partial,
                               const uint group_count,
                               __global uint *histogram)
{
    const uint bin = get_global_id(0);
    if (bin >= BINS) {
        return;
    }
    uint sum = 0;
    for (uint g = 0; g < group_count; ++g) {
        sum += partial[g * BINS + bin];
    }
    histogram[bin] = sum;
}
