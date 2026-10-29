### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:19:09Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-size circular buffer for efficient data streaming with atomic read/write operations.

#### Purpose
- Provides a lock-free, memory-efficient circular buffer for data queuing
- Supports concurrent access patterns with head/tail pointer management

#### Type
C implementation file, exports ringbuf_t structure and related functions

#### Input
- ringbuf_t* buffer instance
- capacity size (must be power of two)
- source data buffer for writes
- destination buffer for reads
- length parameters for read/write operations

#### Output
- ringbuf_status_t status codes (OK, EINVAL, ENOMEM)
- number of bytes actually read/written
- modified buffer state (head/tail pointers)

#### Core logic
- Validates capacity is power of two before initialization
- Allocates memory for fixed-size buffer storage
- Uses bitwise operations for efficient modulo arithmetic
- Implements wraparound logic using mask (capacity - 1)
- Handles multi-segment copies during read/write operations
- Manages head/tail pointer updates with bit masking

#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t (initializes buffer)
- ringbuf_free(rb) -> void (frees buffer memory)
- ringbuf_used(rb) -> size_t (returns occupied space)
- ringbuf_free_space(rb) -> size_t (returns available space)
- ringbuf_write(rb, src, len) -> size_t (writes data to buffer)
- ringbuf_read(rb, dst, len) -> size_t (reads data from buffer)

#### Dependencies
- stdlib.h (malloc, free)
- string.h (memcpy)
- ringbuf.h (header file defining ringbuf_t and status types)

#### Exceptions / Errors
- Returns RINGBUF_EINVAL when capacity is not power of two
- Returns RINGBUF_ENOMEM when memory allocation fails
- Returns RINGBUF_OK on successful operations
