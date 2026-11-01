### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:57Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded byte-stream buffering between a producer and consumer, with partial reads and writes governed by available space and queued data.

#### Purpose
- Implements and declares a fixed-capacity circular byte queue for single-producer, single-consumer use.
- Supports partial transfers; one slot is reserved to distinguish full from empty.

#### Responsibilities
- **Buffer lifecycle and status:** initialize storage, release it, and report occupied or writable byte counts.
- **Byte transfer:** enqueue and dequeue as many bytes as capacity or queued data permits, wrapping across the array boundary.

#### Key units
- `ringbuf_t` — buffer storage, power-of-two capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — initialization results `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — allocate and initialize buffer storage, then release and reset it.
- `ringbuf_write`, `ringbuf_read` — enqueue and dequeue bytes, returning the number transferred.
- `ringbuf_used`, `ringbuf_free_space` — report queued bytes and writable capacity.

#### Data flow
- `ringbuf_init` validates a nonzero power-of-two capacity and allocates storage.
- `ringbuf_write` copies source bytes into available space; `ringbuf_read` copies queued bytes to the destination. Both may wrap and transfer fewer than requested.
- Occupancy is derived from `head` and `tail`; usable capacity is `capacity - 1`.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h` for the buffer type, status codes, and byte type.
- Standard C allocation and copy facilities: `<stdlib.h>`, `<string.h>`; the header includes `<stddef.h>` and `<stdint.h>`.

#### Cross-cutting
- Power-of-two capacity enables index wraparound with a bit mask.
- Invalid capacity returns `RINGBUF_EINVAL`; allocation failure returns `RINGBUF_ENOMEM`.
