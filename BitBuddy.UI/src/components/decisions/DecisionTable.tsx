import React from 'react';
import type { TradeDecision } from '../../types/domain';

interface DecisionTableProps {
    data: TradeDecision[];
}

const statusClass = (status: TradeDecision['status']): string => {
    switch (status) {
        case 'ACCEPTED':
            return 'bb-badge bb-badge--status bb-badge--status-accepted';
        case 'PENDING':
            return 'bb-badge bb-badge--status bb-badge--status-pending';
        case 'REJECTED':
        default:
            return 'bb-badge bb-badge--status bb-badge--status-rejected';
    }
};

export const DecisionTable: React.FC<DecisionTableProps> = ({ data }) => {
    return (
        <section className="bb-section">
            <div className="bb-table-wrapper">
                <div className="bb-table-title-row">
                    <h3 className="bb-table-title">Trade Decisions</h3>
                    <p className="bb-table-subtitle">
                        Signals produced by <strong>Trade Decision Service</strong>
                    </p>
                </div>
                <div className="bb-table-scroll">
                    <table className="bb-table">
                        <thead>
                        <tr>
                            <th>Symbol</th>
                            <th>Side</th>
                            <th>Size</th>
                            <th>Strategy</th>
                            <th>Reason</th>
                            <th>Status</th>
                            <th>Created</th>
                        </tr>
                        </thead>
                        <tbody>
                        {data.map((d) => (
                            <tr key={d.id}>
                                <td>{d.symbol}</td>
                                <td>
                    <span
                        className={
                            'bb-badge ' +
                            (d.side === 'BUY'
                                ? 'bb-badge--buy'
                                : 'bb-badge--sell')
                        }
                    >
                      {d.side}
                    </span>
                                </td>
                                <td>{d.size}</td>
                                <td>{d.strategy}</td>
                                <td>{d.reason}</td>
                                <td>
                                    <span className={statusClass(d.status)}>{d.status}</span>
                                </td>
                                <td>{new Date(d.createdAt).toLocaleString('de-CH')}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    );
};
