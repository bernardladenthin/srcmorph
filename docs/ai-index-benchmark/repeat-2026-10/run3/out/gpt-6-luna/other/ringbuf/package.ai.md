### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:53Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a bounded FIFO byte queue for passing streamed data between a single producer and a single consumer.

#### Purpose
- Defines and implements a fixed-capacity circular buffer with initialization, cleanup, read/write, and occupancy queries.
- Uses one reserved slot to distinguish full from empty, giving usable capacity of `capacity - 1`.

#### Responsibilities
- **Buffer lifecycle:** allocate storage for a valid capacity and release/reset it.
- **Byte transfer:** enqueue and dequeue up to the requested number of bytes, including across the wraparound boundary.
- **Capacity tracking:** report queued bytes and writable space.

#### Key units
- `ringbuf_t` — byte storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — status codes for success, invalid capacity, and allocation failure.
- `ringbuf_init` — initialize storage for a nonzero power-of-two capacity.
- `ringbuf_free` — release storage and reset buffer fields.
- `ringbuf_used` / `ringbuf_free_space` — report occupancy and writable capacity.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue bytes and return the number transferred.

#### Data flow
- Initialize a `ringbuf_t` with a power-of-two capacity; storage is allocated and indices start at zero.
- Writes copy available bytes from a source into the buffer and advance `head`; reads copy queued bytes to a destination and advance `tail`.
- Occupancy is derived from the indices and capacity; cleanup frees storage and resets the buffer.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Power-of-two capacity enables masked index wraparound.
- Invalid capacities return `RINGBUF_EINVAL`; allocation failure returns `RINGBUF_ENOMEM`.
- The interface describes producer-owned `head` and consumer-owned `tail` indices for single-producer/single-consumer use.
