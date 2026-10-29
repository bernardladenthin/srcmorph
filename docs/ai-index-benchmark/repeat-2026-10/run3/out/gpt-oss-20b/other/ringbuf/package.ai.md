### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:57:16Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams in a single‑producer/single‑consumer context.

#### Purpose
- Fixed‑size ring buffer for byte streams.
- Lock‑free enqueue/dequeue with wrap‑around.

#### Responsibilities
- Buffer lifecycle: init and free.
- Byte write and read with wrap‑around.
- Query used and free space.

#### Key units
- `ringbuf_t` struct (ringbuf.h).
- `ringbuf_status_t` enum (OK, EINVAL, ENOMEM).
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` functions.

#### Data flow
- `ringbuf_init` → allocate data array, set head/tail to 0.
- `ringbuf_write` → copy up to `len` bytes, wrap at capacity, update head.
- `ringbuf_read` → copy up to `len` bytes, wrap at capacity, update tail.
- `ringbuf_used` / `ringbuf_free_space` → compute state via modulo arithmetic.

#### Dependencies
- `<stdlib.h>`, `<string.h>`, `<stddef.h>`, `<stdint.h>`.
- Header `ringbuf.h` defines types and constants.

#### Cross-cutting
- Capacity validation: `is_power_of_two`.
- Wrap‑around logic using `(index + len) & (capacity - 1)`.
- Single‑producer/single‑consumer design; no locks.
