### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:37Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides fixed-capacity byte-stream buffering for a single producer and single consumer, with partial reads and writes that preserve unread data across wraparound.

#### Purpose
- Allocate and manage a power-of-two circular byte buffer with `capacity - 1` usable bytes.
- Report buffered and writable byte counts, and transfer only the bytes currently available.

#### Responsibilities
- **Buffer lifecycle:** validate capacity, allocate storage, initialize indices, and release/reset state.
- **Byte transfer:** copy data into and out of the ring in up to two chunks when operations cross the buffer boundary.
- **Occupancy tracking:** compute used bytes and free space while reserving one slot to distinguish full from empty.

#### Key units
- `ringbuf_t` — holds the byte storage, capacity, and volatile `head` (next write index) and `tail` (next read index).
- `ringbuf_status_t` — defines `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` initialization results.
- `ringbuf_init` — validates and initializes a buffer of nonzero power-of-two capacity.
- `ringbuf_free` — releases storage and resets buffer fields.
- `ringbuf_write` / `ringbuf_read` — append or consume up to the available/requested number of bytes.
- `ringbuf_used` / `ringbuf_free_space` — report stored bytes and writable capacity.

#### Data flow
- Initialize `ringbuf_t` with a power-of-two capacity; allocation or validation failures return a status code.
- Producers write available bytes at `head`; consumers read stored bytes at `tail`. Both operations handle wraparound and return the number of bytes transferred.
- Occupancy queries derive counts from the indices; cleanup frees storage and resets the buffer.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>`; provides C++ linkage declarations when compiled as C++.

#### Cross-cutting
- Documents a single-producer, single-consumer usage model; `head` and `tail` are volatile.
- Capacity must be nonzero and a power of two; one slot is always reserved.
- Initialization reports invalid capacity as `RINGBUF_EINVAL` and allocation failure as `RINGBUF_ENOMEM`.
