import React from 'react';
import type {OrderExecution} from '../../types/domain';

interface OrdersTableProps {
    data: OrderExecution[];
}

export const OrdersTable: React.FC<OrdersTableProps> = ({data}) => {
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
                            <th>Base</th>
                            <th>Counter</th>
                            <th>Type</th>
                            <th>Style</th>
                            <th>Created</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.map((o) => (
                            <tr key={o.id}>
                                <td>{o.base}</td>
                                <td>{o.counter}</td>
                                <td>
                                    <span
                                        className={
                                            'bb-badge ' +
                                            (o.orderType === 'BID'
                                                ? 'bb-badge--buy'
                                                : 'bb-badge--sell')
                                        }
                                    >
                                      {o.orderType}
                                    </span>
                                </td>
                                <td>{o.orderStyle}</td>

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
