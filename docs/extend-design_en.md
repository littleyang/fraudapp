[Back to README](../README.md)

# Extension Ideas

## 8.2 Performance Optimizations

### Handling Large Keys in Blacklist Cache

- A single key could grow very large. Distribute data across multiple keys instead.
- Use consistent hashing based on the blacklist account to spread keys in the cache and reduce big-key risk.

### Database Tuning

- Optimize SQL and create indexes on key fields (transaction ID, account)
- Use read/write separation for better performance
- Batch writes to improve record insertion efficiency

### Data Encryption

- Encrypt sensitive fields such as user info and transaction accounts using AES symmetric encryption
- Support transparent encryption at the database layer

### Operation Auditing

- Log all API calls related to transaction processing
- Include request parameters, user, call time and results

### Anti-Attack Design

- Prevent replay attacks at the API layer (using nonce, timestamp, signatures)
- Apply rate limiting strategies (e.g. IP-based QPS limits)

## 8.4 Security Testing and Validation

- Perform SQL injection and XSS penetration tests
