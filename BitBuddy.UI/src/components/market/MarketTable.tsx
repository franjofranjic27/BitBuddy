import React from 'react';
import type { MarketTick } from '../../types/domain';

interface MarketTableProps {
    data: MarketTick[];
}

export const MarketTable: React.FC<MarketTableProps> = ({ data }) => {
    return (
        <section className="bb-section">
            <div className="bb-table-wrapper">
                <div className="bb-table-title-row">
                    <h3 className="bb-table-title">Market Data Stream</h3>
                    <p className="bb-table-subtitle">
                        Latest normalized prices as seen by <strong>Market Data Service</strong>
                    </p>
                </div>
                <div className="bb-table-scroll">
                    <table className="bb-table">
                        <thead>
                        <tr>
                            <th>Exchange</th>
                            <th>Symbol</th>
                            <th>Price</th>
                            <th>Volume</th>
                            <th>Timestamp</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.map((tick) => (
                            <tr key={tick.id}>
                                <td>{tick.exchange}</td>
                                <td>{tick.symbol}</td>
                                <td>{tick.price.toFixed(2)}</td>
                                <td>{tick.volume}</td>
                                <td>{new Date(tick.timestamp).toLocaleString('de-CH')}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    );
};
