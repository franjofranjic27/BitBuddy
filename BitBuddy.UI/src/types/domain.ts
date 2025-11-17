export type Side = 'BUY' | 'SELL';

export interface MarketData {
    id: string;
    symbol: string;
    price: number;
    amount: number;
    type: string;
    timestamp: string; // ISO String
}

export interface TradeDecision {
    id: string;
    base: string;
    counter: string;
    orderStyle: string;
    orderType: string;
    amount: number;
    createdAt: string;
}

export interface OrderExecution {
    id: string;
    base: string;
    counter: string;
    orderStyle: string;
    orderType: string;
    amount: number;
    createdAt: string;
}

export interface PricePoint {
    symbol: string;
    price: number;
    timestamp: string;
}
