### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:56:21Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
A circular buffer (ring buffer) implementation that stores bytes with power-of-two sizing and provides atomic-like write/read operations.

#### Purpose
- Initializes a ring buffer with a power-of-two capacity.
- Frees allocated resources.
- Reports used space and free space.
- Writes up to `len` bytes from `src`, wrapping around if needed.
- Reads up to `len` bytes into `dst`, wrapping around if needed.

#### Type
C header/implementation file (`ringbuf.c`) for a ring buffer data structure; includes `ringbuf_t` type defined in `ringbuf.h`.

#### Input
- `ringbuf_t *rb`: pointer to buffer instance.
- `size_t capacity`: buffer size (must be power of two).
- `const uint8_t *src`: source data for write.
- `size_t len`: number of bytes to write/read.
- `const ringbuf_t *rb`: pointer to const buffer instance for used/free queries.
- `uint8_t *dst`: destination data for read.
- Internal fields: `data`, `capacity`, `head`, `tail` from `ringbuf_t`.

#### Output
- `ringbuf_status_t` status (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `size_t` used space from `ringbuf_used`.
- `size_t` free space from `ringbuf_free_space`.
- `size_t` bytes written/read (`n`).
- Side effect: updates `rb->data`, `rb->head`, `rb->tail`, sets `rb->data = NULL` on free.

#### Core logic
- Validates `capacity` is power of two via `(n & (n-1)) == 0`.
- Allocates `data` array of `capacity` bytes; returns `RINGBUF_ENOMEM` on failure.
- Sets `head = 0`, `tail = 0`.
- `ringbuf_used`: `(head - tail) & (capacity - 1)` (modular wrap).
- `ringbuf_free_space`: `capacity - 1 - used` (derived from used).
- `ringbuf_write`:
  - Computes `n = min(len, free_space)`.
  - Computes `first = min(n, capacity - head)`.
  - Copies `first` bytes to `data[head..]`, remaining `n-first` bytes to `data[0..]`.
  - Updates `head = (head + n) & (capacity - 1)`.
  - Returns `n`.
- `ringbuf_read`:
  - Computes `n = min(len, used)`.
  - Computes `first = min(n, capacity - tail)`.
  - Copies `first` bytes from `data[tail..]`, remaining `n-first` bytes from `data[0..]`.
  - Updates `tail = (tail + n) & (capacity - 1)`.
  - Returns `n`.
- `ringbuf_free`: frees `data`, resets all fields to zero/null.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: initializes buffer with power-of-two capacity; returns error if capacity invalid.
- `ringbuf_free(ringbuf_t *rb) -> void`: frees buffer memory and resets state.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: returns number of bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: returns bytes available for writing.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: writes up to `len` bytes, wrapping if buffer full; returns bytes written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: reads up to `len` bytes, wrapping if buffer empty; returns bytes read.

#### Dependencies
- `ringbuf.h` (defines `ringbuf_t`, `ringbuf_status_t`, constants).
- `<stdlib.h>` (for `malloc`, `free`).
- `<string.h>` (for `memcpy`).

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned by `ringbuf_init` if `capacity` is not a power of two or zero.
- `RINGBUF_ENOMEM`: returned by `ringbuf_init` if `malloc` fails.
- No runtime exceptions; all errors are returned via `ringbuf_status_t`.
- `ringbuf_free` assumes `rb` was initialized (does not check for NULL `data` before free; sets `data = NULL` after free).
