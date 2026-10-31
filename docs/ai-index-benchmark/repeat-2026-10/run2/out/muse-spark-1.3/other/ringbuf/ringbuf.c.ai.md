### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:45Z
- G: 
- A: 0.0.0
- X: file
---
> Byte-circular FIFO buffer for producer-consumer byte streaming with wraparound reads/writes.
#### Purpose
- Implements power-of-two ringbuf_t circular byte queue
- Handles allocation, occupancy tracking, wraparound copy
#### Type
- C implementation file, language C
- Exported: ringbuf_init, ringbuf_free, ringbuf_used, ringbuf_free_space, ringbuf_write, ringbuf_read
- Static: is_power_of_two, min_size
#### Input
- ringbuf_t *rb with data, capacity, head, tail fields
- capacity power-of-two size for ringbuf_init
- src + len byte stream for ringbuf_write
- dst + len buffer for ringbuf_read
- Reads ringbuf.h, stdlib.h, string.h
#### Output
- ringbuf_status_t RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM
- Mutates rb->data, rb->capacity, rb->head, rb->tail
- Returns used bytes, free_space bytes, written/read byte counts
- malloced/freed rb->data heap buffer
#### Core logic
- Validates capacity via is_power_of_two bitmask check
- Uses head-tail difference masked with capacity-1 for ringbuf_used
- Reserves one slot: free_space = capacity-1-used
- Splits ringbuf_write / ringbuf_read into two memcpy chunks at array end
- Advances head/tail with modulo bitmask wrap
- Clamps transfers via min_size to free_space / used
#### Public API
- ringbuf_init(rb, capacity) -> ringbuf_status_t — validates and allocates buffer
- ringbuf_free(rb) -> void — releases buffer and resets
- ringbuf_used(rb) -> size_t — reports occupied bytes
- ringbuf_free_space(rb) -> size_t — reports writable bytes
- ringbuf_write(rb, src, len) -> size_t — enqueues bytes with wrap
- ringbuf_read(rb, dst, len) -> size_t — dequeues bytes with wrap
#### Dependencies
- ringbuf.h for ringbuf_t, ringbuf_status_t
- stdlib.h for malloc, free
- string.h for memcpy
- uint8_t, size_t, RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM
#### Exceptions / Errors
- RINGBUF_EINVAL on non-power-of-two capacity
- RINGBUF_ENOMEM on malloc failure with NULL data
