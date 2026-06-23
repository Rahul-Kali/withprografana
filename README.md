# Microservices Training Workspace

This workspace contains 7 small applications for DevOps training:

- `user-service` - Spring Boot service on port 8081
- `product-service` - Spring Boot service on port 8082
- `order-service` - Spring Boot service on port 8083
- `payment-service` - Flask service on port 5001
- `notification-service` - Flask service on port 5002
- `analytics-service` - Flask service on port 5003
- `order-frontend` - React app that calls the order service

## Quick Start

### Spring Boot services

Run each service from its own folder:

```powershell
mvn spring-boot:run
```

### Python services

Install dependencies and start each app:

```powershell
pip install -r requirements.txt
python app.py
```

### React app

```powershell
npm install
npm run dev
```

### Full stack with monitoring

Start all services plus Prometheus, Alertmanager, and Grafana:

```powershell
docker compose up -d --build
```

Monitoring URLs:

- Prometheus: http://localhost:9090
- Alertmanager: http://localhost:9093
- Grafana: http://localhost:3001

For Kubernetes, the monitoring stack is deployed into the `monitoring` namespace:

```powershell
kubectl apply -f k8s/monitoring.yaml
```

The Jenkins CI/CD pipeline also deploys this stack to Minikube and prints the monitoring namespace resources plus the Grafana Minikube URL during verification.

Grafana login:

- Username: `admin`
- Password: `admin`

Prometheus scrapes:

- Spring Boot services from `/actuator/prometheus`
- Flask services from `/metrics`

Useful custom metrics:

- `orders_total` - order requests by `result`
- `orders_duration_seconds` - end-to-end order latency
- `downstream_service_calls_total` - order-service calls to user, product, payment, notification, and analytics services by `source` and `result`
- `payment_requests_total`, `notification_requests_total`, `analytics_requests_total` - Flask service traffic by endpoint and status
- `payment_request_duration_seconds`, `notification_request_duration_seconds`, `analytics_request_duration_seconds` - Flask service latency

Place a few orders from http://localhost:3000, then open the Grafana dashboard named `Microservices Order Metrics`.

Email alerts are configured in `monitoring/alertmanager/alertmanager.yml`. Replace these placeholder values before using alerts:

- `smtp_from`
- `smtp_auth_username`
- `smtp_auth_password`
- `email_configs.to`

## Notes

The order service is configured for service-to-service calls using hostnames like `user-service` and also includes localhost fallbacks for simple local training runs.
