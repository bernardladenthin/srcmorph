### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:35Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte buffering between a single producer and a single consumer, preserving order across circular wraparound.

#### Purpose
- Initialize, use, query, and release fixed-capacity byte ring buffers.
- Limit usable storage to `capacity - 1` bytes so empty and full states remain distinguishable.

#### Responsibilities
- **Buffer lifecycle:** `ringbuf_init` validates and allocates storage; `ringbuf_free` releases it and resets fields.
- **Byte transfer and occupancy:** `ringbuf_write` and `ringbuf_read` copy as many bytes as space or data allows; `ringbuf_used` and `ringbuf_free_space` report occupancy.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and volatile producer-owned `head` and consumer-owned `tail` indices.
- `ringbuf_status_t` — reports `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — public lifecycle, transfer, and occupancy API.

#### Data flow
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates storage, and initializes indices.
- Writes copy source bytes into available space; reads copy buffered bytes to the destination. Transfers can wrap at the array boundary and return the actual byte count.
- Occupancy uses masked index differences; free space reserves one sentinel slot.

#### Dependencies
- `ringbuf.c` implements the declarations and types in `ringbuf.h`.
- Uses standard C allocation and copy facilities (`stdlib.h`, `string.h`) and byte/size types (`stdint.h`, `stddef.h`).

#### Cross-cutting
- Capacity must be a nonzero power of two; index wraparound uses a capacity mask.
- Operations are bounded and may transfer fewer bytes than requested.
- The API describes an SPSC arrangement with volatile producer-owned `head` and consumer-owned `tail`; no other concurrency guarantees are specified.
