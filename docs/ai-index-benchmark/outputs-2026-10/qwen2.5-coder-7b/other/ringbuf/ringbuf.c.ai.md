### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:00Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a thread-safe ring buffer for efficient data storage and retrieval.

#### Purpose
- Provides a circular buffer implementation for storing and reading data in a FIFO manner.
- Ensures thread safety through the use of atomic operations.

#### Type
- C implementation file

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer structure.
- `size_t capacity`: Desired capacity of the ring buffer.
- `const uint8_t *src`: Source data to write into the buffer.
- `size_t len`: Length of the source data to write.

#### Output
- `ringbuf_status_t`: Status of the operation (success, invalid capacity, out of memory).
- `size_t`: Number of bytes written or read.

#### Core logic
- Initializes the ring buffer with a specified capacity.
- Frees the memory allocated for the ring buffer.
- Calculates the number of bytes currently used in the buffer.
- Calculates the available free space in the buffer.
- Writes data to the buffer, handling wrap-around.
- Reads data from the buffer, handling wrap-around.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)`: Initializes the ring buffer.
- `void ringbuf_free(ringbuf_t *rb)`: Frees the memory allocated for the ring buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)`: Returns the number of bytes currently used in the buffer.
- `size_t ringbuf_free_space(const ringbuf_t *rb)`: Returns the available free space in the buffer.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)`: Writes data to the ring buffer.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)`: Reads data from the ring buffer.

#### Dependencies
- `ringbuf.h`: Header file containing the ring buffer structure and function declarations.
- `stdlib.h`: For memory allocation and deallocation functions.
- `string.h`: For memory copy functions.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Invalid capacity provided (not a power of two).
- `RINGBUF_ENOMEM`: Out of memory when attempting to allocate buffer space.
