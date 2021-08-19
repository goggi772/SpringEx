## Spring 통해 만들어보는 예제
* 예제를 따라해보며 스프링의 기본적인 핵심원리를 습득한다.

* 먼저 Spring을 사용하지 않고 순수 자바만을 이용해 프로젝트를 진행한다.

### 2021/7/31

**프로젝트 생성**
-비즈니스 요구사항
1. 회원가입과 조회를 할 수 있다.
2. 회원은 일반과 VIP 두가지 등급이 있다.
3. 회원 데이터 관리방법은 미확정이다.(자체 DB를 구축할수도 있고 외부 시스템과 연동할 수도 있다.)
4. 회원은 상품을 주문할 수 있고 회원 등급에 따라 할인 정책을 적용할 수 있다.
5. 할인정책: 모든 VIP는 1000원을 할인해 주는 고정 금액 할인을 적용한다.(추후 변경될수도 있다.)
6. 할인정책은 변경될 가능성이 매우 높다. 기본 할인정책을 정하지 못하였고 최악의 경우 할인 정책을 적용하지 않을수도 있다.


**회원 도메인 설계**

- 회원 등급을 나타내는 grade enum클래스를 생성한다.<br/>

- 회원 엔티티인 Member 클래스를 생성한다. Member에는 id, name, grade가 있다.<br/>

- 회원가입과 조회를 할 수 있는 회원 저장소 MemberRepository 인터페이스와 회원 서비스 MemberService 인터페이스를 생성
기본적으로 MemberRepository는 save와 findById 메소드가 있고 MemberService는 join과 findMember 메소드가 있다.<br/>

- 일단 회원 저장소는 메모리를 사용하여 하는것으로 한다.<br/>

- 메모리 회원 저장소 구현체인 MemoryMemberRepository클래스와 회원 서비스 구현체인 MemberServiceImpl클래스를 생성한다.<br/>

회원 도메인 테스트

*MemberApp은 순수 자바만을 이용하여 테스트를 함

*junit을 사용하여 MemberServiceTest를 진행


### 2021/8/1


**주문과 회원 도메인 개발** + 테스트

*할인 정책은 변경될수 있으므로 인터페이스를 만들어 개발한다.

- 할인 정책 인터페이스 DiscountPolicy를 만든다.

- 고정으로 할인해주는 구현체 FixDiscountPolicy를 만들고 회원 등급에 따라 할인해주는 메소드 discount를 만든다.

- 그 다음으로 주문 도메인 Order 클래스를 생성한다. Order클래스에는 memberId, itemName, itemPrice, dicountPrice가 있다.

- 주문 도메인 인터페이스 OrderService를 생성해 createOrder메소드를 만든다.

- 구현체 OrderServiceImpl를 생성한다.

*OrderApp 또한 순수 자바만을 이용하여 테스트 진행


### 2021/8/2


**정률 할인 정책**

- 기획자가 고정할인정책에서 정률할인정책으로 변경 요구하였다고 가정

- 정률 할인 정책 RateDiscountPolicy 클래스를 만들어 새로운 할인 정책으로 변경

- FixDiscountPolicy에서 RateDiscountPolicy로 적용

*RateDiscountPolicyTest를 만들어 테스트 진행*

- 할인 정책을 변경하려면 주문 도메인 OrderServiceImpl을 수정해야한다. 이는 OCP와 DIP를 위반하게된다.

- OrderServiceImpl는 DiscountPolicy인터페이스와 구현체 둘다 의존하므로 인터페이스에만 의존하도록 수정해야한다.
하지만 그렇게 수정하게 되면 NullPointException이 발생한다.<br/>```(OrderServiceImpl : private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); ```<br/>```-> private DiscountPolicy discountPolicy;)```



### 2021/8/3


**관심사의 분리**

이전까지는 구현체 내에서 객체를 생성해 실행해 왔었다. 하지만 그렇게 하면 구현체가 다양한 책임을 가지게 된다.<br/>
그러므로 구현객체를 생성하고 연결하는 책임을 대신 가지는 별도의 클래스(AppConfig)를 만들어야한다.

- AppConfig는 실제 동작에 필요한 객체를 생성하는 역할을 하고 객체 인스턴스 참조를 생성자를 통해서 주입해준다.
- 구현체 MemberServiceImpl, OrderServiceImpl에 생성자를 주입한다.
- 이제 MemberServiceImpl과 OrderServiceImpl은 오로지 인터페이스에만 의존할 수 있게 되었다.
- 이는 DIP를 만족함과 동시에 관심사의 분리 또한 만족한다.(객체를 생성하는 역할과 실행하는 역할이 명확히 분리되었다.

       AppConfig appConfig = new appConfig();
       MemberService memberService = appConfig.memberService();
    
이처럼 객체를 생성할때 AppConfig를 통해 생성한다.
- 처음 구현한 AppConfig는 중복이 있고 역할에 따른 구현이 잘 구분되지 않는다.
- 따라서 리팩터링을 통해 중복을 제거하고 역할에 따른 구현이 잘 보이도록 한다.
- 이는 전체 구성이 어떻게 되어있는지 빠르게 파악할 수 있다.


### 2021/8/6


**스프링으로 전환**
- 지금까지는 오직 자바로 DI를 적용했다. 이제는 스프링을 사용해보자.
- AppConfig에 ```@Configuration```을 붙이고 각 메소드에 ```@Bean```을 붙이면 스프링 컨테이너에 스프링 빈이 등록된다.
- 그러면 이전에 AppConfig를 통해 생성한 객체는 필요가 없고 위 코드로 스프링 컨테이너에 생성되어 있는 객체를 불러올 수 있다.

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        
- ApplicationContext를 스프링 컨테이너라 하며 스프링 컨테이너는 ```@Configuration```이 붙은것을 설정 정보로 사용한다.
- 그리고 ```@Bean```이 붙은 메소드들을 모두 호출해 반환된 객체를 스프링 컨테이너에 등록한다.
- 이는 이전에 직접 자바코드로 짠 것보다 훨씬 큰 장점이 생긴다.
- 스프링 컨테이너는 스프링 빈을 생성하고 의존관계를 주입하는데 단계가 정해져 있다. 먼저 스프링 빈이 생성되면 스프링 컨테이너는 설정 정보를 참고하여 의존관계를 주입한다.

* 스프링 빈 조회 테스트

- ApplicationContextInfoTest는 스프링 빈들이 잘 등록되었는지 확인하는 테스트 클래스이다.
- ```getDefinitionNames()```를 사용하면 등록된 스프링 빈들의 이름을 출력해준다.
- 스프링 내부에서 사용하는 빈들을 제외한 내가 등록한 스프링 빈들만 출력해보자
- 스프링 내부에서 사용하는 빈들은 ```getRole()```로 구분이 가능하다.


       if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) //직접 등록한 스프링 빈인지 확인
       
 ROLE_APPLICATION: 직접 등록한 애플리케이션 빈 <br/>
 ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈


### 2021/8/8 ~ 9


**스프링 빈 조회 테스트**

- 스프링 빈을 조회할 수 있는 방법을 테스트 해보자
- 스프링 빈 조회 - 동일한 타입이 두개 이상일때
- 타입으로 스프링 빈 조회할시 동일한 타입이 두개 이상이면 오류가 발생한다.
- 이럴때는 스프링 빈의 이름을 지정해야한다.
- ```getBeansOfType```을 이용하면 해당 타입의 모든 빈의 조회할 수 있다.
- 스프링 빈 조회 - 상속관계
- 상속관계에 있는 타입을 조회하면 자식 타입과 부모타입 둘다 조회된다.
- 그리고 모든 타입의 부모타입는 Object타입 이므로 Object타입을 조회하면 모든 스프링 빈을 조회할 수 있다.


### 2021/8/10


**BeanFactory와 ApplicationContext**

- BeanFactory: 스프링 컨테이너의 최상위 인터페이스, 스프링 빈을 관리하고 조회하는 역할
- ApplicationContext: BeanFactory를 상속받음, 빈 관리기능 + 편리한 부가기능 제공
- 즉 ApplicationContext는 BeanFactory + 편리한 부가기능 이다.<br/>
ApplicationContext의 부가기능<br/>
- MessageSource: 메시지소스를 활용한 국제화 기능
- EnvironmentCapable: 환경변수(로컬, 개발, 운영을 구분해서 처리)
- ApplicationEventPublisher: 애플리케이션 이벤트
- ResourceLoader: 편리한 리소스 조회


**XML**

- 스프링 컨테이너는 다양한 형식의 설정정보를 받아들일수 있다.
- AnnotationConfigApplicationContext -> AppConfig.class (자바코드)
- GenericXmlApplicationContext -> appConfig.xml(Xml)
- ~~ ApplicationContext -> appConfig.~~ (임의)
- Xml기반의 설정 정보인 appConfig.xml을 보면 자바코드로 된 AppConfig.class와 거의 비슷하다.
- 최근 실무에서는 Xml을 잘 사용하지 않고 주로 Spring Boot를 이용해 자바코드로 사용한다.


**스프링 빈 설정 메타 정보(BeanDefinition)**

- 스프링은 ```BeanDefinition``` 추상화를 이용하여 다양한 설정 형식을 지원한다.
- BeanDefinition은 역할과 구현을 개념적으로 나눈것
- 스프링 컨테이너는 설정 정보가 xml인지 자바코드인지 BeanDefinition을 통해 구분한다. 즉 스프링 컨테이너는 오직 BeanDefinition만 알면된다.
- AnnotationConfigApplicationContext를 보면 AnnotatedBeanDefinitionReader를 통해 설정 정보를 읽고 BeanDefinition을 생성한다.
- GenericXmlApplicationContext를 보면 XmlBeanDefinitionReader를 통해 설정 정보를 읽고 BeanDefinition을 생성한다.

BeanDefinition정보<br/>
- BeanClassName: 생성할 빈의 클래스 명(자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)
- factoryBeanName: 팩토리 역할의 빈을 사용할 경우 이름, 예) appConfig
- factoryMethodName: 빈을 생성할 팩토리 메서드 지정, 예) memberService
- Scope: 싱글톤(기본값)
- lazyInit: 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때 까지 최대한
생성을 지연처리 하는지 여부
- InitMethodName: 빈을 생성하고, 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
- DestroyMethodName: 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명
- Constructor arguments, Properties: 의존관계 주입에서 사용한다. (자바 설정 처럼 팩토리 역할
의 빈을 사용하면 없음)

- 이러한 BeanDefinition은 실무에서 직접 정의하거나 사용할일이 거의 없다.


### 2021/8/12


**싱글톤 패턴**

- 싱글톤이란 여러 클라이언트가 요청을 할때 클라이언트 수 만큼 객체를 만드는것이 아니라 한개만 만들어 쓰는것이다.
- 즉, 클래스의 인스턴스가 무조건 1개만 생성되도록 디자인하는 패턴이다. 2개이상 생성되면 안된다.
- 그래서 예제 ``` SingletonService``` 에서 static영역에 객체를 딱 1개 생성하고 private생성자를 만들어 외부에서 객체를 만들지 못하게 하였다.

*싱글톤 패턴의 문제점*

- 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
- 의존관계상 클라이언트가 구체 클래스에 의존한다. DIP를 위반한다.
- 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다.
- 테스트하기 어렵다.
- 내부 속성을 변경하거나 초기화 하기 어렵다.
- private 생성자로 자식 클래스를 만들기 어렵다.
- 결론적으로 유연성이 떨어진다.
- 안티패턴으로 불리기도 한다. <br/>

- 그래서 싱글톤 컨테이너를 사용해야한다.


**싱글톤 컨테이너**

- 싱글톤 컨테이너는 싱글톤 패턴의 문제점을 다 해결하면서 객체 인스턴스를 싱글톤으로 관리할 수 있다.
- 싱글톤 패턴을 따로 적용하지 않아도 자동으로 싱글톤으로 관리해준다.
- 스프링 컨테이너 덕분에 클라이언트의 요청이 올 때 마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유
해서 효율적으로 재사용할 수 있다.
**싱글톤 방식에서 주의할점!!**

       //ThreadA: userA가 10000원 주문
        statefulService1.order("userA", 10000);

        //ThreadB: userB가 20000원 주문
        statefulService2.order("userB", 20000);

        //ThreadA: userA 주문 금액 조회
        int price = statefulService1.getPrice();
        System.out.println("price = " + price);
        
        //result -> price = 20000
        
- ```StatefulServiceTest```를 돌려보면 나는 userA의 주문금액 10000원을 조회했지만 중간에 생성한 userB의 주문금액인 20000원이 return되었다.
- 이는 싱글톤 방식인 객체를 하나만 만들어 공유하였기 때문에 발생한 문제이다.
- 이러한 오류를 막기 위해 스프링 빈은 항상 무상태(stateless)로 설계해야한다.


### 2021/8/20


**컴포넌트 스캔**
(컴포넌트 스캔과 의존관계 자동 주입)

- ```AppConfig```와 같이 스프링 빈을 직접 입력해서 등록할 수도 있지만 컴포넌트 스캔을 이용하면 자동으로 스프링 빈을 등록할 수 있다.
- 컴포넌트 스캔을 사용하려면 설정 정보 클래스에 ```@ComponentScan```을 붙여줘야 한다.
- 이 ```@ComponentScan```은 ```@Component``` 애노테이션이 붙은 클래스를 자동으로 스캔하여 스프링 빈으로 등록한다.
- ```AutoAppConfig```에 있는 ```excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class```는 직접 입력하고 테스트용으로 만든
스프링 빈을 제외하기 위해 ```@Configuration```이 붙은 것은 컴포넌트 스캔을 하지 못하도록 막은것이다. (@Configuration안에 @Component가 있어서 @Configuration 또한 자동으로 스캔된다.)
- 컴포넌트 스캔을 이용하여 스프링 빈으로 등록했지만 의존관계는 아직 주입되지 않았다. 
- 하지만 의존관계 또한 ```@AutoWired```를 통해 자동으로 주입할 수 있다. 
- 클래스 생성자에 ```@AutoWired``` 애노테이션을 붙이면 스프링이 자동으로 의존관계를 주입해 준다.
- 앞에서 했던 ac.getBean(MemberRepository.class)와 비슷하다고 이해하면 된다.
