### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:35:24Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
- Single-producer single-consumer byte ring buffer with fixed capacity
- Provides write and read operations with byte-level granularity

#### Type
- Header file defining ringbuf_t struct and ringbuf_status_t enum
- Language: C with optional C++ extern "C" guard

#### Input
- ringbuf_t pointer (rb) for initialized buffer
- const uint8_t pointer (src) for write source
- uint8_t pointer (dst) for read destination
- size_t len for number of bytes to copy
- size_t capacity for buffer allocation

#### Output
- size_t return value for ringbuf_write: bytes actually copied in
- size_t return value for ringbuf_read: bytes actually copied out
- size_t return value for ringbuf_used: bytes currently in buffer
- size_t return value for ringbuf_free_space: available write space
- ringbuf_status_t return value for ringbuf_init: initialization status

#### Core logic
- Buffer capacity must be power of two; one slot reserved so usable capacity is capacity minus one
- head index owned by producer for next write position
- tail index owned by consumer for next read position
- ringbuf_write copies up to len bytes from src into buffer at head position
- ringbuf_read copies up to len bytes from buffer at tail position into dst
- ringbuf_init allocates data buffer and initializes indices
- ringbuf_free releases allocated data buffer

#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t: initializes buffer with given capacity
- ringbuf_free(rb) -> void: releases buffer memory
- ringbuf_write(rb, src, len) -> size_t: copies bytes into buffer
- ringbuf_read(rb, dst, len) -> size_t: copies bytes out of buffer
- ringbuf_used(rb) -> size_t: returns bytes currently stored
- ringbuf_free_space(rb) -> size_t: returns available write capacity

#### Dependencies
- stddef.h
-stdint.h

#### Exceptions / Errors
- RINGBUF_EINVAL: capacity is zero or not a power of two
- RINGBUF_ENOMEM: allocation failed during initialization
