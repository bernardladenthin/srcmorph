### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:48Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Buffers and retrieves byte streams through a fixed-capacity ring buffer, preserving FIFO order across wraparound.

#### Purpose
- Provides initialization, cleanup, usage queries, and bounded reads and writes for a byte ring buffer.
- Reserves one slot to distinguish full from empty.

#### Type
- C implementation file; implements the `ringbuf.h` API.

#### Input
- `ringbuf_init`: buffer pointer `rb`, power-of-two `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, requested `len`.
- `ringbuf_read`: buffer `rb`, destination `dst`, requested `len`.
- Usage queries and `ringbuf_free`: ring-buffer pointer `rb`.

#### Output
- Initialization returns a `ringbuf_status_t`; read and write return the number of bytes transferred.
- Maintains `rb->data`, `capacity`, `head`, and `tail`; `ringbuf_free` releases storage and resets these fields.

#### Core logic
- `ringbuf_init` rejects zero or non-power-of-two capacities; allocates `capacity` bytes and initializes indices to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts occupancy and the reserved slot from capacity.
- `ringbuf_write` copies up to available space in at most two chunks, wrapping at the array boundary; advances `head` with a capacity mask.
- `ringbuf_read` copies up to available data in at most two chunks, wrapping at the array boundary; advances `tail` with a capacity mask.
- `ringbuf_free` releases the allocation and clears buffer state.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — status values referenced from `ringbuf.h`; definitions are not present here.

#### Dependencies
- `"ringbuf.h"` — buffer types, status type, and status values.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `uint8_t` is used for byte buffers.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
