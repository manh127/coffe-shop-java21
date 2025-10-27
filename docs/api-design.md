# API Design Decisions

## REST API Design

### URL Structure

We follow RESTful conventions:

```
/api/v1/resources
/api/v1/resources/{id}
/api/v1/resources/{id}/action
```

**Examples:**
- `POST /api/v1/products` - Create product
- `GET /api/v1/products/{id}` - Get product
- `PATCH /api/v1/products/{id}/restock` - Restock action
- `POST /api/v1/orders/{id}/pay` - Pay action

### Versioning

We use **URI versioning** (`/api/v1/`) because:
- ✅ Simple and explicit
- ✅ Easy to test with curl/Postman
- ✅ Clear documentation

**Alternatives considered:**
- ❌ Header versioning: Less discoverable
- ❌ Query parameter: Inconsistent with REST

### HTTP Methods

| Method | Purpose | Idempotent | Safe |
|--------|---------|------------|------|
| GET | Read resource | ✅ | ✅ |
| POST | Create resource | ❌ | ❌ |
| PUT | Replace resource | ✅ | ❌ |
| PATCH | Update resource | ❌ | ❌ |
| DELETE | Delete resource | ✅ | ❌ |

### Status Codes

We use standard HTTP status codes:

| Code | Meaning | Usage |
|------|---------|-------|
| 200 OK | Success | GET, PUT, PATCH |
| 201 Created | Created | POST |
| 204 No Content | Success, no body | DELETE |
| 400 Bad Request | Validation error | Invalid input |
| 401 Unauthorized | Not authenticated | Missing/invalid JWT |
| 403 Forbidden | Not authorized | Insufficient permissions |
| 404 Not Found | Resource not found | Invalid ID |
| 409 Conflict | Business rule violation | Duplicate SKU |
| 500 Internal Server Error | Server error | Unexpected error |

## Error Response Format (RFC 7807)

We follow **RFC 7807 Problem Details** for consistent error responses:

```json
{
  "type": "https://api.coffee-shop.com/problems/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/products",
  "timestamp": "2024-01-15T10:30:00Z",
  "additionalProperties": {
    "name": "Product name is required",
    "price": "Price must be positive"
  }
}
```

**Benefits:**
- ✅ Standard format
- ✅ Machine-readable
- ✅ Human-readable
- ✅ Includes context

## Pagination

We use **offset-based pagination** with Spring Data:

**Request:**
```
GET /api/v1/products?page=0&size=20&sort=name,asc
```

**Response:**
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalPages": 5,
  "totalElements": 100,
  "last": false,
  "first": true
}
```

**Alternative considered:**
- Cursor-based pagination: Better for real-time feeds but more complex

## Filtering & Sorting

**Filtering:**
```
GET /api/v1/orders?status=PAID&dateFrom=2024-01-01T00:00:00Z
```

**Sorting:**
```
GET /api/v1/products?sort=price,asc&sort=name,desc
```

## Authentication & Authorization

### JWT Token Format

```json
{
  "sub": "user@example.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iat": 1640000000,
  "exp": 1640003600
}
```

**Token Lifetime:** 1 hour (configurable)

### Authorization Header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Role-Based Access Control

| Endpoint | Admin | User | Public |
|----------|-------|------|--------|
| POST /products | ✅ | ❌ | ❌ |
| GET /products | ✅ | ✅ | ✅ |
| POST /orders | ✅ | ✅ | ❌ |
| GET /orders (all) | ✅ | ❌ | ❌ |

## Idempotency

For **idempotent operations**, clients can include:

```
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
```

**Benefits:**
- ✅ Safe retries
- ✅ Prevent duplicate orders
- ✅ Network fault tolerance

**Implementation (future):**
1. Store key in database with request hash
2. Return cached response if key exists
3. Expire keys after 24 hours

## Content Negotiation

We support **JSON only** (for simplicity):

```
Content-Type: application/json
Accept: application/json
```

## Rate Limiting (Future)

**Planned implementation:**
- 100 requests per minute per user
- 1000 requests per minute per IP

**Headers:**
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640003600
```

## API Documentation

We use **OpenAPI 3.0** (Swagger):
- **Interactive docs:** `/swagger-ui.html`
- **OpenAPI spec:** `/api-docs`

## Best Practices Applied

1. ✅ **Resource-oriented URLs:** `/products`, `/orders`
2. ✅ **Standard HTTP methods:** GET, POST, PATCH, DELETE
3. ✅ **Proper status codes:** 200, 201, 400, 404, etc.
4. ✅ **Pagination:** Offset-based with Spring Data
5. ✅ **Filtering:** Query parameters
6. ✅ **Sorting:** Multiple sort fields
7. ✅ **Error handling:** RFC 7807
8. ✅ **Versioning:** URI-based
9. ✅ **Security:** JWT with Bearer token
10. ✅ **Documentation:** OpenAPI 3.0

## Future Improvements

- [ ] GraphQL endpoint for flexible queries
- [ ] WebSocket support for real-time updates
- [ ] HATEOAS links in responses
- [ ] API rate limiting
- [ ] Request/response compression
- [ ] ETags for caching
- [ ] CORS configuration for web clients

