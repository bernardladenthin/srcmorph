### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:34:58Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-size circular buffer for efficient byte streaming between producer and consumer

#### Purpose
- Implements a lock-free ring buffer for byte data
- Supports bounded write and read operations
- Uses power-of-two capacity for fast masking

#### Type
- C header implementation (ringbuf.c)
- Struct ringbuf_t with fields data, capacity, head, tail
- Exported functions: ringbuf_init, ringbuf_free, ringbuf_used, ringbuf_free_space, ringbuf_write, ringbuf_read

#### Input
- ringbuf_t pointer rb
- size_t capacity for initialization
- const uint8_t pointer src and size_t len for write
- uint8_t pointer dst and size_t len for read

#### Output
- ringbuf_status_t return values: RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM
- size_t return values: used bytes, free space, bytes written, bytes read
- Mutated state: head, tail, data pointer

#### Core logic
- Validates capacity is power of two using bitmask check
- Allocates buffer with malloc and initializes pointers to zero
- Computes used space as head minus tail masked by capacity minus one
- Computes free space as capacity minus one minus used space
- Writes up to two chunks: first from head to end of array, then from start
- Read copies up to two chunks: first from tail to end of array, then from start
- Updates head and tail using bitwise AND with capacity minus one

#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t: initializes buffer with given capacity
- ringbuf_free(rb) -> void: frees buffer and resets state
- ringbuf_used(rb) -> size_t: returns number of bytes in buffer
- ringbuf_free_space(rb) -> size_t: returns available write space
- ringbuf_write(rb, src, len) -> size_t: writes up to len bytes from source
- ringbuf_read(rb, dst, len) -> size_t: reads up to len bytes into destination

#### Dependencies
- ringbuf.h
- stdlib.h
- string.h

#### Exceptions / Errors
- RINGBUF_EINVAL returned when capacity is not power of two
- RINGBUF_ENOMEM returned when malloc fails
