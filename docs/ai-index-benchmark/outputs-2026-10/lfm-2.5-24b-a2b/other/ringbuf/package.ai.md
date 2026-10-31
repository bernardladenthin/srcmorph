### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:46:09Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity circular byte buffer for efficient single-producer single-consumer data streaming with read/write operations.

#### Purpose
- Implements a power-of-two sized ring buffer for storing and retrieving byte data.
- Validates capacity is a power of two and manages producer/consumer head and tail pointers.

#### Responsibilities
- Buffer lifecycle: initialization, allocation, and cleanup.
- Read/write operations with wrap-around handling for sequential data transfer.
- Status reporting via ringbuf_status_t codes for initialization and error conditions.
- Single-producer single-consumer semantics without per-call dynamic allocation.

#### Key units
- ringbuf_t: struct holding data pointer, capacity, volatile head and tail indices.
- ringbuf_status_t: enum with RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.
- ringbuf_init: initializes buffer, validates capacity, allocates memory.
- ringbuf_free: releases allocated buffer and resets fields.
- ringbuf_write: copies up to two chunks into buffer, handles wrap, returns bytes written.
- ringbuf_read: copies stored data from tail to destination, handles wrap, returns bytes read.
- ringbuf_used: returns bytes currently stored.
- ringbuf_free_space: returns remaining free space.

#### Data flow
- ringbuf_init: checks power-of-two capacity, allocates buffer, sets head/tail to zero.
- ringbuf_write: copies source data into buffer at head, wraps modulo capacity, advances head, returns count.
- ringbuf_read: copies stored data from tail to destination, wraps, advances tail, returns count.
- ringbuf_used / ringbuf_free_space: compute used and free space from head/tail and capacity.
- ringbuf_free: frees buffer and resets state.

#### Dependencies
- ringbuf.h: defines ringbuf_t struct and ringbuf_status_t enum.
- stdlib.h: malloc and free for allocation.
- string.h: memcpy for data copying.
- stdint.h: uint8_t type.
- stddef.h: standard types.

#### Cross-cutting
- Capacity must be a power of two; invalid capacity triggers RINGBUF_EINVAL.
- Allocation failure triggers RINGBUF_ENOMEM.
- Volatile head/tail indices enable safe producer/consumer coordination.
- No per-call dynamic allocation beyond the initial buffer allocation.
