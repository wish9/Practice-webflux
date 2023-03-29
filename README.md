[블로그 포스팅 주소](https://velog.io/@wish17/%EC%BD%94%EB%93%9C%EC%8A%A4%ED%85%8C%EC%9D%B4%EC%B8%A0-%EB%B0%B1%EC%97%94%EB%93%9C-%EB%B6%80%ED%8A%B8%EC%BA%A0%ED%94%84-69%EC%9D%BC%EC%B0%A8-Spring-WebFlux-%EB%A6%AC%EC%95%A1%ED%8B%B0%EB%B8%8C-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D)

# [Spring WebFlux] 리액티브 프로그래밍


> 리액티브 시스템(Reactive System)
-  클라이언트의 요청에 반응을 잘하는 시스템
- 클라이언트의 요청에 대한 응답 대기 시간을 최소화 할 수 있도록 요청 쓰레드가 차단되지 않게 함으로써(Non-Blocking) 클라이언트에게 즉각적으로 반응하도록 구성된 시스템

### 리액티브 시스템 특징

[![](https://velog.velcdn.com/images/wish17/post/ccaad4b5-6138-447d-9972-5425838adc2d/image.png)](https://www.reactivemanifesto.org/)

#### MEANS

- 리액티브 시스템에서 사용하는 **커뮤니케이션 수단**을 의미
    - Message Driven
        - 리액티브 시스템에서는 메시지 기반 통신을 통해 여러 시스템 간에 느슨한 결합을 유지


#### FORM

- 메시지 기반 통신을 통해 리액티브 시스템이 **어떤 특성을 가지는 구조로 형성되는지**를 의미
    - Elastic
    	- 시스템으로 들어오는 요청량이 적거나 많거나에 상관없이 일정한 응답성을 유지하는 것을 의미
    - Resillient
    	- 시스템의 일부분에 장애가 발생하더라도 응답성을 유지하는 것을 의미

#### VALUE

- 액티브 시스템의 핵심 가치가 무엇인지를 표현하는 영역이다.
    - Responsive
        - 리액티브 시스템은 클라이언트의 요청에 즉각적으로 응답할 수 있어야 함을 의미
    - Maintainable
    	- 클라이언트의 요청에 대한 즉각적인 응답이 지속가능해야 함을 의미
    - Extensible
    	- 클라이언트의 요청에 대한 처리량을 자동으로 확장하고 축소할 수 있어야 함을 의미

***

## 리액티브 프로그래밍(Reactive Programming)

- 리액티브 시스템에서 사용되는 프로그래밍 모델을 의미
- Non-Blocking 통신을 위한 프로그래밍 모델

### 리액티브 프로그래밍의 특징

1. 지속적으로 발생하는 데이터를 하나의 데이터 플로우로 보고 데이터를 자동으로 전달한다.
    - 리액티브 프로그래밍에서는 데이터가 지속적으로 발생하는 것 자체를 데이터에 어떤 **변경이 발생**했다고 인지한다.
    - 변경 자체를 이벤트로 간주하고, 이벤트가 발생할 때 마다 데이터를 계속해서 전달한다.

2. 높은 응답성(Responsiveness)
- 시스템이 외부 요청에 대해 즉각적으로 반응하여 응답할 수 있어야 한다.

3. 탄력성(Elasticity)
- 시스템이 부하량의 증감에 따라 유연하게 대처할 수 있어야 한다.

4. 복원성(Resilience)
- 시스템이 일부분이 손상되었을 경우에도 전체적인 기능을 유지하며 응답할 수 있어야 한다.

5. 메시지 기반 통신(Message-driven)
- 시스템의 컴포넌트 간에는 비동기적인 메시지 전달을 통해 통신한다.

6. 무상태성(Statelessness)
- 시스템의 컴포넌트는 상태 정보를 유지하지 않으며, 모든 요청은 독립적으로 처리된다.

7. 비차단성(Non-blocking)
- I/O 작업 등의 블로킹 작업이 발생해도 다른 작업을 중단시키지 않고 계속 처리할 수 있어야 한다.

***

### 리액티브 스트림즈(Reactive Streams)

> 리액티브 스트림즈(Reactive Streams)
- 리액티브 프로그래밍을 위한 표준 사양(또는 명세, Specification)

#### 리액티브 스트림즈 컴포넌트

>``Publisher`` 인터페이스
- 데이터 소스로 부터 데이터를 내보내는(emit) 역할
- 데이터 스트림을 생성하고, ``Subscriber``에게 데이터를 전달하는 역할을 한다.
- ``subscribe()`` 메서드를 통해 ``Subscriber``를 등록하고, 데이터를 전달할 준비가 되면 ``onNext()`` 메서드를 호출하여 데이터를 전송한다.
```java
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```
- ``subscribe()``의 파라미터로 전달되는 ``Subscriber``가 ``Publisher``로부터 내보내진 데이터를 소비하는 역할을 한다.
    - ``subscribe()`` 는 ``Publisher``가 내보내는 데이터를 수신할 지 여부를 결정하는 구독의 의미를 가지고 있으며, 일반적으로 ``subscribe()``가 호출되지 않으면 ``Publisher``가 데이터를 내보내는 프로세스는 시작되지 않는다.

> ``Subscriber`` 인터페이스
- ``Publisher``로부터 내보내진 데이터를 소비하는 역할
```java
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```
- onSubscribe(Subscription s)
    - 이 메서드는 ``Publisher``로부터 ``Subscription``을 받는다.
    - `Subscription` 객체를 통해 `Subscriber`는 데이터 요청 및 취소를 처리할 수 있다.
    - ``onSubscribe()`` 메서드에서는 보통 ``Subscription``의 ``request()`` 메서드를 호출하여 데이터를 요청한다.
- onNext(T t)
    - 이 메서드는 ``Publisher``로부터 데이터를 수신할 때 호출된다.
    - 데이터 처리 로직을 구현하는 곳으로, ``Subscriber``는 여기에서 전달받은 데이터를 처리한다.
    - ``onNext()`` 메서드가 호출될 때마다 새로운 데이터를 받아 처리할 수 있다.
- onError(Throwable t)
    - 이 메서드는 데이터 처리 중 오류가 발생했을 때 호출된다.
    - 오류 처리 로직을 구현하는 곳으로, 오류가 발생한 경우 이 메서드를 통해 ``Subscriber``는 오류에 대한 처리를 수행할 수 있다.
- onComplete()
    - 이 메서드는 ``Publisher``가 데이터 스트림의 전송이 완료되었음을 알릴 때 호출된다.
    - 스트림 종료 시 수행할 작업을 구현하는 곳으로, 예를 들어 리소스 해제나 최종 결과를 처리하는 로직을 구현할 수 있다.

> ``Subscription`` 인터페이스
- Publisher와 Subscriber 사이의 연결을 나타내는 객체로, 데이터 요청 및 취소를 처리한다. 
```java
public interface Subscription {
    public void request(long n);
    public void cancel();
}
```
- ``request()`` 메서드 
    - ``Subscriber``가 요청한 데이터 양을 지정
- ``cancel()`` 메서드
    - 데이터 전송을 중단하는 메서드
    - 구독을 해지하는 역할
    - 즉, 구독 해지가 발생하면 ``Publisher``는 더이상 데이터를 emit하지 않는다.

> ``Processor`` 인터페이스
- Publisher와 Subscriber의 역할을 동시에 수행하여, 데이터 스트림을 변환하거나 조작하는데 사용된다.
- 별도로 구현해야 되는 추상 메서드는 없다.
- 보통 중간에서 데이터 처리를 위한 로직을 적용하기 위해 사용된다.
```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

#### 리액티브 스트림즈의 구현체

<ol><li><p>Project Reactor</p><ul><li>Spring과 잘 호환되는 리액티브 스트림즈 구현체이다.</li><li>Spring5의 리액티브 스택에 포함되어 있어 Spring Reactive Application 구현에 핵심적인 역할을 담당한다.</li></ul></li><li><p>RxJava</p><ul><li>넷플릭스에서 개발한 .NET 기반의 리액티브 라이브러리를 Java 언어로 포팅한 JVM 기반의 리액티브 확장 라이브러리이다.</li><li>2.0 버전부터 리액티브 스트림즈 표준 사양을 준수한다.</li></ul></li><li><p>Java Flow API</p><ul><li>Java 9부터 지원하는 리액티브 스트림즈 표준 사양이다.</li><li>구현체가 아니라 리액티브 스트림즈 사양을 Java에 포함시킨 구조로, SPI(Service Provider Interface) 역할을 한다.</li></ul></li><li><p>기타 리액티브 확장(Reactive Extension)</p><ul><li>다양한 프로그래밍 언어에서 리액티브 스트림즈를 구현한 리액티브 확장 라이브러리가 제공된다.</li><li>대표적으로 RxJS, RxAndroid, RxKotlin, RxPython, RxScala 등이 있다.</li></ul></li></ol>

***

### 리액티브 프로그래밍 핵심 포인트

<ul><li><p>리액티브 시스템은 <strong>클라이언트의 요청에 반응을 잘하는 시스템</strong>을 의미한다.</p></li><li><p>리액티브 프로그래밍은 리액티브 시스템에서 사용되는 프로그래밍 모델이다.</p></li><li><p>리액티브 스트림즈(Reactive Streams)는 리액티브 프로그래밍을 위한 <strong>표준 사양(또는 명세, Specification)</strong>이다.</p></li><li><p>리액티브 스트림즈는 아래의 네 개 컴포넌트로 구성된다.</p><ul><li><p>Publisher</p></li><li><p>Subscriber</p></li><li><p>Subscription</p></li><li><p>Processor</p></li></ul></li><li><p>다음과 같은 리액티브 스트림즈의 구현체가 사용된다.</p><ul><li><p>Project Reactor</p></li><li><p>RxJava</p></li><li><p>Java Flow API</p></li><li><p>기타 리액티브 확장(Reactive Extension)</p></li></ul></li></ul>

- ``Publisher``는 데이터를 emit하는 역할을 하며, ``Subsciber``는 `Publisher`가 emit한 데이터를 전달 받아서 소비하는 역할을 한다.

***

### 리액티브 프로그래밍에서 사용되는 용어 정의

<ol><li><p>Publisher</p><ul><li>데이터를 내보내는 주체이다.</li></ul></li><li><p>Emit</p><ul><li>Publisher가 데이터를 내보내는 행위를 의미한다.</li></ul></li><li><p>Subscriber</p><ul><li>Publisher가 emit한 데이터를 전달받아 소비하는 주체이다.</li></ul></li><li><p>Subscribe</p><ul><li>구독을 의미한다.</li><li> subscribe() 메서드를 호출해 구독을 한다.</li></ul></li><li><p>Signal</p><ul><li>Publisher가 발생시키는 이벤트를 의미한다.</li></ul></li><li><p>Operator(연산자)</p><ul><li>리액티브 프로그래밍에서 어떤 동작을 수행하는 메서드를 의미한다.</li><li>예시: fromIterable(), filter(), reduce() 등이 Operator다.</li></ul></li><li><p>Sequence</p><ul><li>Operator 체인으로 표현되는 데이터의 흐름을 의미한다.</li><li>Operator 체인으로 작성된 코드 자체가 하나의 Sequence다.</li></ul></li><li><p>Upstream / Downstream</p><ul><li>예시: filter() Operator를 기준으로 위쪽의 fromIterable()은 Upstream이 되고, 아래쪽의 reduce() Operator는 Downstream이 된다.</li></ul></li></ol>


***

### 핵심 포인트2

- 선언형 프로그래밍 방식은 실행할 코드를 선언만 하며, 순차적으로 실행되지 않는다.

- 실행 로직을 작성한 코드대로 실행이 되는 명령형 프로그래밍 방식과 달리 선언형 프로그래밍 방식은 실행 로직의 일부만 람다 표현식으로 전달하며 전달 받은 람다 표현식을 기반으로 Operation 메서드 내부에서 전체 로직을 실행한다.

