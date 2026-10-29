// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/* Fixed-capacity byte ring buffer for a single producer and a single consumer. */
#ifndef RINGBUF_H
#define RINGBUF_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Capacity must be a power of two; one slot is never used, so a buffer holds capacity - 1 bytes. */
typedef struct ringbuf {
    uint8_t *data;
    size_t capacity;
    volatile size_t head; /* next write index, owned by the producer */
    volatile size_t tail; /* next read index, owned by the consumer */
} ringbuf_t;

typedef enum ringbuf_status {
    RINGBUF_OK = 0,
    RINGBUF_EINVAL = -1, /* capacity is zero or not a power of two */
    RINGBUF_ENOMEM = -2  /* allocation failed */
} ringbuf_status_t;

ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity);
void ringbuf_free(ringbuf_t *rb);

/* Copies up to len bytes in or out and returns how many were actually copied. */
size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len);
size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len);

size_t ringbuf_used(const ringbuf_t *rb);
size_t ringbuf_free_space(const ringbuf_t *rb);

#ifdef __cplusplus
}
#endif

#endif /* RINGBUF_H */
