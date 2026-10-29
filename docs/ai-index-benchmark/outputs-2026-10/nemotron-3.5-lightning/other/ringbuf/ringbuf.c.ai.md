### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:08:34Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a circular buffer for byte data with write and read operations.

#### Purpose
- Implements a fixed-capacity circular buffer for byte data storage and transfer.

#### Type
- C implementation file (ringbuf.c) providing ring buffer functions.

#### Input
- `ringbuf_t *rb`: ring buffer state
- `size_t capacity`: buffer capacity (init only)
- `const uint8_t *src`: source data for write
- `uint8_t *dst`: destination data for read
- `size_t len`: byte count for write/read

#### Output
- `ringbuf_status_t` return: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`
- `size_t` byte counts from `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read`
- Modifies `rb->head` on write, `rb->tail` on read

#### Core logic
- `ringbuf_init`: validates power-of-two `capacity`, `malloc`s `data`, resets `head`/`tail` to 0
- `ringbuf_free`: `free`s `data`, zeroes `rb->data`, `capacity`, `head`, `tail`
- `ringbuf_used`: computes `(rb->head - rb->tail) & (rb->capacity - 1)`
- `ringbuf_free_space`: returns `rb->capacity - 1 - ringbuf_used(rb)`
- `ringbuf_write`: copies up to two chunks (end-first, then start) to wrap, updates `rb->head`
- `ringbuf_read`: copies up to two chunks (end-first, then start) to wrap, updates `rb->tail`

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: allocates ring buffer
- `ringbuf_free(ringbuf_t *rb) -> void`: frees ring buffer memory
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: returns used byte count
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: returns free byte count
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: writes bytes to buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: reads bytes from buffer

#### Dependencies
- `"ringbuf.h"`: type and status declarations
- `<stdlib.h>`: `malloc`, `free`
- `<string.h>`: `memcpy`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned when `capacity` is not a power of two
- `RINGBUF_ENOMEM`: returned when `malloc` fails
- Error handling via return codes; no exceptions
