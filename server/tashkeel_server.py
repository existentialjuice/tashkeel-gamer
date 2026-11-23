from flask import Flask, request, jsonify
import subprocess

app = Flask(__name__)

@app.route("/", methods=["GET"])
def home():
    return "Text2Tashkeel service is running!"

@app.route("/tashkeel", methods=["POST"])
def tashkeel():
    data = request.get_json(silent=True) or {}
    text = data.get("text", "")

    if not text:
        return jsonify({"error": "No text supplied"}), 400

    result = subprocess.run(
        ["text2tashkeel", "-m", "rawi-v2-int8", text],
        capture_output=True,
        text=True
    )

    if result.returncode != 0:
        return jsonify({"error": result.stderr}), 500

    return jsonify({"text": result.stdout.strip()})

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=8765)
