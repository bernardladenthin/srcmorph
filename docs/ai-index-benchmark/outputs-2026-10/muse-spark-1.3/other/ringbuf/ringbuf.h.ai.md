### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:17:17Z
- G: 
- A: 0.0.0
- X: file
---
> Single-producer single-consumer FIFO byte queue with fixed power-of-two capacity.
#### Purpose
- Declares lock-free SPSC byte ring buffer API for producer-consumer byte transfer.
- Defines ringbuf_t state and ringbuf_status_t init result codes.
#### Type
- C header file, exported API declarations, no implementation.
- Defines struct ringbuf_t with data, capacity, volatile head, tail; enum ringbuf_status_t.
- C++ compatible via extern C guard.
#### Input
- ringbuf_t fields: data buffer pointer, capacity power-of-two, head write index, tail read index.
- ringbuf_init capacity: requested byte capacity, must be non-zero power-of-two.
- ringbuf_write src bytes, len requested; ringbuf_read dst buffer, len requested.
#### Output
- ringbuf_init return ringbuf_status_t, allocates data buffer on success.
- ringbuf_write / ringbuf_read return size_t actual bytes copied.
- ringbuf_used / ringbuf_free_space return size_t occupancy / available space.
- ringbuf_free releases data buffer side effect.
#### Core logic
- Circular FIFO with capacity-1 usable bytes, one slot always empty.
- Producer owns head next-write index, consumer owns tail next-read index.
- Used / free_space derived from head-tail distance modulo capacity.
- Partial write/read copies up to len limited by free_space / used.
#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` allocates power-of-two buffer
- `ringbuf_free(rb) -> void` releases buffer memory
- `ringbuf_write(rb, src, len) -> size_t` enqueues available bytes
- `ringbuf_read(rb, dst, len) -> size_t` dequeues available bytes
- `ringbuf_used(rb) -> size_t` reports occupied bytes
- `ringbuf_free_space(rb) -> size_t` reports writable bytes
#### Dependencies
-stddef.h for size_t, stdint.h for uint8_t.
#### Exceptions / Errors
- RINGBUF_OK init success.
- RINGBUF_EINVAL capacity zero or non-power-of-two.
- RINGBUF_ENOMEM allocation failed.
