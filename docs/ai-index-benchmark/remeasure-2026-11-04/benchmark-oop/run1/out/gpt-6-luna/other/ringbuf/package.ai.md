### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:21Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte buffering for passing data between a producer and a consumer.

#### Purpose
- Defines and implements a fixed-capacity circular buffer with partial byte reads and writes.
- Uses one reserved slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Responsibilities
- Buffer lifecycle and status: initialize storage, validate capacity, and free/reset the ring buffer.
- FIFO byte transfer: copy available bytes into or out of the buffer, including across wraparound.
- Occupancy reporting: calculate used bytes and remaining writable space.

#### Key units
- `ringbuf_t` — stores the byte-array pointer, capacity, next-write `head`, and next-read `tail`.
- `ringbuf_status_t` — reports initialization success or invalid capacity/allocation failure.
- `ringbuf_init` — allocates and initializes a ring buffer with a nonzero power-of-two capacity.
- `ringbuf_write` / `ringbuf_read` — copy up to available space or data and return the number of bytes transferred.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.
- `ringbuf_free` — releases storage and resets buffer fields.

#### Data flow
- Initialization validates capacity, allocates storage, and sets `head` and `tail` to zero.
- Writes copy available source bytes into the buffer and advance `head`; reads copy available bytes to the destination and advance `tail`.
- Transfers wrap at the array boundary; occupancy and free space are derived from the indices and capacity.

#### Dependencies
- `ringbuf.h` declares the API, structure, and status codes; `ringbuf.c` implements them.
- Uses `<stdlib.h>` for allocation, `<string.h>` for copying, and `<stddef.h>` / `<stdint.h>` for size and byte types.

#### Cross-cutting
- Power-of-two capacities enable index wraparound using a capacity mask.
- Initialization returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` on allocation failure; success is `RINGBUF_OK`.
- The header describes producer-owned `head` and consumer-owned `tail` indices and single-producer/single-consumer use.
