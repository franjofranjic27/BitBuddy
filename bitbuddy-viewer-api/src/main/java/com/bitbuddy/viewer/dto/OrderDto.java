// OrderDto.java
package com.bitbuddy.viewer.dto;

public record OrderDto(String id, long ts, String side, String symbol, double qty, double price, String status) {}
