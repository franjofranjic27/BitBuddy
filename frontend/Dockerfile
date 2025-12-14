FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve


# Startup-Script hinzufügen
COPY --chmod=755 <<'EOF' /app/entrypoint.sh
#!/bin/sh
# Generiere env-config.js mit tatsächlichen Umgebungsvariablen
cat > /app/dist/env-config.js <<EOL
window.ENV = {
  MARKET_DATA_API_URL: "/api/market-data",
  TRADE_DECISIONS_API_URL: "/api/order-decision",
  ORDER_EXECUTIONS_API_URL: "/api/order-execution"
};
EOL

exec serve -s dist -l 3000
EOF

COPY --from=builder /app/dist ./dist
EXPOSE 3000
ENTRYPOINT ["/app/entrypoint.sh"]