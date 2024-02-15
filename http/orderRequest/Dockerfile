# Dockerfile

FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# CMD [ "python", "./http_request_tool.py" ]

# 회원가입이 이미 돼있는경우
CMD ["python", "http_request_tool.py", "--skip_registration"]
