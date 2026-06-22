from time import perf_counter

from flask import Flask, Response, jsonify, request
from prometheus_client import CONTENT_TYPE_LATEST, Counter, Histogram, generate_latest

app = Flask(__name__)
NOTIFICATION_REQUESTS = Counter("notification_requests_total", "Total notification requests", ["endpoint", "status"])
NOTIFICATION_LATENCY = Histogram("notification_request_duration_seconds", "Notification request latency", ["endpoint"])


@app.post("/notify")
def notify():
    start = perf_counter()
    status = "success"
    try:
        print("Notification sent")
        return jsonify({"status": "Notification sent", "channel": "email"})
    except Exception:
        status = "failure"
        raise
    finally:
        endpoint = request.path
        NOTIFICATION_REQUESTS.labels(endpoint=endpoint, status=status).inc()
        NOTIFICATION_LATENCY.labels(endpoint=endpoint).observe(perf_counter() - start)


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002)
