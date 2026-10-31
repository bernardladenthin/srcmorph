### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:45:40Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity byte ring buffer for single-producer single-consumer byte streaming between threads.

#### Purpose
- Fixed-capacity byte ring buffer for SPSC (single producer, single consumer) concurrency
- Power-of-two capacity with one unused slot to distinguish full from empty states

#### Type
- C header file (ringbuf.h) with include guard RINGBUF_H and extern "C" C++ compatibility
- Struct ringbuf_t: uint8_t *data, size_t capacity, volatile size_t head (producer write index), volatile size_t tail (consumer read index)
- Enum ringbuf_status_t: RINGBUF_OK=0, RINGBUF_EINVAL=-1, RINGBUF_ENOMEM=-2

#### Input
- ringbuf_init: ringbuf_t *rb, size_t capacity (must be non-zero power of two)
- ringbuf_write: ringbuf_t *rb, const uint8_t *src, size_t len
- ringbuf_read: ringbuf_t *rb, uint8_t *dst, size_t len
- ringbuf_used / ringbuf_free_space: const ringbuf_t *rb

#### Output
- ringbuf_init: ringbuf_status_t (RINGBUF_OK, RINGBUF_EINVAL, or RINGBUF_ENOMEM)
- ringbuf_write / ringbuf_read: size_t count of bytes actually copied
- ringbuf_used / ringbuf_free_space: size_t byte counts
- ringbuf_free: releases allocated data buffer

#### Core logic
- Capacity must be power of two; one slot never used so usable capacity is capacity - 1 bytes
- head owned by producer (next write), tail owned by consumer (next read); both volatile for cross-thread visibility
- ringbuf_write copies up to len bytes from src into ring, returns actual bytes written
- ringbuf_read copies up to len bytes from ring into dst, returns actual bytes read
- ringbuf_used reports bytes currently stored; ringbuf_free_space reports bytes available for writing

#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t: validate capacity and allocate data buffer
- ringbuf_free(rb) -> void: deallocate data buffer
- ringbuf_write(rb, src, len) -> size_t: copy bytes into ring, return count written
- ringbuf_read(rb, dst, len) -> size_t: copy bytes out of ring, return count read
- ringbuf_used(rb) -> size_t: return bytes currently buffered
- ringbuf_free_space(rb) -> size_t: return writable bytes remaining

#### Dependencies
- <stddef.h> for size_t
- <stdint.h> for uint8_t

#### Exceptions / Errors
- RINGBUF_EINVAL: capacity is zero or not a power of two
- RINGBUF_ENOMEM: allocation failed
