package com.ksaraev.suddenrun.user;

import lombok.Builder;

@Builder
public record GetCurrentUserResponse(String id, String name, boolean isRegistered) {}
