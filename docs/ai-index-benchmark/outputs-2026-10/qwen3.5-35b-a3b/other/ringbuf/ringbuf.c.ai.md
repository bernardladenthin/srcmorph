### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:37:30Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular buffer for streaming byte data with automatic wrap-around handling.

#### Purpose
*   Provides memory-managed circular buffer operations for byte streams.
*   Ensures power-of-two capacity for efficient bitwise modulo arithmetic.

#### Type
C source file implementing `ringbuf_t` struct methods.

#### Input
*   `ringbuf_t *rb`: Pointer to ring buffer instance.
*   `size_t capacity`: Initial buffer size (must be power of two).
*   `const uint8_t *src`: Source data pointer for write.
*   `uint8_t *dst`: Destination buffer pointer for read.
*   `size_t len`: Requested transfer length.

#### Output
*   `ringbuf_status_t`: Initialization success or error codes.
*   `size_t`: Actual bytes written or read.
*   `rb->data`: Allocated memory block.
*   `rb->head`, `rb->tail`: Updated buffer pointers.

#### Core logic
*   Validates capacity is power of two using bitwise check.
*   Allocates `rb->data` via `malloc` on initialization.
*   Calculates used/free space using `(head - tail) & (capacity - 1)`.
*   Splits `memcpy` operations into two chunks to handle wrap-around.
*   Updates pointers using bitwise AND for modulo capacity.

#### Public API
*   `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Allocates and initializes buffer.
*   `ringbuf_free(rb) -> void`: Frees memory and resets state.
*   `ringbuf_used(rb) -> size_t`: Returns current number of stored bytes.
*   `ringbuf_free_space(rb) -> size_t`: Returns available capacity for writing.
*   `ringbuf_write(rb, src, len) -> size_t`: Copies data into buffer.
*   `ringbuf_read(rb, dst, len) -> size_t`: Copies data out of buffer.

#### Dependencies
*   `ringbuf.h`: Type definitions and status codes.
*   `stdlib.h`: Memory allocation functions.
*   `string.h`: Memory copy functions.

#### Exceptions / Errors
*   `RINGBUF_EINVAL`: Capacity is not power of two.
*   `RINGBUF_ENOMEM`: `malloc` returns NULL.
