import React, {useEffect, useState} from 'react';
import {PageLayout} from './components/layout/PageLayout';
import type {TabKey} from './components/layout/NavBar';
import {NavBar} from './components/layout/NavBar';
import {Api} from './services/api';
import {MarketTable} from './components/market/MarketTable';
import {DecisionTable} from './components/decisions/DecisionTable';
import {OrdersTable} from './components/orders/OrdersTable';
import {PriceChart} from './components/market/PriceChart';
import type {MarketData, OrderExecution, TradeDecision,} from './types/domain';

const App: React.FC = () => {
    const [activeTab, setActiveTab] = useState<TabKey>('overview');
    const [marketData, setMarketData] = useState<MarketData[]>([]);
    const [decisions, setDecisions] = useState<TradeDecision[]>([]);
    const [orders, setOrders] = useState<OrderExecution[]>([]);
    const [btcHistory, setBtcHistory] = useState<MarketData[]>([]);
    const [ethHistory, setEthHistory] = useState<MarketData[]>([]);

    useEffect(() => {
        Api.getMarketData().then(setMarketData);
        Api.getTradeDecisions().then(setDecisions);
        Api.getOrderExecutions().then(setOrders);
        Api.getPriceHistory('BTC/USD').then(setBtcHistory);
        Api.getPriceHistory('ETH/USD').then(setEthHistory);
    }, []);

    const filledOrders = orders;
    // const filledOrders = orders.filter((o) => o.status === 'FILLED');

    const renderContent = () => {
        if (activeTab === 'overview') {
            return (
                <>
                    <section className="bb-section">
                        <h2 className="bb-section-title">System Overview</h2>
                        <div className="bb-grid-cards">
                            <article className="bb-card">
                                <h3 className="bb-card-title">Market ticks (snapshot)</h3>
                                <p className="bb-card-kpi">{marketData.length}</p>
                                <p className="bb-card-note">From Market Data Service</p>
                            </article>
                            <article className="bb-card">
                                <h3 className="bb-card-title">Decisions total</h3>
                                <p className="bb-card-kpi">{decisions.length}</p>
                                <p className="bb-card-note">Produced strategies & rules</p>
                            </article>
                            <article className="bb-card">
                                <h3 className="bb-card-title">Filled orders</h3>
                                <p className="bb-card-kpi">{filledOrders.length}</p>
                                <p className="bb-card-note">Successfully executed on exchange</p>
                            </article>
                        </div>
                    </section>

                    <section className="bb-section">
                        <h2 className="bb-section-title">Recent Decisions</h2>
                        <DecisionTable data={decisions.slice(0, 5)}/>
                    </section>
                </>
            );
        }

        if (activeTab === 'market') {
            return (
                <>
                    <section className="bb-section">
                        <h2 className="bb-section-title">Price Action</h2>
                        <div className="bb-chart-grid">
                            <PriceChart data={btcHistory} symbol="BTC/EUR"/>
                            <PriceChart data={ethHistory} symbol="ETH/EUR"/>
                        </div>
                    </section>
                    <MarketTable data={marketData}/>
                </>
            );
        }

        if (activeTab === 'decisions') {
            return <DecisionTable data={decisions}/>;
        }

        return <OrdersTable data={orders}/>;
    };

    return (
        <PageLayout>
            <NavBar activeTab={activeTab} onTabChange={setActiveTab}/>
            {renderContent()}
        </PageLayout>
    );
};

export default App;
