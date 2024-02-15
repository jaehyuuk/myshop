import requests
import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import logging
import time
import sys

# 로깅 설정
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# API 엔드포인트 설정
JOIN_URL = "http://host.docker.internal:8081/api/auth/join"
LOGIN_URL = "http://host.docker.internal:8081/api/auth/login"
ORDERS_URL = "http://host.docker.internal:8084/api/orders"
PAY_URL = "http://host.docker.internal:8084/api/orders/pay/"

# 요청 타임아웃 설정 (초 단위)
REQUEST_TIMEOUT = 15

# 배치 처리 설정
BATCH_SIZE = 100
BATCH_DELAY = 5  # 배치 사이의 지연 시간 (초)

# 고유한 사용자 정보 생성 함수
def generate_user_info(user_id):
    return {
        "email": f"user_{user_id}@example.com",
        "password": "123123",
        "name": f"user_{user_id}"
    }

# 회원가입 함수
def register_user(user_info):
    try:
        response = requests.post(JOIN_URL, json=user_info, timeout=REQUEST_TIMEOUT)
        if response.status_code in [200, 201]:
            logging.info(f"Registration successful for {user_info['email']}")
            return True
        else:
            logging.warning(f"Registration failed for {user_info['email']}: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        logging.error(f"Registration error for {user_info['email']}: {e}")
        return False

# 로그인 함수
def login(user_info):
    try:
        response = requests.post(LOGIN_URL, json=user_info, timeout=REQUEST_TIMEOUT)
        if response.status_code == 200:
            token = response.json()['data']['token']
            logging.info(f"Login successful for {user_info['email']}")
            return token
        else:
            logging.warning(f"Login failed for {user_info['email']}: {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        logging.error(f"Login error for {user_info['email']}: {e}")
        return None

# 결제 화면 진입 함수
def enter_payment_screen(token):
    headers = {"Authorization": f"Bearer {token}"}
    data = [{"itemId": 15, "count": 1}]
    try:
        response = requests.post(ORDERS_URL, headers=headers, json=data, timeout=REQUEST_TIMEOUT)
        if response.status_code == 200:
            response_data = response.json()
            orderId = response_data.get('data')  # 'data' 필드에서 orderId 추출
            logging.info(f"Entering payment screen successful, orderId: {orderId}")
            return orderId
        else:
            logging.warning(f"Entering payment screen failed: {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        logging.error(f"Entering payment screen error: {e}")
        return None
        
# 결제 시도 함수
def attempt_payment(token, orderId):
    headers = {"Authorization": f"Bearer {token}"}
    try:
        response = requests.post(f"{PAY_URL}{orderId}", headers=headers, timeout=REQUEST_TIMEOUT)
        if response.status_code == 200:
            logging.info(f"Payment successful for order {orderId}")
            return True
        else:
            logging.warning(f"Payment failed for order {orderId}: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        logging.error(f"Payment error for order {orderId}: {e}")
        return False

# 메인 함수
def main(skip_registration):
    users_count = 10000  # 사용자 수를 10,000으로 설정
    batches = users_count // BATCH_SIZE

    with ThreadPoolExecutor(max_workers=50) as executor:
        for batch in range(batches):
            start_index = batch * BATCH_SIZE
            end_index = start_index + BATCH_SIZE
            user_infos = [generate_user_info(user_id) for user_id in range(start_index + 1, end_index + 1)]

            if not skip_registration:
                # 회원가입 배치 처리
                register_futures = [executor.submit(register_user, user_info) for user_info in user_infos]
                logging.info(f"Batch {batch+1}/{batches}: Registration started.")
                for future in as_completed(register_futures):
                    pass  # 회원가입 결과 처리

            # 로그인 배치 처리
            logging.info(f"Batch {batch+1}/{batches}: Login started.")
            login_futures = {executor.submit(login, user_info): user_info for user_info in user_infos}
            tokens = [future.result() for future in as_completed(login_futures) if future.result()]

            # 주문 생성 및 결제 시도 배치 처리
            logging.info(f"Batch {batch+1}/{batches}: Order and payment started.")
            order_futures = [executor.submit(enter_payment_screen, token) for token in tokens]
            orderIds = [future.result() for future in as_completed(order_futures) if future.result()]

            pay_futures = [executor.submit(attempt_payment, tokens[i], orderId) for i, orderId in enumerate(orderIds)]
            for future in as_completed(pay_futures):
                pass  # 결제 시도 결과 처리

            logging.info(f"Batch {batch+1}/{batches} completed.")
            if batch < batches - 1:
                logging.info(f"Waiting for {BATCH_DELAY} seconds before next batch...")
                time.sleep(BATCH_DELAY)  # 다음 배치까지 지연

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="HTTP Request Automation Tool")
    parser.add_argument("--skip_registration", action="store_true", help="Skip the registration process")
    args = parser.parse_args()

    main(args.skip_registration)