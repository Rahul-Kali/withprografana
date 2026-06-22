from flask import Flask, Response, jsonify
from prometheus_client import CONTENT_TYPE_LATEST, Counter, generate_latest

app = Flask(__name__)
PAYMENT_REQUESTS = Counter("payment_requests_total", "Total payment requests")


@app.post("/pay")
def pay():
    PAYMENT_REQUESTS.inc()
    return jsonify({"status": "Payment Successful"})


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
