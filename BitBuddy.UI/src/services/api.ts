import type {MarketTick, OrderExecution, PricePoint, TradeDecision,} from '../types/domain';
import {btcEurHistory, ethEurHistory,} from '../mock/mockData';

export const Api = {
    async getMarketTicks(): Promise<MarketTick[]> {
        try {
            const response = await fetch('http://localhost:8080/api/market-data');

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: MarketTick[] = await response.json();
            console.log('Fetched market ticks:', data);

            // return Promise.resolve(mockMarketTicks);
            return data;
        } catch (error) {
            console.error('Error fetching market ticks:', error);
            throw error;
        }
    },

    async getTradeDecisions(): Promise<TradeDecision[]> {
        try {
            const response = await fetch('http://localhost:8081/api/orders');

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: TradeDecision[] = await response.json();
            console.log('Fetched trades:', data);

            // return Promise.resolve(mockTradeDecisions);

            return data;
        } catch (error) {
            console.error('Error fetching market ticks:', error);
            throw error;
        }
    },

    async getOrderExecutions(): Promise<OrderExecution[]> {
        try {
            const response = await fetch('http://localhost:8082/api/trades');

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: OrderExecution[] = await response.json();
            console.log('Fetched trades:', data);

            // return Promise.resolve(mockOrderExecutions);

            return data;
        } catch (error) {
            console.error('Error fetching market ticks:', error);
            throw error;
        }
    },

    async getPriceHistory(symbol: string): Promise<PricePoint[]> {
        const map: Record<string, PricePoint[]> = {
            'BTC/EUR': btcEurHistory,
            'ETH/EUR': ethEurHistory,
        };

        return Promise.resolve(map[symbol] ?? []);
    },
};
