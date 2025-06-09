from flask import Flask, request, jsonify
import openai
import faiss
import numpy as np

app = Flask(__name__)
openai.api_key = "github 보안 문제로 인한 삭제"

dimension = 1536
index = faiss.IndexFlatL2(dimension)
id_to_text = {}

# 1. 임베딩 생성
def get_embedding(text):
    response = openai.embeddings.create(
        input=text,
        model="text-embedding-3-small"
    )
    return np.array(response.data[0].embedding, dtype=np.float32)

# 2. 문장 저장 API
@app.route("/add", methods=["POST"])
def add_text():
    data = request.get_json()
    text = data.get("text")
    idx = int(data.get("id"))

    vector = get_embedding(text)
    index.add(np.array([vector]))
    id_to_text[idx] = text
    return jsonify({"status": "ok", "added": text})

# 유사 문장 검색 API
@app.route("/search", methods=["GET"])
def search():
    query = request.args.get("q")
    if not query:
        return jsonify({"error": "쿼리(q) 파라미터가 없습니다"}), 400

    try:
        top_k = int(request.args.get("top_k", 3))
    except ValueError:
        return jsonify({"error": "top_k는 숫자여야 합니다"}), 400

    vector = get_embedding(query)
    if vector is None:
        return jsonify({"error": "임베딩 실패"}), 500

    try:
        D, I = index.search(vector.reshape(1, -1), top_k)
    except Exception as e:
        return jsonify({"error": f"벡터 검색 실패: {str(e)}"}), 500

    results = []
    for j, i in enumerate(I[0]):
        if i in id_to_text:
            results.append({
                "text": id_to_text[i],
                "distance": float(D[0][j])
            })

    return jsonify(results)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5005)
