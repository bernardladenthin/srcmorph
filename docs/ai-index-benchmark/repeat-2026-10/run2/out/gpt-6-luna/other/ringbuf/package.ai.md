### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:50Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides fixed-capacity FIFO byte buffering for producer-consumer data streams.

#### Purpose
- Initialize, measure, write, and read byte data through a circular buffer.
- Reserve one slot to distinguish full from empty, giving usable capacity of `capacity - 1`.

#### Responsibilities
- **Buffer API and state:** `ringbuf.h` declares the buffer type, status codes, and operations; `ringbuf.c` implements allocation, cleanup, occupancy tracking, and byte transfers.
- **Circular transfers:** Read and write up to available data or space, handling wraparound in two copy segments.

#### Key units
- `ringbuf_t` — stores byte data, capacity, and producer/consumer indices (`head` and `tail`).
- `ringbuf_status_t` — reports `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- `ringbuf_init` — validates and allocates a buffer with power-of-two capacity.
- `ringbuf_free` — releases storage and resets buffer fields.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and writable byte counts.
- `ringbuf_write` / `ringbuf_read` — copy bytes into and out of the circular buffer.

#### Data flow
- Initialize with a nonzero power-of-two capacity; allocation errors are returned as status codes.
- Writes copy up to available space and advance `head`; reads copy up to occupied bytes and advance `tail`.
- Index advancement uses a capacity mask; cleanup releases storage and clears state.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- `head` is the producer-owned write index and `tail` the consumer-owned read index; both indices are `volatile`.
- Power-of-two capacity and one reserved slot support masked index wraparound and full/empty distinction.
- Invalid capacities return `RINGBUF_EINVAL`; allocation failure returns `RINGBUF_ENOMEM`.
