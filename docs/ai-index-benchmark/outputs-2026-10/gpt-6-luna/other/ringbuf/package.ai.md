### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:22:54Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides fixed-capacity FIFO byte buffering for producer/consumer data streams.

#### Purpose
- Initialize, manage, and transfer bytes through a circular buffer with capacity-minus-one usable slots.
- Support single-producer/single-consumer use with occupancy and free-space queries.

#### Responsibilities
- Buffer lifecycle: allocate and initialize power-of-two capacity; free storage and reset state.
- FIFO transfers: write available bytes and read buffered bytes, handling wraparound in up to two copy segments.
- Capacity management: report used and writable bytes while reserving one slot to distinguish full from empty.

#### Key units
- `ringbuf_t` — buffer storage, capacity, and volatile `head`/`tail` indices.
- `ringbuf_status_t` — initialization status codes `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — allocate or release buffer resources.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue up to the requested byte count.
- `ringbuf_used` / `ringbuf_free_space` — query occupied and available bytes.

#### Data flow
- `ringbuf_init` validates nonzero power-of-two capacity, allocates storage, and resets indices.
- Writes copy source bytes into available slots; reads copy occupied bytes to the destination. Both may transfer fewer bytes than requested and advance masked indices across wraparound.
- Occupancy queries derive used and free capacity from the current indices.

#### Dependencies
- `ringbuf.h` defines the public types, status codes, and C-compatible API.
- `<stdlib.h>` provides allocation and release; `<string.h>` provides `memcpy`.
- `<stddef.h>` and `<stdint.h>` provide `size_t` and `uint8_t` in the interface.

#### Cross-cutting
- Power-of-two capacity enables index wraparound via a capacity mask.
- One slot remains unused; usable capacity is `capacity - 1`.
- `head` and `tail` are volatile and described as producer-owned and consumer-owned, respectively.
