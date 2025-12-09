import React from 'react';

interface PageLayoutProps {
    children: React.ReactNode;
}

export const PageLayout: React.FC<PageLayoutProps> = ({children}) => {
    return (
        <div className="bb-root">
            <div className="bb-shell">
                <header className="bb-header">
                    <div>
                        <h1 className="bb-header-title">BitBuddy · Crypto Execution Stack</h1>
                        <p className="bb-header-subtitle">
                            Modular Trading Bot · Market Data · Orders
                        </p>
                    </div>
                    <div className="bb-header-tag">V1.0.0</div>
                </header>
                <main className="bb-main">{children}</main>
            </div>
        </div>
    );
};
