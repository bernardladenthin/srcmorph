### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:44:50Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense, structured markdown summary of ringbuf.c.

The file implements a circular byte buffer. It requires power-of-two capacity. The code includes helpers for alignment checks and minimum values. Core functions are init, free, used space, free space, write, and read. Write and read each split data into at most two memcpy chunks to handle wrap-around.> Byte ring buffer that stores and retrieves bytes with wraparound copies and power-of-two capacity.

#### Purpose
- Implements a byte circular buffer (`ringbuf_t`) for enqueue and dequeue of `uint8_t` data.
- Tracks occupancy with masked `head`/`tail` indexes so one slot stays unused to distinguish full from empty.

#### Type
- C implementation file for the `ringbuf.h` API; helpers `is_power_of_two` and `min_size` are `static`.
- Exported functions operate on `ringbuf_t` and return `ringbuf_status_t` or `size_t`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, power-of-two `capacity`.
- `ringbuf_write`: `ringbuf_t *rb`, source bytes `src`, requested `len`.
- `ringbuf_read`: `ringbuf_t *rb`, destination `dst`, requested `len`.
- Occupancy helpers read `head`, `tail`, and `capacity` on `ringbuf_t`.

#### Output
- `ringbuf_init` allocates `rb->data` and sets `capacity`, `head`, and `tail` to 0, or returns `RINGBUF_EINVAL` / `RINGBUF_ENOMEM`.
- `ringbuf_write` / `ringbuf_read` copy up to the available count and advance `head` or `tail` with a capacity mask; each returns bytes transferred.
- `ringbuf_free` releases `rb->data` and clears `data`, `capacity`, `head`, and `tail`.

#### Core logic
- `is_power_of_two` rejects 0 and any `n` where `n & (n - 1)` is nonzero, so the mask `capacity - 1` is valid.
- `ringbuf_used` is `(head - tail) & (capacity - 1)`; `ringbuf_free_space` is `capacity - 1 - ringbuf_used`.
- `ringbuf_write` limits `len` to free space, then `memcpy`s a first chunk from `head` to the end of `data` and a second chunk from index 0, then sets `head = (head + n) & (capacity - 1)`.
- `ringbuf_read` limits `len` to used bytes and performs the same two-chunk wrap from `tail` into `dst`, then advances `tail` with the same mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate power-of-two buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — free buffer and reset fields.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — bytes that can still be written.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue with wrap.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue with wrap.

#### Dependencies
- `ringbuf.h`
- `stdlib.h` (`malloc`, `free`)
- `string.h` (`memcpy`)

#### Exceptions / Errors
- `RINGBUF_EINVAL` when `capacity` is not a power of two.
- `RINGBUF_ENOMEM` when `malloc` fails; `rb` is left uninitialized in that path.
- Short writes and reads are not errors: returned `n` may be less than requested `len` when free space or used bytes run out.
