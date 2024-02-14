# http_request_tool.py

import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
import uuid

# API 엔드포인트 설정
JOIN_URL = "http://host.docker.internal:8081/api/auth/join"
LOGIN_URL = "http://host.docker.internal:8081/api/auth/login"
ORDERS_URL = "http://host.docker.internal:8084/api/orders"
PAY_URL = "http://host.docker.internal:8084/api/orders/pay/"

# 요청 타임아웃 설정 (초 단위)
REQUEST_TIMEOUT = 15

# 고유한 사용자 정보 생성 함수
def generate_user_info():
    unique_id = uuid.uuid4()
    return {
        "email": f"user_{unique_id}@example.com",
        "password": "123123",
        "name": f"user_{unique_id}"
    }

# 회원가입 함수
def register_user(user_info):
    try:
        response = requests.post(JOIN_URL, json=user_info, timeout=REQUEST_TIMEOUT)
        return response.status_code in [200, 201]
    except requests.exceptions.Timeout:
        print(f"Registration timeout for {user_info['email']}")
        return False

# 로그인 함수
def login(user_info):
    try:
        response = requests.post(LOGIN_URL, json=user_info, timeout=REQUEST_TIMEOUT)
        if response.status_code == 200:
            # 'data' 객체 내부의 'token' 키에 접근하여 토큰 추출
            return response.json()['data']['token']
    except requests.exceptions.Timeout:
        print(f"Login timeout for {user_info['email']}")
    except KeyError:
        print("KeyError: 'token' not found in response")
    return None

# 결제 화면 진입 함수
def enter_payment_screen(token):
    headers = {"Authorization": f"Bearer {token}"}
    data = [{"itemId": 12, "count": 1}]
    try:
        response = requests.post(ORDERS_URL, headers=headers, json=data, timeout=REQUEST_TIMEOUT)
        if response.status_code == 200:
            return response.json().get('orderId')
    except requests.exceptions.Timeout:
        print("Entering payment screen timeout")
    return None

# 결제 시도 함수
def attempt_payment(token, orderId):
    headers = {"Authorization": f"Bearer {token}"}
    try:
        response = requests.post(f"{PAY_URL}{orderId}", headers=headers, timeout=REQUEST_TIMEOUT)
        return response.status_code == 200
    except requests.exceptions.Timeout:
        print(f"Payment attempt timeout for order {orderId}")
        return False

# 메인 함수
def main():
    users_count = 10000
    with ThreadPoolExecutor(max_workers=50) as executor:
        user_infos = [generate_user_info() for _ in range(users_count)]
        register_futures = [executor.submit(register_user, user_info) for user_info in user_infos]
        as_completed(register_futures)

        login_futures = {executor.submit(login, user_info): user_info for user_info in user_infos}
        tokens = [future.result() for future in as_completed(login_futures) if future.result()]

        order_futures = [executor.submit(enter_payment_screen, token) for token in tokens]
        orderIds = [future.result() for future in as_completed(order_futures) if future.result()]

        pay_futures = [executor.submit(attempt_payment, tokens[i], orderId) for i, orderId in enumerate(orderIds[:8000])]
        as_completed(pay_futures)
        print(f"Attempted payments for the first 8000 orders.")

if __name__ == "__main__":
    main()