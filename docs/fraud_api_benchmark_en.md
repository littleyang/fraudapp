[Back to README](../README.md)

# 📊 API Performance Benchmark

## API Details

| Item             | Content                                                     |
|------------------|-------------------------------------------------------------|
| Endpoint         | `http://localhost:8080/fraud/tx/evaluate/check`             |
| Method           | `POST`                                                      |
| Sample Request   | `{ "transactionId": "228u48339stdds", "account": "ew185r4", "amount": "45659666", "transactionTime": 1750477432000, "description": "transaction info" }` |
| Headers          | `Content-Type: application/json`                            |

## Test Environment

| Item        | Configuration                     |
|-------------|----------------------------------|
| Tool        | Apache JMeter v5.6               |
| Threads     | 100                              |
| Loops       | 1000                             |
| Total Req   | 100,000                          |
| Server      | 8C16G, Java 21, Spring Boot 3.2  |

## Key Metrics

| Metric            | Value        | Notes                                |
|-------------------|-------------|--------------------------------------|
| Avg Response Time | 89 ms       | Overall stable                       |
| Max Response Time | 201 ms      | Fluctuates under high concurrency    |
| Min Response Time | 65 ms       | Best case when resources are ample   |
| Error Rate        | 0.03%       | Few failures (lock conflicts, etc.)  |
| Throughput (TPS)  | 1050 req/s  | Single machine concurrency capability |
| CPU Usage         | 73%         | Average during the test              |
| Memory Usage      | 68%         | GC stable, no obvious Full GC        |

## Error Analysis

- **Failed Request Example:**
  ```json
  {
    "code": 2003,
    "message": "Duplicate request, processing in progress. Please retry later"
  }
  ```
