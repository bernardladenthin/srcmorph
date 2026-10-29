### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:11:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity circular buffer for single-producer single-consumer byte data transfer.

#### Purpose
- Fixed-capacity circular buffer for byte data storage and transfer
- Single-producer single-consumer ring buffer implementation

#### Responsibilities
- Implement and manage a fixed-capacity ring buffer with power-of-two capacity constraint
- Provide write and read operations with wrap-around data transfer
- Track used and free space with bitmask arithmetic
- Support initialization, cleanup, and status-returning API

#### Key units
- `ringbuf_t`: struct holding buffer data, capacity, head, tail indices
- `ringbuf_status_t`: status type with `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`
- `ringbuf_init()`: allocates buffer, validates power-of-two capacity
- `ringbuf_free()`: releases buffer memory
- `ringbuf_write()`: copies bytes into buffer with wrap-around, returns actual bytes written
- `ringbuf_read()`: copies bytes from buffer with wrap-around, returns actual bytes read
- `ringbuf_used()`: computes stored byte count via `(head - tail) & (capacity - 1)`
- `ringbuf_free_space()`: returns available write capacity

#### Data flow
- Producer calls `ringbuf_write()` with source data and length; data wraps into `ringbuf_t.data` via head index advancement
- Consumer calls `ringbuf_read()` with destination buffer; data wraps via tail index advancement
- `ringbuf_used()` and `ringbuf_free_space()` derive state from head/tail positions
- Buffer reserves one slot permanently to distinguish full from empty states

#### Dependencies
- `"ringbuf.h"`: type and status declarations
- `<stdlib.h>`: `malloc`, `free`
- `<string.h>`: `memcpy`
- `<stddef.h>`: `size_t`
- `<stdint.h>`: `uint8_t`
- C `extern "C"` linkage for C++ compatibility

#### Cross-cutting
- Power-of-two capacity validation enforced in `ringbuf_init`
- One-slot reservation pattern to distinguish full from empty buffer state
- Error handling via return codes (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) rather than exceptions
- Bitmask wrap-around arithmetic for index management
- Symmetrical write/read operations with dual-chunk copy logic for wrap-around
