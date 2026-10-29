### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:44:33Z
- G: 
- A: 0.0.0
- X: file
---
> Lock-free single-producer single-consumer (SPSC) fixed-capacity byte ring buffer for inter-thread data exchange.

#### Purpose
- Provides a header-only API for a power-of-two-capacity byte ring buffer safe for one producer and one consumer without locks.
- Uses `volatile` head/tail indices to avoid cache-line contention between threads.

#### Type
- C header (`ringbuf.h`), declares one struct, one enum, and six functions.
- `ringbuf_t` struct: `uint8_t *data`, `size_t capacity`, `volatile size_t head`, `volatile size_t tail`.
- `ringbuf_status_t` enum: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.

#### Input
- `ringbuf_init`: pointer to `ringbuf_t`, `size_t capacity` (must be power of two, > 0).
- `ringbuf_write`: `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `uint8_t *dst`, `size_t len`.

#### Output
- `ringbuf_init` / `ringbuf_free`: status code or void.
- `ringbuf_write` / `ringbuf_read`: `size_t` — actual bytes copied (may be less than requested).
- `ringbuf_used` / `ringbuf_free_space`: `size_t` — current occupancy or available slots.
- Side effect: `ringbuf_init` allocates internal `data` buffer; `ringbuf_free` releases it.

#### Core logic
- Capacity is always a power of two; one slot is permanently reserved so `head == tail` means empty, and `head == tail + 1` (mod capacity) means full.
- Producer advances `head`; consumer advances `tail` — no mutex needed, only `volatile` visibility.
- `ringbuf_write` copies up to `len` bytes starting at `head`, wrapping around; returns actual count.
- `ringbuf_read` copies up to `len` bytes starting at `tail`, wrapping around; returns actual count.
- `ringbuf_used` computes `(head - tail) mod capacity`; `ringbuf_free_space` is `capacity - 1 - used`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release internal data buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — producer enqueues bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consumer dequeues bytes.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — bytes that can still be written.

#### Dependencies
- `<stddef.h>` — `size_t`
- `<stdint.h>` — `uint8_t`

#### Exceptions / Errors
- `RINGBUF_EINVAL` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` — internal allocation failed.
