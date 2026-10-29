### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:06:14Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity circular buffer implementation for efficient zero-copy data transfer between producers and consumers using power-of-two capacity validation and bitwise indexing.

#### Purpose
- Manages a fixed-capacity byte ring buffer (`ringbuf_t`) optimized for single-producer/single-consumer data transfer.
- Enforces power-of-two capacity constraints to enable high-performance bitwise masking for circular indexing.

#### Responsibilities
- Allocates and initializes contiguous memory blocks for buffer data with strict power-of-two capacity validation.
- Performs atomic read/write operations that wrap around buffer boundaries using modulo arithmetic via bitwise AND.
- Monitors buffer state including used bytes, free space, and head/tail indices for flow control.

#### Key units
- `ringbuf_t`: Structure defining the buffer instance with `head` (write), `tail` (read), and `data` pointers.
- `ringbuf_status_t`: Enum representing success, invalid capacity (`RINGBUF_EINVAL`), or allocation failure (`RINGBUF_ENOMEM`).
- `ringbuf_init(rb, capacity)`: Initializes buffer with validated power-of-two capacity.
- `ringbuf_free(rb)`: Deallocates memory and resets internal state.
- `ringbuf_write(rb, src, len)`: Writes up to `len` bytes from source, splitting across boundary if needed.
- `ringbuf_read(rb, dst, len)`: Reads up to `len` bytes into destination, splitting across boundary if needed.
- `ringbuf_used(rb)`: Returns current number of bytes stored.
- `ringbuf_free_space(rb)`: Returns available bytes for writing.

#### Data flow
- Input data flows from `src` pointers into the buffer via `ringbuf_write`, updating `head` and tracking used space.
- Consumed data flows from the buffer via `ringbuf_read` into `dst` pointers, updating `tail` and tracking free space.
- Initialization validates `capacity` against power-of-two requirements before allocating memory and setting initial `head` and `tail` indices.

#### Dependencies
- Internal: `ringbuf.h` (header defining structure and types), `ringbuf.c` (implementation).
- External: `<stdlib.h>` (for `malloc`/`free`), `<string.h>` (for `memcpy`), `<stddef.h>` (for `size_t`), `<stdint.h>` (for `uint8_t`).

#### Cross-cutting
- All operations assume `capacity` is a power of two to utilize bitwise masking (`& (capacity - 1)`) instead of modulo arithmetic.
- Shared error codes (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) handle invalid capacity and memory allocation failures consistently.
- Thread safety is not explicitly mentioned; operations appear to be single-producer/single-consumer based on the summary.
