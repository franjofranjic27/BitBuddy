import type {MarketData, OrderExecution, TradeDecision,} from '../types/domain';

declare global {
    interface Window {
        ENV?: {
            MARKET_DATA_API_URL: string;
            TRADE_DECISIONS_API_URL: string;
            ORDER_EXECUTIONS_API_URL: string;
        };
    }
}

const MARKET_DATA_API_URL = window.ENV?.MARKET_DATA_API_URL || import.meta.env.MARKET_DATA_API_URL;
const TRADE_DECISIONS_API_URL = window.ENV?.TRADE_DECISIONS_API_URL || import.meta.env.TRADE_DECISIONS_API_URL;
const ORDER_EXECUTIONS_API_URL = window.ENV?.ORDER_EXECUTIONS_API_URL || import.meta.env.ORDER_EXECUTIONS_API_URL;


export const Api = {
    async getMarketData(): Promise<MarketData[]> {
        try {
            console.log('Fetching market ticks from:', MARKET_DATA_API_URL);
            const response = await fetch(`${MARKET_DATA_API_URL}`);

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
            console.log('Fetching market ticks from:', TRADE_DECISIONS_API_URL);
            const response = await fetch(`${TRADE_DECISIONS_API_URL}`);

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
            console.log('Fetching market ticks from:', ORDER_EXECUTIONS_API_URL);
            const response = await fetch(`${ORDER_EXECUTIONS_API_URL}`);

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
            console.log('Fetching market ticks from:', MARKET_DATA_API_URL);
            const response = await fetch(`${MARKET_DATA_API_URL}/api/market-data/symbol?symbol=${symbol}`);

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
