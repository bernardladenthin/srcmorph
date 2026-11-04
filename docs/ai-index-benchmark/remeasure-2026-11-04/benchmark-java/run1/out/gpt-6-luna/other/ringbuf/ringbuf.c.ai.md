### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:37Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides bounded FIFO byte buffering for producers and consumers using a power-of-two ring buffer.

#### Purpose
- Stores and transfers bytes in FIFO order, including writes and reads that wrap around the buffer end.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file; defines exported `ringbuf_*` functions and static helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: ring buffer, source byte array, and requested length.
- `ringbuf_read`: ring buffer, destination byte array, and requested length.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free`: ring buffer pointer.

#### Output
- `ringbuf_init` returns initialization status; allocates `capacity` bytes and initializes buffer state on success.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_used` and `ringbuf_free_space` return occupancy and writable capacity.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities; allocation failure returns `RINGBUF_ENOMEM`.
- Tracks read and write positions with `tail` and `head`; wraps index updates using `capacity - 1` as a bitmask.
- Computes used bytes as `(head - tail) & (capacity - 1)`; reserves one slot, so writable space is `capacity - 1 - used`.
- Reads and writes at most the available bytes, copying in up to two chunks when data wraps around the array boundary.
- Advances `head` after writes and `tail` after reads.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(rb) -> void` — release and reset buffer.
- `ringbuf_used(rb) -> size_t` — report buffered bytes.
- `ringbuf_free_space(rb) -> size_t` — report writable bytes.
- `ringbuf_write(rb, src, len) -> size_t` — append available bytes.
- `ringbuf_read(rb, dst, len) -> size_t` — consume available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL` — invalid capacity.
- `RINGBUF_ENOMEM` — allocation failure.
- `RINGBUF_OK` — successful initialization.
- Capacity must be a nonzero power of two.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `<stdint.h>` — `uint8_t` (referenced by the API through the header or included declarations).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if `malloc` fails.
