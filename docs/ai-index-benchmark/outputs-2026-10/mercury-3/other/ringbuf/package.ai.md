### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:28Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, power-of-two circular byte FIFO for lock-free single-producer/single-consumer streaming between tasks, interrupts, or hardware byte streams.

#### Purpose
- Fixed-size ring buffer for byte streaming (UART, DMA, audio, inter-thread/interrupt transfer) with non-blocking bounded read/write.
- Lock-free design: producer writes only `head`, consumer reads only `tail`; one slot reserved to disambiguate full vs empty.

#### Responsibilities
- Lifecycle: `ringbuf_init` validates capacity (non-zero, power of two), allocates storage, resets indices; `ringbuf_free` releases and zeroes state.
- Write path: `ringbuf_write` copies up to `len` bytes from `src` at `head`, wraps modulo capacity, advances `head` via mask `(head + n) & (capacity - 1)`.
- Read path: `ringbuf_read` copies up to `len` bytes to `dst` from `tail`, wraps modulo capacity, advances `tail` via mask `(tail + n) & (capacity - 1)`.
- Query: `ringbuf_used` computes `(head - tail) & (capacity - 1)`; `ringbuf_free_space` returns `capacity - 1 - used`.

#### Key units
- `ringbuf_t` — struct holding `data` pointer, `capacity`, volatile `head`/`tail`.
- `ringbuf_status_t` / `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` — init return codes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — public API surface.

#### Data flow
- Producer: `ringbuf_write(rb, src, len)` → copies up to free space into buffer at `head`, wraps, advances `head`.
- Consumer: `ringbuf_read(rb, dst, len)` → copies up to used bytes from `tail`, wraps, advances `tail`.
- `ringbuf_used` / `ringbuf_free_space` expose current occupancy to callers; both return counts possibly shorter than requested when full/empty.

#### Dependencies
- `ringbuf.h` defines types, enum, and function declarations; `ringbuf.c` provides implementation.
- Standard library: `stdlib.h` (`malloc`/`free`), `string.h` (`memcpy`), `stddef.h` (`size_t`), `stdint.h` (`uint8_t`).
- `extern "C"` guard for C++ inclusion.

#### Cross-cutting
- Invariant: capacity must be a power of two, enforced at init; all wrapping uses bitmask `(capacity - 1)`.
- Volatile `head`/`tail` for lock-free coordination; no guards against NULL `rb` or invalid data pointers.
- Errors reported only by init (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`); read/write silently truncate on full/empty buffer.
