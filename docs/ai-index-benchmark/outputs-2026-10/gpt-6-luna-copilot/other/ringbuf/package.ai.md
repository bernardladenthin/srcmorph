### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:24Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte storage for transferring data between a producer and a consumer.

#### Purpose
- Initializes, tracks, reads, and writes a fixed-capacity circular byte buffer.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Responsibilities
- Buffer lifecycle and capacity validation: allocate and initialize only for nonzero power-of-two capacities; release and reset storage.
- FIFO transfer and occupancy queries: read or write available bytes, wrapping indices as needed.

#### Key units
- `ringbuf_t` — buffer storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — initialization status, including `RINGBUF_EINVAL` and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — initialize and release buffer resources.
- `ringbuf_write`, `ringbuf_read` — transfer available bytes in FIFO order.
- `ringbuf_used`, `ringbuf_free_space` — report occupancy and writable capacity.

#### Data flow
- `ringbuf_init` allocates storage; writes copy source bytes into available slots and advance `head`; reads copy stored bytes to the destination and advance `tail`.
- Read and write operations return the number of bytes transferred, bounded by available data or space.

#### Dependencies
- `ringbuf.h` declares the API and types used by `ringbuf.c`.
- Standard C headers: `<stdlib.h>`, `<string.h>`, `<stddef.h>`, and `<stdint.h>`.

#### Cross-cutting
- Single-producer/single-consumer design with producer-owned `head` and consumer-owned `tail`; indices wrap using the power-of-two capacity.
- Reads and writes may transfer fewer bytes than requested; initialization reports invalid capacity or allocation failure.
