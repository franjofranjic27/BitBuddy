import React from 'react';
import type { OrderExecution } from '../../types/domain';

interface OrdersTableProps {
    data: OrderExecution[];
}

const statusClass = (status: OrderExecution['status']): string => {
    switch (status) {
        case 'FILLED':
            return 'bb-badge bb-badge--status bb-badge--status-accepted';
        case 'NEW':
        case 'PARTIALLY_FILLED':
            return 'bb-badge bb-badge--status bb-badge--status-pending';
        case 'REJECTED':
        default:
            return 'bb-badge bb-badge--status bb-badge--status-rejected';
    }
};

export const OrdersTable: React.FC<OrdersTableProps> = ({ data }) => {
    return (
        <section className="bb-section">
            <div className="bb-table-wrapper">
                <div className="bb-table-title-row">
                    <h3 className="bb-table-title">Order Executions</h3>
                    <p className="bb-table-subtitle">
                        Orders sent to the exchange via <strong>Order Execution Service</strong>
                    </p>
                </div>
                <div className="bb-table-scroll">
                    <table className="bb-table">
                        <thead>
                        <tr>
                            <th>Symbol</th>
                            <th>Side</th>
                            <th>Size</th>
                            <th>Price</th>
                            <th>Exchange</th>
                            <th>Status</th>
                            <th>Created</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.map((o) => (
                            <tr key={o.id}>
                                <td>{o.symbol}</td>
                                <td>
                    <span
                        className={
                            'bb-badge ' +
                            (o.side === 'BUY'
                                ? 'bb-badge--buy'
                                : 'bb-badge--sell')
                        }
                    >
                      {o.side}
                    </span>
                                </td>
                                <td>{o.size}</td>
                                <td>{o.price.toFixed(2)}</td>
                                <td>{o.exchange}</td>
                                <td>
                                    <span className={statusClass(o.status)}>{o.status}</span>
                                </td>
                                <td>{new Date(o.createdAt).toLocaleString('de-CH')}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    );
};
