# 카카오페이 뿌리기 기능 구현하기

## Feature

- Java 11
- Spring boot 2.4.2
- JPA
- H2 Database
- gradle

## 빌드 및 실행

- Build

```
./gradlew clean build

```

- Build 된 jar 파일 실행

```
java -jar build/libs/pay-1.0.0-SNAPSHOT.jar
```


- 직접 실행
```
./gradlew bootRun
```


## 문제 해결 전략

- Token이 중복되면 안 된다는 요구사항이 있지 않아 중복 체크 로직은 만들지 않았다. 대신에 distributed_amount 테이블의 Unique Index 제약 조건으로 넣었다.
- 뿌린 금액 받기를 할 때 여러 사용자가 동시에 요청하는 경우 금액 차감이 중복으로 발생할 수도 있는데, 이 문제를 해결하기 위해서 비관적락을 이용하였다. (JPA의 PERSSIMISTIC_WRITE LOCK 이용)
- Layer 별 (Presentation Layer, Application Layer, Domain Layer)로 분리하여 각 모듈별 의존성을 최소화 하였고 각각의 테스트 케이스를 작성하였다.
- amount가 0이면 안된다는 조건은 없어서 0원도 받을 수도 있게 하였다.
- 받기 API는 서버 내부에서는 Update를 하기 때문에 Http Method를 PATCH로 처리하려고 했지만, API를 호출하는 클라이언트 입장에서는 받기이기 때문에 GET으로 처리하였다.

## API 명세

### 1. 뿌리기 API

```
[REQUEST]
POST /amount-to-distributed-to-user

X-ROOM-ID: room1
X-USER-ID: 1
Content-Type: application/json

RequestBody:
{
    "amount": 10000, 
    "numbersOfMemberReceived": 3
}

[RESPONSE]
{
    "token": "D1q"
}
```

### 2. 받기 API

```
[REQUEST]
GET /distributed-amount/{token}

X-ROOM-ID: room1
X-USER-ID: 2
token : D1q

[RESPONSE]
{
    "amount": 3644
}
```

### 3. 조회 API

```
[REQUEST]
GET /distributed-history/{token}

X-ROOM-ID: room1
X-USER-ID: 1
token : D1q

[RESPONSE]
{
    "distributedDateTime": "2021-02-01T20:40:57",
    "totalAmount": 10000,
    "receivedAmount": 6356,
    "receivedInfos": [
        {
            "amount": 3644,
            "userId": 2
        }
    ]
}
```