from datetime import datetime, timedelta
import pytz
import redis
from apscheduler.schedulers.background import BackgroundScheduler
import time
import logging

logging.basicConfig()
logging.getLogger('apscheduler').setLevel(logging.DEBUG)

# Redis 연결 설정
redis_client = redis.Redis(host='host.docker.internal', port=6379, db=0)

# Asia/Seoul 시간대 설정
seoul_timezone = pytz.timezone('Asia/Seoul')

# 전역 변수로 scheduler 선언
scheduler = BackgroundScheduler()

def notify_stock(event_type, item_id):
    # Redis에서 아이템의 재고 수량 조회
    current_time = datetime.now(seoul_timezone).strftime('%Y-%m-%d %H:%M:%S')
    stock_quantity = redis_client.hget(f"item:{item_id}", "stockQuantity").decode('utf-8')
    print(f"[{current_time}] {event_type} - Item ID: {item_id}, Stock Quantity: {stock_quantity}")

def list_initial_stock():
    print("Listing all items and their initial stock quantities:")
    for key in redis_client.scan_iter("item:*"):
        item_id = key.decode().split(":")[1]
        stock_quantity = redis_client.hget(key, "stockQuantity").decode('utf-8')
        print(f"Item ID: {item_id}, Initial Stock Quantity: {stock_quantity}")

def schedule_stock_checks():
    # scheduler를 전역 변수로 사용
    global scheduler
    scheduler.start()

    for key in redis_client.scan_iter("item:*"):
        item_id = key.decode().split(":")[1]
        item_info = redis_client.hgetall(key)
        
        reservation_start_str = item_info[b'reservationStart'].decode()
        reservation_end_str = item_info[b'reservationEnd'].decode()
        
        reservation_start = datetime.fromisoformat(reservation_start_str).astimezone(seoul_timezone)
        reservation_end = datetime.fromisoformat(reservation_end_str).astimezone(seoul_timezone)

        # 예약 시작 10분 전에 재고 확인 스케줄링
        start_check_time = reservation_start - timedelta(minutes=10)
        scheduler.add_job(notify_stock, 'date', run_date=start_check_time, args=["Reservation Start", item_id])

        # 예약 종료 시간에 재고 확인 스케줄링
        scheduler.add_job(notify_stock, 'date', run_date=reservation_end, args=["Reservation End", item_id])

if __name__ == "__main__":
    list_initial_stock()  # 초기 재고 나열
    schedule_stock_checks()  # 예약 시작 10분 전과 종료 후에 재고 확인 스케줄링

    try:
        # 무한 루프를 사용하여 메인 스레드가 종료되지 않도록 유지
        while True:
            time.sleep(1)
    except (KeyboardInterrupt, SystemExit):
        scheduler.shutdown()  # 스케줄러 종료
