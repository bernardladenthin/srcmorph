### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:58:07Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
A fixed-capacity byte ring buffer for single producer/single consumer with initialization, write, read, and space queries.

#### Purpose
- Provides a thread-safe, power-of-two capacity byte buffer using ring topology.
- Supports exact usage and free space reporting.

#### Type
Header file (C) defining `ringbuf_t` struct and `ringbuf_status_t` enum; usable from C++ via extern "C".

#### Input
- `ringbuf_t *rb`: pointer to buffer instance.
- `capacity`: size (power of two); zero or non-power-two triggers error.
- `const uint8_t *src` / `uint8_t *dst`: data for write/read.
- `len`: number of bytes to write/read.

#### Output
- `ringbuf_status_t`: status (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `size_t`: bytes written/read, used bytes, free space.
- Modified `head`/`tail` indices in `ringbuf_t`.

#### Core logic
- `ringbuf_init`: validates capacity (power of two, >0); allocates `data`; sets `head`=0, `tail`=0; returns `RINGBUF_OK` or `RINGBUF_EINVAL`.
- `ringbuf_write`: calculates write position using `head`; copies up to `len` bytes respecting free space; updates `head`; returns bytes written.
- `ringbuf_read`: calculates read position using `tail`; copies up to `len` bytes respecting used space; updates `tail`; returns bytes read.
- `ringbuf_used`: returns `(head - tail + capacity) % capacity` if `head != tail`, else `0` (empty) or full? Clarified: one slot unused, so max used = capacity-1. `used = (head - tail + capacity) % capacity` but if equal and not full? Standard ring: if `head == tail`, either empty or full; convention: empty when `head == tail`, full when `(head+1) % capacity == tail`. Since one slot unused, full condition avoided. `used = (head - tail + capacity) % capacity`; if `head == tail`, `used = 0` (empty). `free_space = capacity - used`.
- `ringbuf_free`: frees allocated `data`; sets fields to zero (implementation not shown, assumed).

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: initializes buffer; fails if capacity invalid.
- `ringbuf_free(ringbuf_t *rb) -> void`: releases buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: writes up to `len` bytes; returns bytes written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: reads up to `len` bytes; returns bytes read.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: returns currently used byte count.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: returns available byte count for writing.

#### Dependencies
- `<stddef.h>` (for `size_t`).
- `<stdint.h>` (for `uint8_t`).

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned if `capacity` is zero or not a power of two.
- `RINGBUF_ENOMEM`: returned if allocation fails (handled by caller via status).
- No runtime exceptions; all state changes via volatile indices for producer/consumer separation.
