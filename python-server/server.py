from flask import Flask, request, jsonify, send_from_directory
import os
from gtts import gTTS
import uuid
from openai import OpenAI
import logging
import subprocess

# Secret Manager에서 API 키 가져오는 함수
def get_secret(secret_name):
    try:
        result = subprocess.run(
            ["gcloud", "secrets", "versions", "access", "latest", f"--secret={secret_name}"],
            stdout=subprocess.PIPE,
            text=True
        )
        return result.stdout.strip()
    except Exception as e:
        logging.error(f"Secret Manager에서 API 키를 가져오는 데 실패했습니다: {e}")
        return None

# OpenAI API 키 가져오기
openai_api_key = get_secret("OPENAI_API_KEY")
if not openai_api_key:
    raise ValueError("OPENAI_API_KEY를 Secret Manager에서 가져오지 못했습니다.")

# Flask 앱 초기화
app = Flask(__name__)

# OpenAI 클라이언트 초기화
client = OpenAI(api_key=openai_api_key)

# 오디오 파일 저장 디렉토리 설정
AUDIO_SAVE_DIR = os.path.abspath("./audio_files")
if not os.path.exists(AUDIO_SAVE_DIR):
    os.makedirs(AUDIO_SAVE_DIR)

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# Chat Completion 생성 함수
def create_chat_completion(system_input, user_input, model="gpt-4o", temperature=1.0, max_tokens=150):
    try:
        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": system_input},
                {"role": "user", "content": user_input}
            ],
            temperature=temperature,
            max_tokens=max_tokens
        )
        return response.choices[0].message.content
    except Exception as e:
        logging.error(f"OpenAI API 호출 중 오류 발생: {str(e)}")
        return f"OpenAI API 호출 중 오류 발생: {str(e)}"


@app.route('/', methods=['GET'])
def home():
    return "Flask 서버가 실행 중입니다! /chat 엔드포인트를 사용하세요."

@app.route('/chat', methods=['POST'])
def chat():
    try:
        data = request.json
        if not data or 'system_input' not in data or 'user_input' not in data:
            logging.error("요청 JSON이 올바르지 않습니다.")
            return jsonify({"error": "요청 JSON이 올바르지 않습니다."}), 400

        logging.info(f"요청 JSON: {data}")

        system_input = data.get('system_input', 'You are a helpful assistant.')
        user_input = data.get('user_input', '')

        # 최신 ChatCompletion 사용
        text_response = create_chat_completion(system_input, user_input)
        logging.info(f"GPT 텍스트 응답: {text_response}")

        unique_filename = f"{uuid.uuid4()}.mp3"
        file_path = os.path.join(AUDIO_SAVE_DIR, unique_filename)

        try:
            tts = gTTS(text=text_response, lang='ko')
            tts.save(file_path)
            logging.info(f"TTS 파일 저장 완료: {file_path}")
        except Exception as e:
            logging.error(f"TTS 변환 실패: {str(e)}")
            return jsonify({"error": "TTS 변환에 실패했습니다."}), 500

        response_json = {
            "text": text_response,
            "audio_url": f"/audio/{unique_filename}"
        }
        logging.info(f"응답 JSON: {response_json}")
        return jsonify(response_json)

    except Exception as e:
        logging.error(f"오류 발생: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/audio/<filename>', methods=['GET'])
def serve_audio(filename):
    try:
        file_path = os.path.join(AUDIO_SAVE_DIR, filename)
        if not os.path.exists(file_path):
            logging.error(f"파일이 존재하지 않음: {file_path}")
            return jsonify({"error": "파일이 존재하지 않습니다."}), 404

        logging.info(f"오디오 파일 요청: {file_path}")
        return send_from_directory(AUDIO_SAVE_DIR, filename)
    except Exception as e:
        logging.error(f"파일 요청 처리 중 오류 발생: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
