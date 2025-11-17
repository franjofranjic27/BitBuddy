import type {
    MarketTick,
    TradeDecision,
    OrderExecution,
    PricePoint,
} from '../types/domain';
import {
    mockMarketTicks,
    mockTradeDecisions,
    mockOrderExecutions,
    btcEurHistory,
    ethEurHistory,
} from '../mock/mockData';

export const Api = {
    async getMarketTicks(): Promise<MarketTick[]> {
        // später: fetch('/api/market-data')
        return Promise.resolve(mockMarketTicks);
    },

    async getTradeDecisions(): Promise<TradeDecision[]> {
        // später: fetch('/api/trade-decisions')
        return Promise.resolve(mockTradeDecisions);
    },

    async getOrderExecutions(): Promise<OrderExecution[]> {
        // später: fetch('/api/order-executions')
        return Promise.resolve(mockOrderExecutions);
    },

    async getPriceHistory(symbol: string): Promise<PricePoint[]> {
        const map: Record<string, PricePoint[]> = {
            'BTC/EUR': btcEurHistory,
            'ETH/EUR': ethEurHistory,
        };

        return Promise.resolve(map[symbol] ?? []);
    },
};
