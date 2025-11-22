## Social Media Application - Complete API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints except `/auth/register` and `/auth/login` require authentication via JWT token in the Authorization header:
```
Authorization: Bearer <access_token>
```

---

## 📝 Authentication Endpoints

### 1. Register User
**POST** `/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "createdAt": "2024-01-15T10:30:00"
    }
  }
}
```

### 2. Login
**POST** `/auth/login`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:** `200 OK` (Same structure as register)

### 3. Refresh Token
**POST** `/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "user": { ... }
  }
}
```

### 4. Logout
**POST** `/auth/logout`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

### 5. Get Current User
**GET** `/auth/me`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

## 📮 Post Endpoints

### 1. Create Post
**POST** `/posts`
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "This is my first post!",
  "imageUrl": "https://example.com/image.jpg",
  "isPublic": true
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "createdAt": "2024-01-15T10:30:00"
    },
    "content": "This is my first post!",
    "imageUrl": "https://example.com/image.jpg",
    "isPublic": true,
    "likeCount": 0,
    "commentCount": 0,
    "isLikedByCurrentUser": false,
    "likedBy": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get All Posts (Feed)
**GET** `/posts?page=0&size=10`
**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Items per page

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "user": { ... },
        "content": "This is my first post!",
        "imageUrl": "https://example.com/image.jpg",
        "isPublic": true,
        "likeCount": 5,
        "commentCount": 3,
        "isLikedByCurrentUser": true,
        "likedBy": [
          { "id": 2, "firstName": "Jane", "lastName": "Smith", ... }
        ],
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 50,
    "totalPages": 5,
    "last": false
  }
}
```

### 3. Get Post by ID
**GET** `/posts/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (Single post object)

### 4. Update Post
**PUT** `/posts/{id}`
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "Updated content",
  "imageUrl": "https://example.com/new-image.jpg",
  "isPublic": false
}
```

**Response:** `200 OK`

### 5. Delete Post
**DELETE** `/posts/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Post deleted successfully",
  "data": null
}
```

### 6. Like/Unlike Post
**POST** `/posts/{id}/like`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (Returns updated post with new like count)

### 7. Get Post Likes
**GET** `/posts/{id}/likes`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane@example.com",
      "createdAt": "2024-01-14T10:30:00"
    }
  ]
}
```

### 8. Get My Posts
**GET** `/posts/my-posts?page=0&size=10`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (Paginated post list)

---

## 💬 Comment Endpoints

### 1. Create Comment
**POST** `/posts/{postId}/comments`
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "Great post!",
  "parentCommentId": null
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "Comment created successfully",
  "data": {
    "id": 1,
    "postId": 1,
    "user": {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane@example.com",
      "createdAt": "2024-01-14T10:30:00"
    },
    "parentCommentId": null,
    "content": "Great post!",
    "likeCount": 0,
    "isLikedByCurrentUser": false,
    "likedBy": [],
    "replies": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get Comments for Post
**GET** `/posts/{postId}/comments`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "postId": 1,
      "user": { ... },
      "parentCommentId": null,
      "content": "Great post!",
      "likeCount": 2,
      "isLikedByCurrentUser": true,
      "likedBy": [ ... ],
      "replies": [
        {
          "id": 2,
          "postId": 1,
          "user": { ... },
          "parentCommentId": 1,
          "content": "Thanks!",
          "likeCount": 0,
          "isLikedByCurrentUser": false,
          "likedBy": [],
          "replies": null,
          "createdAt": "2024-01-15T10:35:00",
          "updatedAt": "2024-01-15T10:35:00"
        }
      ],
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ]
}
```

### 3. Get Comment by ID
**GET** `/comments/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (Single comment object with replies)

### 4. Update Comment
**PUT** `/comments/{id}`
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "Updated comment text"
}
```

**Response:** `200 OK`

### 5. Delete Comment
**DELETE** `/comments/{id}`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK`

### 6. Like/Unlike Comment
**POST** `/comments/{id}/like`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (Returns updated comment)

### 7. Get Comment Likes
**GET** `/comments/{id}/likes`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (List of users who liked)

### 8. Reply to Comment
**POST** `/comments/{id}/reply`
**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "This is a reply to the comment"
}
```

**Response:** `201 Created`

### 9. Get Replies to Comment
**GET** `/comments/{id}/replies`
**Headers:** `Authorization: Bearer <token>`

**Response:** `200 OK` (List of reply comments)

---

## 🚨 Error Responses

### Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email should be valid",
    "password": "Password must be at least 8 characters"
  }
}
```

### Unauthorized
```json
{
  "success": false,
  "message": "You don't have permission to perform this action"
}
```

### Not Found
```json
{
  "success": false,
  "message": "Post not found with ID: 123"
}
```

### Duplicate Resource
```json
{
  "success": false,
  "message": "Email already registered"
}
```

### Bad Credentials
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

---

## 🧪 Testing with Postman/cURL

### Example: Register and Login Flow

1. **Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

2. **Create Post:**
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "content": "My first post!",
    "isPublic": true
  }'
```

3. **Get Feed:**
```bash
curl -X GET "http://localhost:8080/api/posts?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

4. **Like a Post:**
```bash
curl -X POST http://localhost:8080/api/posts/1/like \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

5. **Comment on Post:**
```bash
curl -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "content": "Great post!"
  }'
```

---

## 🎯 Key Features Summary

✅ **Authentication:** JWT + Refresh Token  
✅ **Posts:** CRUD with privacy controls (public/private)  
✅ **Likes:** Toggle like/unlike for posts and comments  
✅ **Comments:** Nested replies with hierarchical structure  
✅ **Feed:** Paginated, sorted by newest first  
✅ **Authorization:** Users can only edit/delete their own content  
✅ **Performance:** Optimized queries with proper indexing  
✅ **Security:** Password hashing, JWT validation, CORS protection  
✅ **Validation:** Input validation on all endpoints  

---

## 📊 Database Schema Overview

**Tables:**
- `users` - User accounts
- `refresh_tokens` - JWT refresh tokens
- `posts` - User posts with privacy settings
- `post_likes` - Many-to-many: users who liked posts
- `comments` - Comments with hierarchical replies
- `comment_likes` - Many-to-many: users who liked comments

**Key Relationships:**
- User → Posts (1:N)
- User → Comments (1:N)
- Post → Comments (1:N)
- Comment → Replies (Self-referencing 1:N)
- Post ↔ Users (Likes M:N via post_likes)
- Comment ↔ Users (Likes M:N via comment_likes)

---
