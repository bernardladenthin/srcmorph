### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:37:43Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a fixed-capacity byte ring buffer for single-producer single-consumer data transfer.

#### Purpose
*   Declares data structures for circular buffer management in C.
*   Enforces power-of-two capacity constraints for efficient indexing.

#### Type
*   C header file.
*   Struct `ringbuf_t` with `head` (producer) and `tail` (consumer) indices.
*   Enum `ringbuf_status_t` for operation result codes.

#### Input
*   `ringbuf_t *rb`: Buffer instance reference.
*   `size_t capacity`: Required power-of-two buffer size.
*   `uint8_t *src` / `dst`: Data buffers for read/write operations.
*   `size_t len`: Transfer length limits.

#### Output
*   `ringbuf_status_t`: Initialization result code.
*   `size_t`: Actual bytes transferred or space available.
*   Memory allocation on init, deallocation on free.

#### Core logic
*   Declares `head` owned by producer, `tail` owned by consumer.
*   Requires capacity to be power of two.
*   Ensures buffer holds capacity minus one bytes.
*   Returns actual bytes copied if transfer limit exceeded.

#### Public API
*   `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Allocates and sets up buffer.
*   `ringbuf_free(rb) -> void`: Destroys buffer and frees memory.
*   `ringbuf_write(rb, src, len) -> size_t`: Pushes bytes to buffer.
*   `ringbuf_read(rb, dst, len) -> size_t`: Pops bytes from buffer.
*   `ringbuf_used(rb) -> size_t`: Queries occupied space.
*   `ringbuf_free_space(rb) -> size_t`: Queries available space.

#### Dependencies
*   `stddef.h`
*   `stdint.h`

#### Exceptions / Errors
*   `RINGBUF_EINVAL`: Invalid capacity (zero or non-power-of-two).
*   `RINGBUF_ENOMEM`: Allocation failure.
