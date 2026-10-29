### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:59:09Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-size, circular buffer structure for efficient, thread-safe data streaming operations.

#### Purpose
* Manages a circular buffer (`ringbuf_t`) for sequential data storage.
* Provides methods for initializing, freeing, writing, and reading data from the buffer.

#### Type
Implementation file (.c) for a ring buffer structure in C.

#### Input
* `ringbuf_t *rb`: Pointer to the ring buffer structure.
* `size_t capacity`: Desired capacity for initialization.
* `const uint8_t *src`: Source data buffer for writing.
* `uint8_t *dst`: Destination buffer for reading.
* `size_t len`: Length of data to write or read.

#### Output
* `ringbuf_status_t`: Status code from `ringbuf_init` (e.g., `RINGBUF_OK`, `RINGBUF_EINVAL`).
* `size_t`: Number of bytes successfully written or read.
* Side effect: Modifies `rb->head` and `rb->tail` pointers upon read/write.

#### Core logic
* `ringbuf_init`: Validates capacity is a power of two; allocates memory for `rb->data`.
* `ringbuf_used`: Calculates current data size using head and tail modulo capacity.
* `ringbuf_free_space`: Calculates available space in the buffer.
* `ringbuf_write`: Copies data from `src` to `rb->data`, handling wrap-around if necessary, and updates `rb->head`.
* `ringbuf_read`: Copies data from `rb->data` to `dst`, handling wrap-around if necessary, and updates `rb->tail`.

#### Public API
ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: Initializes the ring buffer structure.
ringbuf_free(ringbuf_t *rb) -> void: Releases memory allocated for the ring buffer.
ringbuf_used(const ringbuf_t *rb) -> size_t: Returns the current number of bytes stored.
ringbuf_free_space(const ringbuf_t *rb) -> size_t: Returns the available empty space.
ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: Writes data to the buffer, handling wrap-around.
ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: Reads data from the buffer, handling wrap-around.

#### Dependencies
ringbuf.h
stdlib.h
string.h

#### Exceptions / Errors
RINGBUF_EINVAL: Capacity is not a power of two during initialization.
RINGBUF_ENOMEM: Memory allocation failure during initialization.
