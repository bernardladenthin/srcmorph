### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:59:18Z
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
* `const ringbuf_t *rb`: Ring buffer instance for status checks.

#### Output
* `ringbuf_status_t`: Status code from initialization.
* `void`: Memory deallocation from `ringbuf_free`.
* `size_t`: Number of bytes successfully written or read.
* `size_t`: Current used bytes or free space.

#### Core logic
* `ringbuf_init`: Allocates memory for `ringbuf_t` based on provided capacity.
* `ringbuf_free`: Deallocates memory associated with the `ringbuf_t`.
* `ringbuf_write`: Copies data into the buffer, respecting capacity and current state.
* `ringbuf_read`: Copies data out of the buffer, respecting available data.
* `ringbuf_used`/`ringbuf_free_space`: Calculates current data occupancy and available space.

#### Public API
ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: Initializes the ring buffer structure.
ringbuf_free(ringbuf_t *rb) -> void: Deallocates the memory used by the ring buffer.
ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: Writes bytes into the ring buffer.
ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: Reads bytes from the ring buffer.
ringbuf_used(const ringbuf_t *rb) -> size_t: Returns the current number of bytes used.
ringbuf_free_space(const ringbuf_t *rb) -> size_t: Returns the number of bytes available for writing.

#### Dependencies
stddef.h, stdint.h

#### Exceptions / Errors
RINGBUF_EINVAL: Capacity zero or not a power of two.
RINGBUF_ENOMEM: Memory allocation failure during initialization.
