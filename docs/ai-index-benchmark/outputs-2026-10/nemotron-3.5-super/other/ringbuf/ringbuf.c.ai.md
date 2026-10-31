### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:52:11Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular byte buffer for producer-consumer streaming with power-of-two wrap-around indexing.

#### Purpose
- Provides a non-blocking circular byte queue using ringbuf_t with head and tail indices.
- Enqueues and dequeues bytes in at most two memcpy chunks to handle array wrap-around.

#### Type
- C implementation module for a ring buffer data structure.
- Depends on ringbuf.h for ringbuf_t struct and ringbuf_status_t enum.

#### Input
- ringbuf_init(ringbuf_t *rb, size_t capacity): pointer to ringbuf_t and a power-of-two capacity.
- ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len): source byte buffer and requested write length.
- ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len): destination byte buffer and requested read length.
- ringbuf_used(const ringbuf_t *rb) and ringbuf_free_space(const ringbuf_t *rb): const ringbuf_t pointer.

#### Output
- ringbuf_init returns ringbuf_status_t: RINGBUF_OK, RINGBUF_EINVAL, or RINGBUF_ENOMEM.
- ringbuf_write returns size_t number of bytes actually enqueued and advances rb->head.
- ringbuf_read returns size_t number of bytes actually dequeued and advances rb->tail.
- ringbuf_used returns size_t occupied byte count.
- ringbuf_free_space returns size_t available write slots.
- ringbuf_free(ringbuf_t *rb) releases rb->data heap memory and zeroes ringbuf_t fields.

#### Core logic
- is_power_of_two validates capacity is non-zero and (n & (n - 1)) == 0.
- ringbuf_init allocates rb->data via malloc(capacity), then sets head and tail to zero.
- ringbuf_used computes (rb->head - rb->tail) & (rb->capacity - 1) for wrapped occupancy.
- ringbuf_free_space returns capacity - 1 - used, reserving one slot to distinguish full from empty.
- ringbuf_write clamps len to free space, copies first chunk to end of array then second chunk from index 0, and masks head with capacity - 1.
- ringbuf_read clamps len to used, copies first chunk from tail to end of array then second chunk from index 0, and masks tail with capacity - 1.

#### Public API
- ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: initializes buffer or reports invalid capacity or allocation failure.
- ringbuf_free(ringbuf_t *rb) -> void: frees backing memory and resets ringbuf_t state.
- ringbuf_used(const ringbuf_t *rb) -> size_t: reports occupied byte count.
- ringbuf_free_space(const ringbuf_t *rb) -> size_t: reports available write capacity.
- ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: enqueues bytes with wrap-around copies.
- ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: dequeues bytes with wrap-around copies.

#### Dependencies
- ringbuf.h: ringbuf_t, ringbuf_status_t, RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.
- stdlib.h: malloc, free.
- string.h: memcpy.

#### Exceptions / Errors
- ringbuf_init returns RINGBUF_EINVAL when capacity is not a power of two.
- ringbuf_init returns RINGBUF_ENOMEM when malloc returns NULL.
- ringbuf_write and ringbuf_read never fail; they clamp to available space or used bytes.
