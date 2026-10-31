### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:11:55Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A circular buffer (ring buffer) implementation that stores bytes with power-of-two sizing and provides atomic-like write/read operations.

#### Purpose
- Implements a fixed-capacity byte ring buffer for single producer/single consumer usage.
- Supports initialization, writing, reading, and querying of used/free space.
- Uses power-of-two sizing for efficient indexing and wrap-around handling.

#### Responsibilities
- Buffer initialization with validation of power-of-two capacity.
- Memory allocation and cleanup.
- Tracking of used and free space via head/tail indices.
- Writing bytes with wrap-around when buffer is near full.
- Reading bytes with wrap-around when buffer is near empty.
- Providing query functions for current usage and available space.

#### Key units
- `ringbuf_t`: struct holding `data`, `capacity`, `head`, `tail` fields.
- `ringbuf_status_t`: enum with values `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init`: initializes buffer; validates capacity and allocates storage.
- `ringbuf_free`: frees allocated `data` and resets fields.
- `ringbuf_used`: returns number of bytes currently stored.
- `ringbuf_free_space`: returns bytes available for writing.
- `ringbuf_write`: writes up to `len` bytes, wrapping if needed; returns bytes written.
- `ringbuf_read`: reads up to `len` bytes, wrapping if needed; returns bytes read.

#### Data flow
- Caller invokes `ringbuf_init` with a power-of-two `capacity` to create `ringbuf_t` instance.
- `ringbuf_write` receives source data and length; computes writable space, writes in up to two segments (tail-to-end then start), updates `head`.
- `ringbuf_read` receives destination buffer and length; computes readable space, reads in up to two segments (head-to-end then start), updates `tail`.
- `ringbuf_used` and `ringbuf_free_space` compute used bytes and free bytes from `head` and `tail` using modular arithmetic with `capacity`.
- `ringbuf_free` releases `data` memory and resets internal fields.

#### Dependencies
- `ringbuf.h` (defines `ringbuf_t`, `ringbuf_status_t`, constants).
- `<stdlib.h>` (for `malloc`, `free`).
- `<string.h>` (for `memcpy`).
- `<stddef.h>` and `<stdint.h>` (via `ringbuf.h` for `size_t` and `uint8_t`).

#### Cross-cutting
- Capacity is restricted to power-of-two values (`(n & (n-1)) == 0` check) for efficient index wrapping using bitwise AND with `(capacity - 1)`.
- Index updates use `(index + n) & (capacity - 1)` to wrap correctly.
- Used space calculated as `(head - tail) & (capacity - 1)` in `ringbuf.c` and `(head - tail + capacity) % capacity` in `ringbuf.h` (consistent behavior expected).
- All errors returned via `ringbuf_status_t`; no runtime exceptions.
- `ringbuf_free` sets `data = NULL` after freeing; does not check for NULL before free.
- Designed for single producer/single consumer usage (no explicit locking mentioned, but "thread-safe" noted in header).
