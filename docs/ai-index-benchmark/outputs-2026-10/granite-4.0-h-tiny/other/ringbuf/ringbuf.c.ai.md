### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:09:43Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
## Purpose
Calculates VAT for invoices.

## Type
C header file (`ringbuf.h`).

## Input
- `ringbuf_t *rb`: Pointer to the ring buffer.
- `size_t capacity`: Capacity of the ring buffer.
- `const uint8_t *src`: Source data to write.
- `size_t len`: Length of the source data.

## Output
- `ringbuf_status_t`: Status indicating success or error.
- `size_t`: Number of bytes written or read.

## Core Logic
- Validates capacity is a power of two.
- Allocates and frees memory for the ring buffer.
- Calculates used and free space in the buffer.
- Writes data to the ring buffer, handling wrap-around.
- Reads data from the ring buffer, handling wrap-around.

## Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes the ring buffer.
- `void ringbuf_free(ringbuf_t *rb)`: Frees the ring buffer.
- `size_t ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns used space in the buffer.
- `size_t ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns free space in the buffer.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes data to the buffer.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads data from the buffer.

## Dependencies
- `stdlib.h`: For memory allocation functions.
- `string.h`: For memory copy functions.

## Exceptions / Errors
- `RINGBUF_EINVAL`: Invalid argument (capacity not a power of two).
- `RINGBUF_ENOMEM`: Out of memory (failed allocation).
```
