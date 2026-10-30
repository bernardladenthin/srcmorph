### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:19Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-size power-of-two circular byte buffer for queued byte data with wraparound read and write.

#### Purpose
- Provides a lock-free-style single-producer/single-consumer style byte ring buffer (no synchronization primitives present).
- Stores bytes in a fixed capacity buffer, reusing space after reads.

#### Type
- C implementation file (`ringbuf.c`), exports functions declared in `ringbuf.h`.
- Uses `ringbuf_t` struct with fields `data`, `capacity`, `head`, `tail`.
- Static helpers: `is_power_of_two`, `min_size`.

#### Input
- `ringbuf_t *rb` target buffer.
- `size_t capacity` for init; must be a power of two.
- `const uint8_t *src` plus `size_t len` for write.
- `uint8_t *dst` plus `size_t len` for read.
- Reads `rb->head`, `rb->tail`, `rb->capacity`, `rb->data`.

#### Output
- `ringbuf_status_t` from init: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `size_t` bytes written or read (may be less than `len`).
- Mutates `rb->head`, `rb->tail`, `rb->data`, `rb->capacity`.
- Copies bytes into `rb->data` or out to `dst`.

#### Core logic
- `ringbuf_init`: validates capacity is power of two, allocates `capacity` bytes via `malloc`, resets `head` and `tail` to 0.
- Index wrap uses bitmask `& (capacity - 1)`, valid only for power-of-two capacity.
- `ringbuf_used`: occupied bytes = `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: `capacity - 1 - used`; one slot reserved to distinguish full from empty.
- `ringbuf_write`:
  - `n = min(len, free_space)`.
  - Copies in at most two `memcpy` chunks: `first` bytes to end of array, remainder from index 0.
  - Advances `head` by `n` with mask.
  - Returns `n`; partial writes possible.
- `ringbuf_read`:
  - `n = min(len, used)`.
  - Copies out in at most two chunks: `first` from `tail` to array end, remainder from index 0.
  - Advances `tail` by `n` with mask.
  - Returns `n`; partial reads possible.
- `ringbuf_free`: frees `data`, nulls pointer, zeroes `capacity`, `head`, `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` allocates buffer, validates power-of-two size.
- `ringbuf_free(ringbuf_t *rb) -> void` releases buffer, resets state.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` returns occupied byte count.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` returns writable byte count.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` copies bytes in, returns count.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` copies bytes out, returns count.

#### Dependencies
- `ringbuf.h` (declares `ringbuf_t`, `ringbuf_status_t`, status codes, prototypes).
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `<stdint.h>` implied via `uint8_t` in header.

#### Exceptions / Errors
- `RINGBUF_EINVAL` returned when capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` returned when `malloc` fails.
- No checks for NULL `rb`, `src`, or `dst`; caller must supply valid pointers.
- Capacity of 1 yields zero usable space (`free_space` = 0 ), since one slot is reserved.
