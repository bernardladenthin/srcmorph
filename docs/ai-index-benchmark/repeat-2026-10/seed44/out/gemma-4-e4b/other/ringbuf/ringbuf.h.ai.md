### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:33Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, single-producer, single-consumer byte ring buffer implementation.

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
* `const ringbuf_t *rb`: Ring buffer instance for status checks.

#### Output
* `ringbuf_status_t`: Status code from initialization.
* `size_t`: Number of bytes successfully written or read.
* `size_t`: Current used bytes or free space.

#### Core logic
* `ringbuf_init`: Allocates memory for `ringbuf_t` based on `capacity`.
* `ringbuf_write`: Writes data into the buffer, respecting capacity limits.
* `ringbuf_read`: Reads data from the buffer, respecting available data.
* `ringbuf_used`/`ringbuf_free_space`: Calculates current data occupancy.

#### Public API
ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: Initializes the ring buffer structure.
ringbuf_free(ringbuf_t *rb) -> void: Deallocates memory associated with the ring buffer.
ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: Copies data into the buffer.
ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: Copies data out of the buffer.
ringbuf_used(const ringbuf_t *rb) -> size_t: Returns current bytes stored in buffer.
ringbuf_free_space(const ringbuf_t *rb) -> size_t: Returns available space in buffer.

#### Dependencies
stddef.h, stdint.h

#### Exceptions / Errors
RINGBUF_EINVAL: Capacity zero or not a power of two.
RINGBUF_ENOMEM: Memory allocation failure during initialization.
