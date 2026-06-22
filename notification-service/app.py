from flask import Flask, jsonify

app = Flask(__name__)


@app.post("/notify")
def notify():
    print("Notification sent")
    return jsonify({"status": "Notification sent"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002)