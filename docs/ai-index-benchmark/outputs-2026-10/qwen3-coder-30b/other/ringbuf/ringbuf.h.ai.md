### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:19:11Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a thread-safe, fixed-capacity byte ring buffer for single producer-consumer communication.

#### Purpose
- Provides a lock-free, single-producer single-consumer ring buffer for efficient data streaming
- Offers atomic read/write operations with capacity management and status reporting

#### Type
C header file defining struct ringbuf_t and associated enums, functions

#### Input
- ringbuf_t *rb: pointer to ring buffer instance
- size_t capacity: buffer capacity (must be power of two)
- const uint8_t *src: source data for writing
- uint8_t *dst: destination buffer for reading
- size_t len: number of bytes to read/write

#### Output
- ringbuf_status_t: initialization status (OK, EINVAL, ENOMEM)
- size_t: actual bytes copied during read/write operations
- size_t: bytes used/free in buffer (ringbuf_used, ringbuf_free_space)

#### Core logic
- Maintains head/tail indices for producer/consumer with atomic access
- Enforces capacity constraint (power of two, one slot unused)
- Implements circular buffer with wraparound indexing
- Provides bounded buffer semantics with blocking-free operations
- Returns actual copied byte counts for partial operations

#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t: initializes ring buffer with given capacity
- ringbuf_free(rb) -> void: releases ring buffer memory
- ringbuf_write(rb, src, len) -> size_t: writes up to len bytes from src to buffer
- ringbuf_read(rb, dst, len) -> size_t: reads up to len bytes from buffer to dst
- ringbuf_used(rb) -> size_t: returns number of bytes currently in buffer
- ringbuf_free_space(rb) -> size_t: returns available space in buffer

#### Dependencies
- stddef.h: for size_t type
- stdint.h: for uint8_t type
- ringbuf_t struct definition
- ringbuf_status_t enum

#### Exceptions / Errors
- RINGBUF_EINVAL: returned when capacity is zero or not a power of two
- RINGBUF_ENOMEM: returned when memory allocation fails during initialization
- All operations return actual byte counts rather than error codes for read/write functions
