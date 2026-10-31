### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:11Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A fixed-capacity, power-of-two byte ring buffer in C for queuing bytes between one producer and one consumer with partial read/write semantics.

#### Purpose
- Provides a circular byte queue for transferring data between a single producer and a single consumer.
- Capacity fixed at init and must be a power of two; one slot is reserved so full and empty states are distinguishable.

#### Responsibilities
- Lifecycle: `ringbuf_init` allocates storage and validates capacity; `ringbuf_free` releases it and resets state.
- Transfer: `ringbuf_write` and `ringbuf_read` copy bytes with partial-count semantics, wrapping at the buffer end.
- Occupancy queries: `ringbuf_used` and `ringbuf_free_space` report readable and writable byte counts.
- Index arithmetic: bitmask wrapping (`capacity - 1`) instead of modulo.

#### Key units
- `ringbuf_t` (struct in `ringbuf.h`): fields `data`, `capacity`, `head` (producer-owned write index), `tail` (consumer-owned read index), both `volatile size_t`.
- `ringbuf_status_t` (enum): `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init`: validates power-of-two capacity and allocates the backing array.
- `ringbuf_free`: frees the backing array and resets fields.
- `ringbuf_write` / `ringbuf_read`: bounded, wrapping byte copy returning bytes actually moved.
- `ringbuf_used` / `ringbuf_free_space`: occupancy calculations via `(head - tail) & (capacity - 1)`.
- `is_power_of_two`, `min_size`: file-local static helpers in `ringbuf.c`.

#### Data flow
- Producer calls `ringbuf_write` → copies into `data` from `head` (split at end of array if wrapping) → `head` advances.
- Consumer calls `ringbuf_read` → copies from `data` at `tail` (split on wrap) → `tail` advances.
- Each operation clamps the request to free space or used bytes, so callers receive a short count rather than an error.

#### Dependencies
- Standard C: `<stdlib.h>` (`malloc`, `free`), `<string.h>` (`memcpy`), `<stddef.h>` (`size_t`), `<stdint.h>` (`uint8_t`).
- Internal: `ringbuf.c` implements the API declared in `ringbuf.h`.

#### Cross-cutting
- Concurrency: lock-free single-producer/single-consumer design relying on `volatile` indices; no atomics or memory barriers, so correctness under true concurrency is implicit, not enforced.
- Error handling: status enum returned only from `ringbuf_init`; read/write signal limits through short return counts.
- Memory: heap allocation owned by the caller's struct; released only via `ringbuf_free`.
- Safety gaps: `ringbuf_read`/`ringbuf_write` do not validate `rb` or buffer pointers; calling before `ringbuf_init` or freeing an uninitialized struct is undefined behavior.
- Portability: header guarded with `extern "C"` for C++ consumers.
