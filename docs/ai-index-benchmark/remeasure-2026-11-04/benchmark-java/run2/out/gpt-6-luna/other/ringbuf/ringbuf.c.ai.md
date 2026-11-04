### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:35Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides bounded FIFO byte buffering with wraparound for producer-consumer data transfer.

#### Purpose
- Implements a dynamically allocated ring buffer for writing and reading byte sequences.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C implementation file; defines `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, and `ringbuf_read` for `ringbuf_t`.

#### Input
- `ringbuf_init`: buffer pointer `rb` and requested `capacity`.
- `ringbuf_write`: buffer `rb`, source byte pointer `src`, and requested length `len`.
- `ringbuf_read`: buffer `rb`, destination byte pointer `dst`, and requested length `len`.
- Buffer state: `data`, `capacity`, `head`, and `tail`, as accessed through `ringbuf_t`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; initializes allocated storage and indices on success.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities; allocates `capacity` bytes and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts occupancy and the reserved slot from capacity.
- `ringbuf_write` copies at most the available space, splitting the copy at the end of the allocation if it wraps; advances `head` with a capacity mask.
- `ringbuf_read` copies at most the available data, likewise splitting at wraparound; advances `tail` with a capacity mask.
- `ringbuf_free` frees the allocation and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes

#### Constants and configuration
- `RINGBUF_EINVAL` — returned for zero or non-power-of-two capacity.
- `RINGBUF_ENOMEM` — returned when allocation fails.
- `RINGBUF_OK` — returned after successful initialization.

#### Dependencies
- `"ringbuf.h"` — `ringbuf_t`, `ringbuf_status_t`, status codes, and declarations.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- Invalid capacity returns `RINGBUF_EINVAL`; allocation failure returns `RINGBUF_ENOMEM`.
- No explicit null-pointer checks for `rb`, `src`, or `dst`.
