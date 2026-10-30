### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:47:20Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a fixed-capacity byte ring buffer with wraparound reads and writes for stream buffering.

#### Purpose
- Implements a circular byte buffer for producer/consumer data movement.
- Provides initialization, capacity accounting, and contiguous-wraparound read/write operations.

#### Type
- C implementation module for a ring buffer.
- Exports: `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read`.
- Depends on `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`; uses `size_t`, `uint8_t`.

#### Input
- `ringbuf_t *rb` plus a required `capacity` value in `ringbuf_init`.
- `const uint8_t *src`, `size_t len` in `ringbuf_write`.
- `uint8_t *dst`, `size_t len` in `ringbuf_read`.
- Consumes buffer state fields: `rb->data`, `rb->capacity`, `rb->head`, `rb->tail`.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` and initializes `rb->data`, `rb->capacity`, `rb->head`, `rb->tail`.
- `ringbuf_free` releases `rb->data` and resets state.
- `ringbuf_used` and `ringbuf_free_space` return `size_t` occupancy/free counts.
- `ringbuf_write` returns the number of bytes copied into the buffer.
- `ringbuf_read` returns the number of bytes copied out of the buffer.
- Side effects: writes into `rb->data` and advances `rb->head`/`rb->tail`.

#### Core logic
- Validates capacity with `is_power_of_two`: `n != 0 && (n & (n - 1)) == 0`.
- Allocates memory with `malloc(capacity)` during `ringbuf_init`; returns `RINGBUF_ENOMEM` on failure.
- Rejects invalid capacities with `RINGBUF_EINVAL` when `capacity` is zero or not a power of two.
- Tracks the circular index using `(head + n) & (rb->capacity - 1)` and `(tail + n) & (rb->capacity - 1)`.
- Computes used bytes as `(rb->head - rb->tail) & (rb->capacity - 1)`.
- Computes free space as `rb->capacity - 1 - ringbuf_used(rb)`, leaving one slot unused to keep full/empty distinct.
- `ringbuf_write` copies at most `min(len, free_space)` bytes, split across two segments when wrapping: `[head .. capacity)` and `[0 .. wrap]`.
- `ringbuf_read` copies at most `min(len, used)` bytes, also splitting across two segments when wrapping.
- Uses `memcpy` for all data transfer and updates the buffer pointers after each operation.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` + initializes buffer
- `ringbuf_free(ringbuf_t *rb) -> void` + frees buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` + returns used bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` + returns free bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` + writes bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` + reads bytes

#### Dependencies
- `ringbuf.h`
- `stdlib.h`
- `string.h`
- `malloc`
- `free`
- `memcpy`
- `ringbuf_t`
- `ringbuf_status_t`
- `RINGBUF_EINVAL`
- `RINGBUF_ENOMEM`
- `RINGBUF_OK`

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` when `capacity` is not a power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` when `malloc(capacity)` fails.
- `ringbuf_write` and `ringbuf_read` perform no explicit null-checks before `memcpy`; invalid `rb`, `src`, or `dst` pointers are not guarded.
