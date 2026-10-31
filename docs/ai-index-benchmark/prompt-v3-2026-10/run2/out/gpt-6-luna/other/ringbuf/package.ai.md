### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:44Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded byte buffering for producer–consumer data exchange, accepting writes and reads only when capacity or buffered data is available.

#### Purpose
- Declares and implements a fixed-capacity circular byte queue with initialization, cleanup, occupancy queries, and partial reads and writes.
- Reserves one slot to distinguish full from empty, leaving `capacity - 1` bytes usable.

#### Responsibilities
- **Buffer lifecycle and validation:** allocate storage for nonzero power-of-two capacities; report invalid capacity or allocation failure; release and reset state.
- **Byte transfer and capacity tracking:** calculate used and free bytes, copy data across the circular boundary, and advance indices with a capacity mask.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, producer `head`, and consumer `tail`.
- `ringbuf_status_t` — initialization result codes: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — initialize allocated storage and release/reset it.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and writable byte counts.
- `ringbuf_write` / `ringbuf_read` — enqueue and dequeue up to the requested number of bytes.

#### Data flow
- `ringbuf_init` validates capacity and allocates storage; successful initialization sets both indices to zero.
- `ringbuf_write` copies up to available space from the source into the buffer and advances `head`.
- `ringbuf_read` copies up to the available data into the destination and advances `tail`.
- Occupancy queries derive used and free byte counts from the indices and reserved slot.

#### Dependencies
- `ringbuf.h` defines the API, types, and status values used by `ringbuf.c`.
- Standard C library: `<stdlib.h>` for allocation and release; `<string.h>` for copying.
- `<stddef.h>` and `<stdint.h>` provide `size_t` and `uint8_t`; the header supports C++ linkage through `extern "C"`.

#### Cross-cutting
- Capacity must be a nonzero power of two; index wrapping uses a capacity mask.
- `head` and `tail` are `volatile size_t`; the API documents producer/consumer roles.
- Reads and writes return actual byte counts, so insufficient data or space results in a partial transfer rather than an error.
