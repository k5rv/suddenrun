package com.ksaraev.suddenrun.exception;

import lombok.Builder;

@Builder
public record SuddenrunError(Integer status, String message) {}
