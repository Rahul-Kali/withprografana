from time import perf_counter

from flask import Flask, Response, jsonify, request
from prometheus_client import CONTENT_TYPE_LATEST, Counter, Histogram, generate_latest

app = Flask(__name__)
PAYMENT_REQUESTS = Counter("payment_requests_total", "Total payment requests", ["endpoint", "status"])
PAYMENT_LATENCY = Histogram("payment_request_duration_seconds", "Payment request latency", ["endpoint"])


@app.post("/pay")
def pay():
    start = perf_counter()
    status = "success"
    try:
        return jsonify({"status": "Payment Successful", "amount": 1499, "currency": "INR"})
    except Exception:
        status = "failure"
        raise
    finally:
        endpoint = request.path
        PAYMENT_REQUESTS.labels(endpoint=endpoint, status=status).inc()
        PAYMENT_LATENCY.labels(endpoint=endpoint).observe(perf_counter() - start)


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
