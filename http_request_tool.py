# http_request_tool.py

import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import uuid

# API 엔드포인트 설정
LOGIN_URL = "http://host.docker.internal:8081/api/auth/login"
ORDERS_URL = "http://host.docker.internal:8084/api/orders"
PAY_URL = "http://host.docker.internal:8084/api/orders/pay/"

# 요청 타임아웃 설정 (초 단위)
REQUEST_TIMEOUT = 15

# 로그인 함수
def login():
    user_info = {
        "email": "aaa@naver.com",
        "password": "123123"
    }
    try:
        response = requests.post(LOGIN_URL, json=user_info)
        if response.status_code == 200:
            # 'data' 객체 내부의 'token' 키에 접근하여 토큰 추출
            return response.json()['data']['token']
    except requests.exceptions.Timeout:
        print("Login timeout for aaa@naver.com")
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
    token = login()
    if token:
        with ThreadPoolExecutor(max_workers=50) as executor:
            # 결제 화면 진입 및 주문 생성
            order_futures = [executor.submit(enter_payment_screen, token) for _ in range(10000)]
            orderIds = [future.result() for future in as_completed(order_futures) if future.result()]

            # 결제 시도 (처음 8000개의 orderId 사용)
            pay_futures = [executor.submit(attempt_payment, token, orderId) for orderId in orderIds[:8000]]
            as_completed(pay_futures)
            print(f"Attempted payments for the first 8000 orders.")
    else:
        print("Failed to authenticate user.")

if __name__ == "__main__":
    main()

