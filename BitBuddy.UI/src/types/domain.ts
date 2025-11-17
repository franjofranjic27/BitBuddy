export type Side = 'BUY' | 'SELL';

export interface MarketTick {
    id: string;
    exchange: string;
    symbol: string;
    price: number;
    volume: number;
    timestamp: string; // ISO String
}

export interface TradeDecision {
    id: string;
    symbol: string;
    side: Side;
    size: number;
    reason: string;
    strategy: string;
    createdAt: string;
    status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
}

export interface OrderExecution {
    id: string;
    decisionId: string;
    symbol: string;
    side: Side;
    size: number;
    price: number;
    exchange: string;
    status: 'NEW' | 'FILLED' | 'PARTIALLY_FILLED' | 'REJECTED';
    createdAt: string;
}

export interface PricePoint {
    symbol: string;
    price: number;
    timestamp: string;
}
