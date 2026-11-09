### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:46:40Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Buffers producer-to-consumer byte streams in FIFO order using bounded circular storage.

#### Purpose
- Provide a fixed-capacity byte ring buffer with initialization, cleanup, occupancy queries, and partial reads and writes.
- Support single-producer, single-consumer use; reserve one slot to distinguish full from empty.

#### Responsibilities
- Buffer lifecycle and validation: allocate only for nonzero power-of-two capacities, initialize indices, and release/reset storage.
- FIFO transfer and capacity tracking: copy bytes across wraparound boundaries, limiting transfers to available space or stored data.

#### Key units
- `ringbuf_t` — buffer state containing storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — initialization result codes `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — initialize and release buffer storage.
- `ringbuf_write`, `ringbuf_read` — enqueue and dequeue up to the requested byte count.
- `ringbuf_used`, `ringbuf_free_space` — report stored and writable byte counts.

#### Data flow
- `ringbuf_init` validates capacity and allocates storage; writes copy source bytes into available slots and advance `head`, while reads copy stored bytes out and advance `tail`.
- Occupancy is derived from the indices and capacity; usable storage is `capacity - 1`.

#### Dependencies
- `ringbuf.c` implements the API declared by `ringbuf.h`.
- Uses standard allocation and copy routines from `<stdlib.h>` and `<string.h>`, with `size_t` and `uint8_t` types.

#### Cross-cutting
- Power-of-two capacity enables index wraparound through a capacity mask.
- `head` and `tail` are documented as producer-owned and consumer-owned `volatile` indices; the API is intended for single-producer, single-consumer use.
- Invalid capacity and allocation failure are reported through status codes; reads and writes may transfer fewer bytes than requested.
