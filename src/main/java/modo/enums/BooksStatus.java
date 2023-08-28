package modo.enums;

public enum BooksStatus {
    // 대여 가능 : AVAILABLE_RENT (대여 전용)
    // 현재 예약 중 : RESERVED_RENT (대여 전용)
    // 현재 대여 중 : RENTING (대여 전용)
    // 판매 가능 : AVAILABLE_SELL (판매 전용)
    // 현재 예약 중 : RESERVED_SELL (판매 전용)
    // 판매 완료 : SOLD (판매 전용)

    // 대여 플로우 : AVAILABLE_RENT -> RESERVED_RENT -> RENTING -> AVAILABLE_RENT
    // 판매 플로우 : AVAILABLE_SELL -> RESERVED_SELL -> SOLD
    AVAILABLE_RENT, RESERVED_RENT, RENTING, AVAILABLE_SELL, RESERVED_SELL, SOLD,
}
