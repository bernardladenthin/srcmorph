### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:34:41Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for data transfer between a single producer and a single consumer.

#### Purpose
- Provides a circular buffer for data storage.
- Allows efficient data transfer with minimal memory allocation.

#### Type
- Header file (`ringbuf.h`).
- C language.

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer structure.
- `size_t capacity`: Buffer capacity.
- `const uint8_t *src`: Pointer to the source data.
- `size_t len`: Length of data to be written.

#### Output
- `ringbuf_status_t`: Status indicating success or error.
- `uint8_t *data`: Pointer to the buffer data.
- `size_t`: Number of bytes written or read.

#### Core logic
- **Initialization**: Set up the buffer with a given capacity.
- **Freeing**: Deallocate the buffer memory.
- **Writing**: Copy data from the source to the buffer.
- **Reading**: Copy data from the buffer to the destination.
- **Status Checks**: Determine the number of used and free bytes in the buffer.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initialize the ring buffer.
- `ringbuf_free(ringbuf_t *rb)`: Free the ring buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Write data to the buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Read data from the buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: Get the number of used bytes in the buffer.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Get the number of free bytes in the buffer.

#### Dependencies
- `stddef.h`: For size_t and NULL.
- `stdint.h`: For uint8_t.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Invalid capacity (zero or not a power of two).
- `RINGBUF_ENOMEM`: Memory allocation failed.
