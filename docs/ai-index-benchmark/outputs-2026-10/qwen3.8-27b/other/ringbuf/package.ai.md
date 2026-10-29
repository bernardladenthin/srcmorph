### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:46:20Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Lock-free single-producer single-consumer (SPSC) fixed-capacity byte ring buffer for inter-thread FIFO data exchange.

#### Purpose
- Provides a minimal, allocation-backed circular byte buffer (`ringbuf_t`) safe for one producer thread and one consumer thread without mutexes.
- Guarantees O(1) read/write via power-of-two capacity and bitmask wraparound.

#### Responsibilities
- **Buffer lifecycle**: `ringbuf_init` / `ringbuf_free` manage internal `uint8_t *data` allocation and reset.
- **Producer path**: `ringbuf_write` appends bytes at `head`, advancing it; handles wraparound in at most two `memcpy` calls.
- **Consumer path**: `ringbuf_read` drains bytes from `tail`, advancing it; symmetric two-`memcpy` split.
- **Introspection**: `ringbuf_used` / `ringbuf_free_space` expose current occupancy and available capacity.

#### Key units
- `ringbuf_t` — struct holding `uint8_t *data`, `size_t capacity`, `volatile size_t head`, `volatile size_t tail`.
- `ringbuf_status_t` — enum: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — lifecycle; capacity must be power of two > 0.
- `ringbuf_write` / `ringbuf_read` — core data transfer; return actual bytes moved (may be less than requested).
- `ringbuf_used` / `ringbuf_free_space` — occupancy queries via `(head - tail) & (capacity - 1)`.
- `is_power_of_two`, `min_size` — static helpers in `ringbuf.c`.

#### Data flow
- Producer calls `ringbuf_write` → bytes copied into `data[head..]` with wraparound → `head` advanced.
- Consumer calls `ringbuf_read` → bytes copied from `data[tail..]` with wraparound → `tail` advanced.
- One slot permanently reserved so `head == tail` ⇒ empty, `head == (tail + 1) mod capacity` ⇒ full; no separate count needed.

#### Dependencies
- `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`) — implementation only.
- `stddef.h`, `stdint.h` — header types.
- No external library or table dependencies.

#### Cross-cutting
- **Concurrency model**: `volatile` on `head`/`tail` provides visibility between threads; no atomics, no locks — strictly SPSC.
- **Error handling**: only `ringbuf_init` returns a status code; `write`/`read` silently clamp to available space (no error path).
- **Sizing invariant**: capacity is always a power of two; all modular arithmetic uses `& (capacity - 1)` instead of `%`.
