from flask import Flask, Response, jsonify
from prometheus_client import CONTENT_TYPE_LATEST, Counter, generate_latest

app = Flask(__name__)
NOTIFICATION_REQUESTS = Counter("notification_requests_total", "Total notification requests")


@app.post("/notify")
def notify():
    NOTIFICATION_REQUESTS.inc()
    print("Notification sent")
    return jsonify({"status": "Notification sent"})


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002)
