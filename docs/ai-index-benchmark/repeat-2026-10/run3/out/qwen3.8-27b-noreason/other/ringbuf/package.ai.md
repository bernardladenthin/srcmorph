### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, power-of-two byte ring buffer for lock-free, single-producer/single-consumer circular data storage.

#### Purpose
- Implements a thread-unsafe (via volatile indices) circular byte buffer (`ringbuf_t`) for efficient byte-level data queuing.
- Enforces power-of-two capacity to enable O(1) index wrap-around via bitwise masking.

#### Responsibilities
- **Lifecycle Management**: Initialization with capacity validation and memory allocation, and deallocation/reset.
- **Data I/O**: Enqueueing (`ringbuf_write`) and dequeueing (`ringbuf_read`) bytes with wrap-around handling.
- **State Inspection**: Querying current occupancy (`ringbuf_used`) and available capacity (`ringbuf_free_space`).

#### Key units
- `ringbuf_t`: Core struct holding `data` pointer, `capacity`, `head` (producer index), and `tail` (consumer index).
- `ringbuf_status_t`: Enum for initialization results (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init`: Validates power-of-two capacity and allocates storage.
- `ringbuf_write` / `ringbuf_read`: Perform chunked `memcpy` operations to handle buffer wrap-around, returning actual bytes transferred.
- `ringbuf_used` / `ringbuf_free_space`: Calculate occupancy and free space, reserving one slot to distinguish full/empty states.

#### Data flow
- **Write Path**: Caller provides source bytes → `ringbuf_write` copies up to `min(len, free_space)` bytes in at most two chunks (end-of-array to start) → advances `head` index via bitmask.
- **Read Path**: Caller provides destination buffer → `ringbuf_read` copies up to `min(len, used)` bytes in at most two chunks (tail to end, then start) → advances `tail` index via bitmask.
- **State Check**: `ringbuf_used` computes `(head - tail) & (capacity - 1)`; `ringbuf_free_space` returns `capacity - 1 - used`.

#### Dependencies
- **Internal**: `ringbuf.h` (type definitions), `ringbuf.c` (implementation).
- **External**: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`), `stddef.h` (`size_t`), `stdint.h` (`uint8_t`).

#### Cross-cutting
- **Concurrency**: Designed for single-producer/single-consumer lock-free usage; `head` and `tail` indices are intended to be accessed via `volatile` semantics to ensure visibility across threads.
- **Error Handling**: Initialization errors are explicit via `ringbuf_status_t`; I/O operations do not return errors but indicate partial transfers via the returned `size_t` value.
- **C++ Compatibility**: Header includes `extern "C"` guards for C++ interoperability.
