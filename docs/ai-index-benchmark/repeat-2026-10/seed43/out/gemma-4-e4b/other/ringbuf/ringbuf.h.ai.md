### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:28Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, single-producer/single-consumer byte ring buffer implementation.

#### Purpose
* Defines the structure and interface for a byte ring buffer.
* Manages data transfer between producer and consumer threads safely.

#### Type
Header file (C)

#### Input
* `ringbuf_t *rb`: Pointer to the ring buffer structure.
* `size_t capacity`: Desired capacity for initialization.
* `const uint8_t *src`: Source data for writing.
* `uint8_t *dst`: Destination buffer for reading.
* `const ringbuf_t *rb`: Ring buffer instance for status queries.

#### Output
* `ringbuf_status_t`: Status code from initialization.
* `size_t`: Number of bytes successfully written or read.
* `size_t`: Current used or free space in the buffer.

#### Core logic
* `ringbuf_init`: Allocates memory for the buffer data and sets initial indices.
* `ringbuf_write`: Writes data into the buffer, respecting capacity and current state.
* `ringbuf_read`: Reads data from the buffer, respecting available data.
* `ringbuf_used`/`ringbuf_free_space`: Calculates current data occupancy.

#### Public API
ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: Initializes the ring buffer structure.
ringbuf_free(ringbuf_t *rb) -> void: Deallocates memory associated with the ring buffer.
ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: Writes up to len bytes into the buffer.
ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: Reads up to len bytes from the buffer.
ringbuf_used(const ringbuf_t *rb) -> size_t: Returns the current number of bytes used.
ringbuf_free_space(const ringbuf_t *rb) -> size_t: Returns the current number of free bytes.

#### Dependencies
stddef.h, stdint.h

#### Exceptions / Errors
RINGBUF_EINVAL: Capacity zero or not a power of two.
RINGBUF_ENOMEM: Memory allocation failure during initialization.
