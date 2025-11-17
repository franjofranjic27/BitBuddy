import React from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer,
} from 'recharts';
import type { PricePoint } from '../../types/domain';

interface PriceChartProps {
    data: PricePoint[];
    symbol: string;
}

export const PriceChart: React.FC<PriceChartProps> = ({ data, symbol }) => {
    return (
        <div className="bb-chart-card">
            <h3 className="bb-table-title" style={{ marginBottom: 6 }}>
                {symbol} · Price (mock)
            </h3>
            <div style={{ width: '100%', height: 220 }}>
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" stroke="rgba(148,163,184,0.35)" />
                        <XAxis
                            dataKey="timestamp"
                            tickFormatter={(value) =>
                                new Date(value).toLocaleTimeString('de-CH', {
                                    hour: '2-digit',
                                    minute: '2-digit',
                                })
                            }
                            tick={{ fill: '#9ca3af', fontSize: 11 }}
                            minTickGap={20}
                        />
                        <YAxis
                            dataKey="price"
                            tickFormatter={(v) => v.toFixed(0)}
                            tick={{ fill: '#9ca3af', fontSize: 11 }}
                        />
                        <Tooltip
                            contentStyle={{
                                background: '#020617',
                                border: '1px solid rgba(148,163,184,0.6)',
                                borderRadius: 10,
                                padding: 8,
                                fontSize: 11,
                            }}
                            labelFormatter={(value) =>
                                new Date(value).toLocaleString('de-CH')
                            }
                        />
                        <Line
                            type="monotone"
                            dataKey="price"
                            stroke="#4f46e5"
                            strokeWidth={2}
                            dot={false}
                            activeDot={{ r: 4 }}
                        />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};
