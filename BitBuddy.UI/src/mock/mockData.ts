import type {
    MarketTick,
    TradeDecision,
    OrderExecution,
    PricePoint,
} from '../types/domain';

const now = new Date();

export const mockMarketTicks: MarketTick[] = [
    {
        id: 'tick-1',
        exchange: 'KRAKEN',
        symbol: 'BTC/EUR',
        price: 61500.25,
        volume: 0.015,
        timestamp: now.toISOString(),
    },
    {
        id: 'tick-2',
        exchange: 'KRAKEN',
        symbol: 'ETH/EUR',
        price: 3200.4,
        volume: 0.5,
        timestamp: now.toISOString(),
    },
];

export const mockTradeDecisions: TradeDecision[] = [
    {
        id: 'dec-1',
        symbol: 'BTC/EUR',
        side: 'BUY',
        size: 0.01,
        reason: 'MA(50) crossed above MA(200)',
        strategy: 'MA_CROSS',
        createdAt: now.toISOString(),
        status: 'ACCEPTED',
    },
    {
        id: 'dec-2',
        symbol: 'ETH/EUR',
        side: 'SELL',
        size: 0.3,
        reason: 'Stop loss hit',
        strategy: 'RISK_MANAGEMENT',
        createdAt: now.toISOString(),
        status: 'PENDING',
    },
];

export const mockOrderExecutions: OrderExecution[] = [
    {
        id: 'ord-1',
        decisionId: 'dec-1',
        symbol: 'BTC/EUR',
        side: 'BUY',
        size: 0.01,
        price: 61490.9,
        exchange: 'KRAKEN',
        status: 'FILLED',
        createdAt: now.toISOString(),
    },
    {
        id: 'ord-2',
        decisionId: 'dec-2',
        symbol: 'ETH/EUR',
        side: 'SELL',
        size: 0.3,
        price: 3195.5,
        exchange: 'KRAKEN',
        status: 'NEW',
        createdAt: now.toISOString(),
    },
];

const makeHistory = (symbol: string, basePrice: number): PricePoint[] =>
    Array.from({ length: 30 }, (_, i) => {
        const minutesAgo = (30 - i) * 2;
        const time = new Date(now.getTime() - minutesAgo * 60_000);

        const delta = Math.sin(i / 3) * (basePrice * 0.01);

        return {
            symbol,
            price: +(basePrice + delta).toFixed(2),
            timestamp: time.toISOString(),
        };
    });

export const btcEurHistory: PricePoint[] = makeHistory('BTC/EUR', 61500);
export const ethEurHistory: PricePoint[] = makeHistory('ETH/EUR', 3200);
