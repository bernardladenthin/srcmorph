### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:44Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular buffer for efficient byte streaming with power-of-two sizing.

#### Purpose
- Provides a thread-safe (externally managed) circular buffer for reading and writing byte streams.
- Manages memory allocation and deallocation for the buffer data structure.

#### Type
- C implementation file (`ringbuf.c`)
- Implements `ringbuf_t` and `ringbuf_status_t` defined in `ringbuf.h`
- Uses power-of-two capacity for efficient bitwise modulo arithmetic

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer structure
- `size_t capacity`: Initial buffer size (must be power of two)
- `const uint8_t *src`: Source data pointer for write operations
- `uint8_t *dst`: Destination buffer pointer for read operations
- `size_t len`: Number of bytes to read or write

#### Output
- `ringbuf_status_t`: Status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
- `size_t`: Number of bytes actually written or read
- Internal state updates: `head` and `tail` indices, `data` pointer

#### Core logic
- Validates capacity is a power of two using bitwise check `(n & (n - 1)) == 0`
- Allocates memory for `data` buffer during initialization
- Calculates used space via `(head - tail) & (capacity - 1)`
- Calculates free space as `capacity - 1 - used_space`
- Writes data in at most two chunks: from `head` to end, then from index 0
- Reads data in at most two chunks: from `tail` to end, then from index 0
- Updates `head` or `tail` using bitwise AND for circular wrapping
- Frees allocated memory and resets indices on destruction

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` Initializes buffer with given capacity
- `ringbuf_free(rb) -> void` Releases buffer memory and resets state
- `ringbuf_used(rb) -> size_t` Returns current number of bytes in buffer
- `ringbuf_free_space(rb) -> size_t` Returns remaining writable bytes
- `ringbuf_write(rb, src, len) -> size_t` Copies bytes into buffer
- `ringbuf_read(rb, dst, len) -> size_t` Copies bytes out of buffer

#### Dependencies
- `ringbuf.h`: Header defining `ringbuf_t` and status types
- `stdlib.h`: `malloc`, `free`
- `string.h`: `memcpy`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if capacity is not a power of two
- `RINGBUF_ENOMEM`: Returned if `malloc` fails during initialization
- Silent truncation: `ringbuf_write` and `ringbuf_read` return actual bytes processed if buffer is full or empty
