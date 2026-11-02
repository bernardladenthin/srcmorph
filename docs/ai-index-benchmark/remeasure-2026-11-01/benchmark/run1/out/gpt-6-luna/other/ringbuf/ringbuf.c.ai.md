### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:37Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves byte streams in a fixed-capacity circular buffer while preserving FIFO order.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and bounded byte reads/writes for `ringbuf_t`.
- Uses one empty slot to distinguish full from empty.

#### Type
- C implementation file; implements declarations from `ringbuf.h`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, requested `size_t capacity`.
- `ringbuf_write`: buffer, source byte array `src`, requested byte count `len`.
- `ringbuf_read`: buffer, destination byte array `dst`, requested byte count `len`.
- Read/write operations use `rb->data`, `capacity`, `head`, and `tail`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, and `ringbuf_read` return `size_t` counts.
- `ringbuf_init` allocates buffer storage; `ringbuf_free` releases it and resets buffer fields.
- Read/write copy bytes and advance the corresponding index.

#### Core logic
- `is_power_of_two` accepts nonzero capacities with a single set bit.
- `ringbuf_init` rejects invalid capacities, allocates `capacity` bytes, and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy with `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot.
- `ringbuf_write` copies up to available space, wrapping across the end of the array in at most two chunks; advances `head` with a capacity mask.
- `ringbuf_read` copies up to the number of used bytes, wrapping in at most two chunks; advances `tail` with a capacity mask.
- `ringbuf_free` frees storage and clears `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(rb) -> void` — release and reset buffer
- `ringbuf_used(rb) -> size_t` — report buffered bytes
- `ringbuf_free_space(rb) -> size_t` — report writable bytes
- `ringbuf_write(rb, src, len) -> size_t` — append available bytes
- `ringbuf_read(rb, dst, len) -> size_t` — retrieve available bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL` — returned for capacity that is zero or not a power of two.
- `RINGBUF_ENOMEM` — returned when allocation fails.
- `RINGBUF_OK` — returned after successful initialization.

#### Dependencies
- `"ringbuf.h"` — buffer and status declarations.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- Uses `size_t` and `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` if `malloc` fails.
- No other error handling is present.
