### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:05Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer (FIFO) for single-producer/single-consumer byte streaming, used for inter-thread or interrupt-to-task data transfer.

#### Purpose
- Provides a lock-free fixed-capacity byte ring buffer (ring buffer_t) with a power-of-two capacity and one unused slot.
- Supports single producer writer and single consumer reader with volatile head/tail indices.

#### Type
- Header (ringbuf.h) in C, with extern "C" guard for C++ inclusion.
- Defines struct ringbuf, enum ringbuf_status, and typedefs ringbuf_t, ringbuf_status_t.

#### Input
- ringbuf_init: ringbuf_t *rb and capacity (must be power of two; zero or non-power-of-two rejected).
- ringbuf_write: src byte pointer and requested len; reads len bytes from src.
- ringbuf_read: dst byte pointer and requested len; writes up to len bytes into dst.
- ringbuf_used / ringbuf_free_space: const ringbuf_t *rb.

#### Output
- ringbuf_init returns ringbuf_status_t: RINGBUF_OK, RINGBUF_EINVAL, or RINGBUF_ENOMEM; allocates rb->data and sets head/tail.
- ringbuf_write / ringbuf_read return size_t of bytes actually copied; advance head/tail and alter used/free space.
- ringbuf_free frees rb->data.
- ringbuf_used returns occupied bytes; ringbuf_free_space returns available bytes (capacity - 1 minus used).

#### Core logic
- A ring buffer holds capacity - 1 bytes; the unused slot disambiguates full versus empty.
- head and tail are volatile; head is written only by the producer, tail only by the consumer.
- ringbuf_write copies up to len bytes from src at head, wrapping modulo capacity; returns copied count.
- ringbuf_read copies up to len bytes out at tail, wrapping modulo capacity; returns copied count.
- ringbuf_free_space subtracts used from capacity - 1.
- ringbuf_init validates capacity (nonzero, power of two), allocates data via malloc (implied), and returns status.

#### Public API
- ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t — allocates and resets buffer.
- ringbuf_free(ringbuf_t *rb) -> void — releases buffer storage.
- ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t — copies in up to len bytes.
- ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t — copies out up to len bytes.
- ringbuf_used(const ringbuf_t *rb) -> size_t — returns bytes currently stored.
- ringbuf_free_space(const ringbuf_t *rb) -> size_t — returns free byte capacity.

#### Dependencies
- Includes <stddef.h> (size_t) and <stdint.h> (uint8_t).

#### Exceptions / Errors
- RINGBUF_EINVAL: capacity is zero or not a power of two.
- RINGBUF_ENOMEM: allocation of rb->data failed.
- Callers must handle NULL rb or invalid data pointers; no guards are declared in this header.
