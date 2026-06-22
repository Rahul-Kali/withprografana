from flask import Flask, jsonify

app = Flask(__name__)


@app.post("/analytics")
def analytics():
    print("Analytics logged")
    return jsonify({"status": "Analytics logged"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5003)