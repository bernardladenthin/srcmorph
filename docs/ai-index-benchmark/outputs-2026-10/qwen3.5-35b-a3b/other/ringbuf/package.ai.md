### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:39:49Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a fixed-capacity circular buffer for streaming byte data with automatic wrap-around handling.

#### Purpose
*   Provides memory-managed circular buffer operations for byte streams.
*   Ensures power-of-two capacity for efficient bitwise modulo arithmetic.

#### Responsibilities
*   Manage single-producer single-consumer byte data transfer.
*   Handle memory allocation and deallocation for buffer storage.
*   Enforce capacity constraints and wrap-around logic during I/O.

#### Key units
*   `ringbuf_t`: Struct with `head` (producer) and `tail` (consumer) indices.
*   `ringbuf_status_t`: Enum for operation result codes.
*   `ringbuf_init`: Allocates and initializes buffer.
*   `ringbuf_free`: Destroys buffer and frees memory.
*   `ringbuf_write`: Pushes bytes to buffer.
*   `ringbuf_read`: Pops bytes from buffer.
*   `ringbuf_used`: Queries occupied space.
*   `ringbuf_free_space`: Queries available space.

#### Data flow
*   `ringbuf_init` allocates `rb->data` based on power-of-two `capacity`.
*   `ringbuf_write` copies `src` to `rb->data` updating `head` with wrap-around.
*   `ringbuf_read` copies `rb->data` to `dst` updating `tail` with wrap-around.
*   `ringbuf_used` and `ringbuf_free_space` calculate space using bitwise AND on indices.
*   `memcpy` operations split into two chunks to handle wrap-around.

#### Dependencies
*   Internal: `ringbuf.h` (type definitions and status codes).
*   External: `stdlib.h` (memory allocation), `string.h` (memory copy), `stddef.h`, `stdint.h`.

#### Cross-cutting
*   Error handling via `ringbuf_status_t` codes (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
*   Power-of-two capacity validation enforced across all operations.
*   Bitwise modulo arithmetic used for index management.
