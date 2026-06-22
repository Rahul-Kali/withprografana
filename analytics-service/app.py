from time import perf_counter

from flask import Flask, Response, jsonify, request
from prometheus_client import CONTENT_TYPE_LATEST, Counter, Histogram, generate_latest

app = Flask(__name__)
ANALYTICS_REQUESTS = Counter("analytics_requests_total", "Total analytics requests", ["endpoint", "status"])
ANALYTICS_LATENCY = Histogram("analytics_request_duration_seconds", "Analytics request latency", ["endpoint"])


@app.post("/analytics")
def analytics():
    start = perf_counter()
    status = "success"
    try:
        print("Analytics logged")
        return jsonify({"status": "Analytics logged", "event": "order_placed"})
    except Exception:
        status = "failure"
        raise
    finally:
        endpoint = request.path
        ANALYTICS_REQUESTS.labels(endpoint=endpoint, status=status).inc()
        ANALYTICS_LATENCY.labels(endpoint=endpoint).observe(perf_counter() - start)


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5003)
