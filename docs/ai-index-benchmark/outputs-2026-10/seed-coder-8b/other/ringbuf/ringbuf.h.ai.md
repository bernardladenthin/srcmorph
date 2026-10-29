### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:23:45Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
- Implements a lock-free ring buffer
- Provides thread-safe operations for single-producer/single-consumer scenarios

#### Type
- Header file (C)
- Defines structures, enums, and function prototypes

#### Input
- `ringbuf_t* rb`: Pointer to ring buffer structure
- `const uint8_t* src`: Source data for write operations
- `uint8_t* dst`: Destination buffer for read operations
- `size_t len`: Number of bytes to read/write
- `size_t capacity`: Desired buffer capacity

#### Output
- `ringbuf_status_t`: Status of operations (OK, EINVAL, ENOMEM)
- `size_t`: Number of bytes actually read/written

#### Core logic
- Manages circular buffer with head and tail pointers
- Handles wrap-around for buffer boundaries
- Ensures atomic operations for thread safety

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes ring buffer
- `ringbuf_free(rb)`: Frees allocated resources
- `ringbuf_write(rb, src, len) -> size_t`: Writes data to buffer
- `ringbuf_read(rb, dst, len) -> size_t`: Reads data from buffer
- `ringbuf_used(rb) -> size_t`: Returns bytes used in buffer
- `ringbuf_free_space(rb) -> size_t`: Returns available space in buffer

#### Dependencies
- `<stddef.h>`: For `size_t` type
- `<stdint.h>`: For `uint8_t` type

#### Exceptions / Errors
- Returns error codes for invalid capacity or allocation failures
