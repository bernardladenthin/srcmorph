### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:17:10Z
- G: 
- A: 0.0.0
- X: file
---
> Byte-stream FIFO queue buffering variable-length data with wraparound storage.
#### Purpose
- Implements power-of-two ringbuf_t circular byte buffer.
- Handles allocation, enqueue/dequeue, occupancy tracking.
#### Type
- C implementation file, exports non-static functions, static helpers.
#### Input
- rb: ringbuf_t pointer with data, capacity, head, tail.
- capacity: size_t must be power-of-two.
- src/len: const uint8_t write source and byte count.
- dst/len: uint8_t read destination and max count.
#### Output
- ringbuf_status_t RINGBUF_OK / RINGBUF_EINVAL / RINGBUF_ENOMEM.
- size_t byte counts: ringbuf_used, ringbuf_free_space, ringbuf_write, ringbuf_read.
- Mutates rb->data, rb->capacity, rb->head, rb->tail.
#### Core logic
- Validates capacity via is_power_of_two bitmask check.
- Allocates rb->data via malloc, zeroes head/tail indices.
- Computes used bytes via (head-tail) & (capacity-1) mask.
- Derives free space as capacity-1-used, reserves one slot.
- Splits ringbuf_write/ringbuf_read into two memcpy chunks at array end.
- Advances head/tail modulo capacity via & (capacity-1) mask.
- Clamps transfers via min_size to free_space / used.
#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t — validates and allocates buffer.
- ringbuf_free(rb) -> void — frees buffer and resets.
- ringbuf_used(rb) -> size_t — returns occupied bytes.
- ringbuf_free_space(rb) -> size_t — returns writable bytes.
- ringbuf_write(rb, src, len) -> size_t — enqueues up to free space.
- ringbuf_read(rb, dst, len) -> size_t — dequeues up to used bytes.
#### Dependencies
- ringbuf.h ringbuf_t, ringbuf_status_t definitions.
- stdlib.h malloc, free.
- string.h memcpy.
#### Exceptions / Errors
- RINGBUF_EINVAL on non-power-of-two capacity.
- RINGBUF_ENOMEM on malloc failure, data remains NULL.
