from flask import Flask, Response, jsonify
from prometheus_client import CONTENT_TYPE_LATEST, Counter, generate_latest

app = Flask(__name__)
ANALYTICS_REQUESTS = Counter("analytics_requests_total", "Total analytics requests")


@app.post("/analytics")
def analytics():
    ANALYTICS_REQUESTS.inc()
    print("Analytics logged")
    return jsonify({"status": "Analytics logged"})


@app.get("/metrics")
def metrics():
    return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5003)
