package com.ksaraev.suddenrun.user;

import lombok.Builder;

@Builder
public record GetUserResponse(String id, String name) {}
