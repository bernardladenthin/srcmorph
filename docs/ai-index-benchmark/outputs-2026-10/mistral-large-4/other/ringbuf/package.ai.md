### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:49:42Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, lock-free single-producer single-consumer (SPSC) circular byte buffer for streaming data between threads using power-of-two sizing and bitmask wraparound.

#### Purpose
- Implements a thread-safe ring buffer for `uint8_t` byte streams where one thread produces and another consumes without locks
- Enables efficient wraparound indexing via power-of-two capacity and bitmask `& (capacity - 1)` instead of modulo

#### Responsibilities
- Lifecycle management: `ringbuf_init` validates capacity and allocates the buffer; `ringbuf_free` deallocates and zeroes the struct
- Data transfer: `ringbuf_write` enqueues bytes clamped to free space; `ringbuf_read` dequeues bytes clamped to used count; both split copies into two `memcpy` chunks across buffer wraparound
- State inspection: `ringbuf_used` and `ringbuf_free_space` report occupancy and writable space using bitmask arithmetic
- Capacity validation: enforces non-zero power-of-two capacity via bit trick `(n & (n - 1)) == 0`

#### Key units
- `ringbuf_t` — struct with `uint8_t *data`, `size_t capacity`, `volatile size_t head` (producer write index), `volatile size_t tail` (consumer read index)
- `ringbuf_status_t` — enum: `RINGBUF_OK=0`, `RINGBUF_EINVAL=-1` (zero/non-power-of-two capacity), `RINGBUF_ENOMEM=-2` (allocation failure)
- `ringbuf_init` / `ringbuf_free` — allocate/validate and release the heap buffer
- `ringbuf_write` / `ringbuf_read` — copy bytes in/out with wraparound handling, returning actual bytes transferred
- `ringbuf_used` / `ringbuf_free_space` — compute `(head - tail) & (capacity - 1)` and remaining writable bytes

#### Data flow
- Producer calls `ringbuf_write` → clamps `len` to `ringbuf_free_space` → copies via two `memcpy` chunks (end-of-buffer then index 0) → advances `head` with `(head + n) & (capacity - 1)`
- Consumer calls `ringbuf_read` → clamps `len` to `ringbuf_used` → copies via two `memcpy` chunks across wraparound → advances `tail` with `(tail + n) & (capacity - 1)`
- `volatile` head/tail provide cross-thread visibility for SPSC; one slot always remains empty to distinguish full from empty

#### Dependencies
- Internal: `ringbuf.h` provides `ringbuf_t` struct and `ringbuf_status_t` enum to `ringbuf.c`
- External C standard library: `<stdlib.h>` (`malloc`/`free`), `<string.h>` (`memcpy`), `<stddef.h>` (`size_t`), `<stdint.h>` (`uint8_t`)

#### Cross-cutting
- Power-of-two capacity constraint enables bitmask wraparound; usable capacity is `capacity - 1` due to one-slot reservation
- `volatile` indices support lock-free SPSC concurrency: `head` owned by producer, `tail` by consumer
- Silent truncation semantics: `ringbuf_write`/`ringbuf_read` clamp to available space/used bytes rather than returning errors
- Status enum pattern for initialization failures (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
