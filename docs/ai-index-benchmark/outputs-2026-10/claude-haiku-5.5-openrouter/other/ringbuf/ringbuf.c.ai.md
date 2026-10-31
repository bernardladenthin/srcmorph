### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:16Z
- G: 
- A: 0.0.0
- X: file
---
> Lock-free-style byte ring buffer in C with power-of-two capacity, wrapping read and write.

#### Purpose
- Fixed-size circular byte buffer for queuing data between producer and consumer.
- Power-of-two capacity enables bitmask index wrapping; one slot kept empty to distinguish full from empty.

#### Type
- C implementation file (not static-only; exports ringbuf API declared in `ringbuf.h`).
- Struct type `ringbuf_t` with fields `data`, `capacity`, `head`, `tail`; status enum `ringbuf_status_t`.
- Two file-local static helpers: `is_power_of_two`, `min_size`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used`, `ringbuf_free_space`: `const ringbuf_t *rb`.
- Fields read: `rb->head`, `rb->tail`, `rb->capacity`, `rb->data`.

#### Output
- `ringbuf_init` returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`; sets `data`, `capacity`, `head`, `tail`.
- `ringbuf_free` releases `data`, resets all fields to NULL/0.
- `ringbuf_used` / `ringbuf_free_space` return byte counts.
- `ringbuf_write` returns bytes actually written; advances `rb->head`.
- `ringbuf_read` returns bytes actually read; advances `rb->tail`.
- Side effects: heap allocation via `malloc`, heap release via `free`, memory copies into `rb->data` and caller buffers.

#### Core logic
- Validate capacity is power of two (`n != 0 && (n & (n-1)) == 0`) in `ringbuf_init`; else `RINGBUF_EINVAL`.
- Allocate `capacity` bytes with `malloc`; on failure `RINGBUF_ENOMEM`.
- Occupancy: `used = (head - tail) & (capacity - 1)`.
- Free space: `capacity - 1 - used` (one slot reserved to separate full from empty).
- Write:
  - `n = min(len, free_space)` (partial writes allowed).
  - `first = min(n, capacity - head)` bytes copied to end of array.
  - Remaining `n - first` bytes copied from index 0 (wrap).
  - `head = (head + n) & (capacity - 1)`.
- Read:
  - `n = min(len, used)`.
  - `first = min(n, capacity - tail)` copied from `tail` to end of array.
  - Remaining `n - first` copied from index 0 (wrap).
  - `tail = (tail + n) & (capacity - 1)`.
- Wrap-around handled via bitmask instead of modulo.
- Not thread-safe: no atomics or locks; single-producer/single-consumer assumptions only implicit.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` : allocate buffer, validate power-of-two capacity.
- `ringbuf_free(ringbuf_t *rb) -> void` : free buffer, reset state.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` : count of stored bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` : count of writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` : copy in, return bytes written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` : copy out, return bytes read.

#### Dependencies
- `ringbuf.h` (declares `ringbuf_t`, `ringbuf_status_t`, `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `uint8_t`).
- `<stdlib.h>` (`malloc`, `free`, `NULL`).
- `<string.h>` (`memcpy`).

#### Exceptions / Errors
- `RINGBUF_EINVAL` when capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` when `malloc` returns NULL.
- No exceptions; partial read/write reported via returned byte count.
- `ringbuf_write`/`ringbuf_read` do not check for NULL `rb` or `data`; calling before `ringbuf_init` is undefined behavior.
- `ringbuf_free` on an uninitialized struct frees an indeterminate pointer.
