### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:22Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, single-producer single-consumer circular buffer for efficient byte streaming with power-of-two optimized indexing.

#### Purpose
- Implements a lock-free, circular buffer for transferring byte streams between one producer and one consumer.
- Manages memory allocation and deallocation for the buffer data structure with power-of-two sizing constraints.

#### Responsibilities
- **Buffer Lifecycle Management**: Initialization, memory allocation, and cleanup of the `ringbuf_t` instance.
- **Data Transfer Operations**: Handling read and write operations with automatic wrapping and chunking logic.
- **Capacity Validation**: Enforcing power-of-two constraints to enable efficient bitwise modulo arithmetic.
- **State Inspection**: Exposing current usage and available free space without modifying internal state.

#### Key units
- `ringbuf_t`: Core structure holding `data` pointer, `head`/`tail` indices, and `capacity`.
- `ringbuf_status_t`: Enum defining status codes `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`: Initializes the buffer with a validated power-of-two capacity.
- `ringbuf_free`: Releases allocated memory and resets internal indices.
- `ringbuf_write`: Copies bytes into the buffer, handling wrap-around in at most two chunks.
- `ringbuf_read`: Copies bytes out of the buffer, handling wrap-around in at most two chunks.
- `ringbuf_used`: Calculates current byte count using `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: Calculates remaining writable bytes as `capacity - 1 - used_space`.

#### Data flow
- **Initialization**: `ringbuf_init` validates capacity, allocates `data` buffer, and sets `head`/`tail` to zero.
- **Write Path**: `ringbuf_write` calculates free space, copies data from `src` to `data` (handling wrap), and updates `head`.
- **Read Path**: `ringbuf_read` calculates used space, copies data from `data` to `dst` (handling wrap), and updates `tail`.
- **Termination**: `ringbuf_free` deallocates `data` and resets indices to zero.

#### Dependencies
- **Internal**: `ringbuf.h` (definitions), `ringbuf.c` (implementation).
- **External**: `<stdlib.h>` (memory allocation), `<string.h>` (data copying), `<stddef.h>` (size types), `<stdint.h>` (byte types).

#### Cross-cutting
- **Concurrency Model**: Designed for single-producer single-consumer; thread safety is externally managed (lock-free).
- **Error Handling**: Returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failures.
- **Optimization**: Uses bitwise AND (`& (capacity - 1)`) for index wrapping instead of modulo operator.
- **Safety**: Reserves one slot to distinguish between full and empty states, preventing overwrites or reading unwritten data.
