### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:53Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock-free single-producer single-consumer circular buffer for high-throughput byte-stream data buffering.

#### Purpose
Provides a fixed-capacity, lock-free ring buffer optimized for power-of-two sizes, enabling efficient single-producer and single-consumer data streaming.

#### Responsibilities
- **Buffer management**: Fixed-capacity circular buffer with power-of-two sizing for efficient wrap-around.
- **Concurrent access**: Volatile head/tail indices supporting single-producer/single-consumer patterns.
- **Data transfer**: Write and read operations with wrap-around copying and space/usage queries.

#### Key units
- `ringbuf_t` — struct holding data buffer, volatile head/tail indices, and capacity; the central state object.
- `ringbuf_status_t` — enum/status type with values `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` for operation results.
- `ringbuf_init` — initializes buffer with power-of-two capacity; validates input and sets head/tail to zero.
- `ringbuf_free` — releases allocated data memory.
- `ringbuf_write` — writes bytes into buffer from source, handling wrap-around via power-of-two masking; returns actual bytes copied.
- `ringbuf_read` — reads bytes from buffer into destination, handling wrap-around via power-of-two masking; returns actual bytes copied.
- `ringbuf_used` — returns current byte count via `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` — returns available bytes as `capacity - 1 - ringbuf_used()`.

#### Data flow
Producer calls `ringbuf_write` with source data and length; buffer copies up to two chunks to handle wrap-around, advances volatile head via power-of-two mask, and returns actual byte count. Consumer calls `ringbuf_read` with destination buffer and length; buffer copies up to two chunks from tail, advances volatile tail via power-of-two mask, and returns actual byte count. Query operations `ringbuf_used` and `ringbuf_free_space` read current head/tail state without modification.

#### Dependencies
- `ringbuf.h` — defines `ringbuf_t`, `ringbuf_status_t`, and constants; included by `ringbuf.c`.
- `<stdlib.h>` — for `malloc`/`free` used in initialization and cleanup.
- `<string.h>` — for `memcpy` used in data copy operations.
- C++ `extern "C"` linkage when compiled as C++.

#### Cross-cutting
- Power-of-two capacity constraint with one slot always reserved to distinguish full/empty states.
- Volatile head/tail indices for thread-safe single-producer/single-consumer access without full mutexes.
- Wrap-around copy pattern (up to two chunks) for efficient boundary crossing.
- Error handling via `RINGBUF_EINVAL` (invalid capacity) and `RINGBUF_ENOMEM` (allocation failure).
