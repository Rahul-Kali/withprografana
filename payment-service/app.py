from flask import Flask, jsonify

app = Flask(__name__)


@app.post("/pay")
def pay():
    return jsonify({"status": "Payment Successful"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)