### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:07:09Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Buffers byte streams in a fixed-capacity circular queue, accepting and returning as many bytes as available space or data permits.

#### Purpose
- Implements ring-buffer initialization, cleanup, occupancy queries, and partial reads and writes.
- Uses one unused slot to distinguish full from empty.

#### Type
- C implementation file; defines externally visible `ringbuf_*` functions and static helpers.

#### Input
- `ringbuf_init`: destination `ringbuf_t *rb` and `size_t capacity`.
- `ringbuf_write`: buffer, source bytes `src`, and requested length `len`.
- `ringbuf_read`: buffer, destination bytes `dst`, and requested length `len`.
- Occupancy queries and cleanup take a ring-buffer pointer.

#### Output
- Initialization returns a `ringbuf_status_t`; write and read return the number of bytes transferred.
- Maintains allocated `rb->data`, `rb->capacity`, and circular `rb->head` / `rb->tail` indices.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` rejects capacities that are zero or not powers of two; allocates `capacity` bytes and initializes indices to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot, yielding `capacity - 1 - used`.
- `ringbuf_write` transfers at most the available space; copies across the array boundary in up to two chunks, then advances `head` with a capacity mask.
- `ringbuf_read` transfers at most the used bytes; copies across the array boundary in up to two chunks, then advances `tail` with a capacity mask.
- `ringbuf_free` frees storage and zeros the pointer, capacity, and indices.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report queued bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable capacity.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- No named constants or configuration variables defined.
- `capacity` must be a nonzero power of two; usable capacity is `capacity - 1`.
- Status codes referenced: `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` (values not shown).

#### Dependencies
- `"ringbuf.h"`: `ringbuf_t`, `ringbuf_status_t`, status codes, and `uint8_t`.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
