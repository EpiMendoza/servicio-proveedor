package com.pay.pal.vo;

public record RequestUserVo(
        String name,
        String lastName,
        int year
) {
    public RequestUserVo {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name not null");
        }
        if (lastName == null || name.isBlank()) {
            throw new IllegalArgumentException("Lastname not null");
        }
        if (year <= 0) {
            throw new IllegalArgumentException("Year must be positive");
        }
    }

}
