import type {MarketData, OrderExecution, TradeDecision,} from '../types/domain';

export const Api = {
    async getMarketData(): Promise<MarketData[]> {
        try {
            const response = await fetch('http://localhost:8080/api/market-data');

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: MarketData[] = await response.json();

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

            return data;
        } catch (error) {
            console.error('Error fetching market ticks:', error);
            throw error;
        }
    },

    async getPriceHistory(symbol: string): Promise<MarketData[]> {
        try {
            const response = await fetch('http://localhost:8080/api/market-data/symbol?symbol=' + symbol);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: MarketData[] = await response.json();

            return data;
        } catch (error) {
            console.error('Error fetching market ticks:', error);
            throw error;
        }
    },
};
