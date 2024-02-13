# http_request_tool.py

import requests
import threading
from concurrent.futures import ThreadPoolExecutor

def send_http_request(url):
    try:
        response = requests.get(url)
        print(f"Request to {url} completed with status code {response.status_code}")
    except Exception as e:
        print(f"Error sending request to {url}: {e}")

def main():
    # Set the number of concurrent requests (N)
    num_requests = 10

    # Set the target URL
    target_url = "https://example.com"

    # Create a ThreadPoolExecutor to send concurrent requests
    with ThreadPoolExecutor(max_workers=num_requests) as executor:
        # Use a list comprehension to create a list of tasks
        tasks = [executor.submit(send_http_request, target_url) for _ in range(num_requests)]

        # Wait for all tasks to complete
        for future in tasks:
            future.result()

if __name__ == "__main__":
    main()