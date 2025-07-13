package com.motherandbabymilk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(enumAsRef = true)

public enum OrderStatus {
    PENDING,
    PAID,
    PROCESSING,
    COMPLETED,
    CANCELED,
    APPROVED
}
