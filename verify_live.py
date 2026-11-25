"""Run against mvn spring-boot:run; verifies real CSRF and session cookies."""
import httpx

with httpx.Client(base_url='http://127.0.0.1:8080') as client:
    assert client.get('/api/hello').status_code == 401
    csrf = client.get('/api/csrf').json()
    headers = {csrf['headerName']: csrf['token']}
    assert client.post('/api/login', data={'username': 'test', 'password': 'wrong'}, headers=headers).status_code == 401
    assert client.post('/api/login', data={'username': 'test', 'password': '123456'}, headers=headers).status_code == 200
    response = client.get('/api/hello')
    assert response.status_code == 200 and response.text == 'Hello World'
    csrf = client.get('/api/csrf').json()
    assert client.post('/api/logout', headers={csrf['headerName']: csrf['token']}).status_code == 204
    assert client.get('/api/hello').status_code == 401
    print('PASS: anonymous 401, invalid password 401, login 200, Hello World, logout 204, anonymous 401')
