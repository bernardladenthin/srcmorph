### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:57:26Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular buffer for efficient memory management and data streaming.

#### Purpose
- Manages a circular buffer with power-of-two capacity for high-performance read/write operations.
- Handles data wrapping and memory allocation without external dependencies.

#### Type
C implementation file (`ringbuf.c`)
- Struct: `ringbuf_t` (head, tail, data, capacity)
- Status enum: `ringbuf_status_t` (OK, EINVAL, ENOMEM)

#### Input
- `ringbuf_t *rb`: Pointer to the buffer structure.
- `size_t capacity`: Required power-of-two size for initialization.
- `const uint8_t *src`: Source data for write operations.
- `uint8_t *dst`: Destination buffer for read operations.
- `size_t len`: Requested data length for read/write.

#### Output
- `ringbuf_status_t`: Initialization success or error codes.
- `size_t`: Number of bytes actually written or read.
- `ringbuf_t` state: Updated `head` and `tail` indices.

#### Core logic
- Validates capacity is a power of two during initialization.
- Allocates memory for the data array and resets head/tail to zero.
- Calculates used/free space using bitwise AND masking for circular indexing.
- Splits data copies into two chunks to handle buffer wrapping (end of array to index 0).
- Updates head/tail indices using bitwise masking to wrap around the buffer capacity.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`
  Initializes circular buffer with power-of-two capacity.
- `ringbuf_free(rb) -> void`
  Releases allocated memory and resets buffer state.
- `ringbuf_used(rb) -> size_t`
  Returns current number of bytes stored in the buffer.
- `ringbuf_free_space(rb) -> size_t`
  Returns available bytes remaining before overflow.
- `ringbuf_write(rb, src, len) -> size_t`
  Writes data into buffer, handling wrap-around in two chunks.
- `ringbuf_read(rb, dst, len) -> size_t`
  Reads data from buffer, handling wrap-around in two chunks.

#### Dependencies
- `ringbuf.h`: Header defining `ringbuf_t` and status codes.
- `stdlib.h`: `malloc`, `free`.
- `string.h`: `memcpy`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if capacity is not a power of two.
- `RINGBUF_ENOMEM`: Returned if memory allocation fails.
