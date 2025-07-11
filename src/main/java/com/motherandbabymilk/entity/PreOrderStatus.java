package com.motherandbabymilk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(enumAsRef = true)

public enum PreOrderStatus {
    PENDING,
    CONFIRMED,
    FULFILLED,
    CANCELED
}