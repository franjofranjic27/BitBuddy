import React from 'react';

export type TabKey = 'overview' | 'market' | 'decisions' | 'orders';

interface NavBarProps {
    activeTab: TabKey;
    onTabChange: (tab: TabKey) => void;
}

export const NavBar: React.FC<NavBarProps> = ({ activeTab, onTabChange }) => {
    const tabs: { key: TabKey; label: string }[] = [
        { key: 'overview', label: 'Overview' },
        { key: 'market', label: 'Market Data' },
        { key: 'decisions', label: 'Trade Decisions' },
        { key: 'orders', label: 'Order Executions' },
    ];

    return (
        <nav className="bb-nav">
            {tabs.map((tab) => (
                <button
                    key={tab.key}
                    type="button"
                    onClick={() => onTabChange(tab.key)}
                    className={
                        'bb-nav-btn' + (tab.key === activeTab ? ' bb-nav-btn--active' : '')
                    }
                >
                    {tab.label}
                </button>
            ))}
        </nav>
    );
};
