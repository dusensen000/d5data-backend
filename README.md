# Spring Boot 3.2.0 + Spring Security

Java 17+、Maven 3.6.3+。版本固定为题目要求的 3.2.0。

```sh
mvn test
mvn spring-boot:run
```

| Method | URL | 说明 |
| --- | --- | --- |
| GET | /api/csrf | 匿名可访问，返回 CSRF token，保存响应 session cookie |
| POST | /api/login | 表单 username=test&password=123456，并带 CSRF 请求头和 cookie |
| GET | /api/hello | 登录后带 session cookie，返回纯文本 Hello World；未登录 401 |
| POST | /api/logout | 带当前 CSRF token，成功 204，销毁 session |

登录使用 application/x-www-form-urlencoded，不是 JSON。CSRF 响应中的 headerName 指示请求头名、token 是请求头值。登录后 CSRF token 会轮换，退出前重新 GET /api/csrf。密码使用 BCrypt，示例账号仅存于内存。

服务默认监听 `http://localhost:8080`。下面是使用 Python 自动保留 cookie、获取 token 并调用接口的最短示例。

```sh
python -m pip install httpx
python verify_live.py
```

脚本依次检查未登录访问、错误密码、正确登录、返回 `Hello World`、退出和退出后访问。成功时输出一行 `PASS`。

Java 集成测试不需要 Python，执行 `mvn test` 即可，包含以上主要流程和缺少 CSRF token 时的拒绝检查。
