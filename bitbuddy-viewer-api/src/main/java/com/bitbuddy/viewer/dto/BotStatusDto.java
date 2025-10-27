// BotStatusDto.java
package com.bitbuddy.viewer.dto;

public record BotStatusDto(boolean running, String symbol, String lastSignal, long updatedAt) {}
