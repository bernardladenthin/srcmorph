### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:11:45Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-size ring buffer for byte data in C.

#### Purpose
- Provides a simple, lock-free ring buffer for storing and retrieving byte data.
- Useful for producer-consumer scenarios where data is written and read in a circular fashion.

#### Type
- Implementation of a ring buffer in C.
- Header file: `ringbuf.h`
- Source file: `ringbuf.c`

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer structure.
- `size_t capacity`: Capacity of the ring buffer, must be a power of two.
- `const uint8_t *src`: Source buffer containing data to write.
- `size_t len`: Length of data to write or read.

#### Output
- `ringbuf_status_t`: Status of initialization (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `size_t`: Number of bytes written or read.
- `void`: Frees allocated memory and resets the ring buffer.

#### Core logic
- Initializes the ring buffer with a power-of-two capacity.
- Writes data to the ring buffer, handling wrap-around by copying in at most two chunks.
- Reads data from the ring buffer, handling wrap-around by copying in at most two chunks.
- Frees allocated memory and resets the ring buffer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)`: Initializes the ring buffer.
- `void ringbuf_free(ringbuf_t *rb)`: Frees the ring buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)`: Returns the number of bytes used in the ring buffer.
- `size_t ringbuf_free_space(const ringbuf_t *rb)`: Returns the number of bytes free in the ring buffer.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)`: Writes data to the ring buffer.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)`: Reads data from the ring buffer.

#### Dependencies
- `stdlib.h`: For memory allocation and deallocation.
- `string.h`: For memory copy operations.
- `ringbuf.h`: Header file containing the ring buffer structure and status codes.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Invalid capacity (not a power of two).
- `RINGBUF_ENOMEM`: Memory allocation failure.
