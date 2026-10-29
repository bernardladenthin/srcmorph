### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:23:19Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a lock-free ring buffer for efficient data streaming

#### Purpose
- Provides a circular buffer implementation
- Enables lock-free communication between threads

#### Type
C implementation, header vs implementation

#### Input
- `ringbuf_t *rb`: Pointer to ring buffer structure
- `size_t capacity`: Desired buffer capacity
- `const uint8_t *src`: Source data for writing
- `uint8_t *dst`: Destination buffer for reading
- `size_t len`: Number of bytes to read/write

#### Output
- `ringbuf_status_t`: Status of operations (OK, EINVAL, ENOMEM)
- `size_t`: Number of bytes written/read

#### Core logic
- Initializes buffer with power-of-two capacity
- Manages head and tail pointers for read/write operations
- Handles wrap-around using bitwise AND with capacity-1
- Efficiently writes/reads data in two chunks when necessary

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes ring buffer
- `ringbuf_free(rb)`: Frees allocated memory
- `ringbuf_used(rb) -> size_t`: Returns bytes available for reading
- `ringbuf_free_space(rb) -> size_t`: Returns available write space
- `ringbuf_write(rb, src, len) -> size_t`: Writes data to buffer
- `ringbuf_read(rb, dst, len) -> size_t`: Reads data from buffer

#### Dependencies
- `stdlib.h`: For memory allocation functions
- `string.h`: For memcpy

#### Exceptions / Errors
- Returns RINGBUF_EINVAL if capacity is not power of two
- Returns RINGBUF_ENOMEM if memory allocation fails
