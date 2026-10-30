### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:07Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A fixed-size power-of-two circular byte buffer for single-producer, single-consumer queuing of raw bytes with wraparound read and write.

#### Purpose
- Provides a byte FIFO ring buffer for one producer and one consumer, without locks or memory barriers.
- Reuses buffer space after reads; partial transfers are reported via return counts.

#### Responsibilities
- Lifecycle: `ringbuf_init` allocates and validates capacity; `ringbuf_free` releases and resets state.
- Transfer: `ringbuf_write` (producer, copies in from `src`) and `ringbuf_read` (consumer, copies out to `dst`), bounded by free space and used bytes.
- Occupancy queries: `ringbuf_used` and `ringbuf_free_space` derive counts from `head` and `tail`.
- Wraparound handling: index masking with `capacity - 1`, copies split into at most two `memcpy` chunks.

#### Key units
- `ringbuf_t`: struct holding `data`, `capacity`, `volatile head`, `volatile tail`.
- `ringbuf_status_t`: enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free`: allocate and release the backing byte array.
- `ringbuf_write` / `ringbuf_read`: bulk byte copy in and out, returning bytes actually transferred.
- `ringbuf_used` / `ringbuf_free_space`: readable and writable byte counts.
- `is_power_of_two`, `min_size`: static helpers in `ringbuf.c`.

#### Data flow
- Producer: `src` → `ringbuf_write` → `data` at `head` (advances `head`).
- Consumer: `data` at `tail` → `ringbuf_read` → `dst` (advances `tail`).
- Occupancy: `head - tail` masked gives used bytes; `capacity - 1 - used` gives free space.

#### Dependencies
- Internal: `ringbuf.c` implements prototypes and types declared in `ringbuf.h`.
- External: `<stdlib.h>` (`malloc`, `free`), `<string.h>` (`memcpy`), `<stdint.h>` (`uint8_t`), `<stddef.h>` (`size_t`).

#### Cross-cutting
- Concurrency: SPSC design; `volatile` on `head` and `tail` for cross-thread visibility, no explicit barriers or locks.
- Capacity rule: must be nonzero power of two; one slot reserved, so usable size is `capacity - 1` (capacity 1 yields zero usable space).
- Error handling: `RINGBUF_EINVAL` for invalid capacity, `RINGBUF_ENOMEM` for allocation failure; short read/write counts signal partial transfer, not error.
- Pointer validation: no NULL checks on `rb`, `src`, or `dst`; callers must supply valid pointers.
- C linkage: header is `extern "C"` guarded for C++ consumers.
