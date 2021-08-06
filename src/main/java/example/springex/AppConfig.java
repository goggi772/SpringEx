package example.springex;

import example.springex.discount.DiscountPolicy;
import example.springex.discount.FixDiscountPolicy;
import example.springex.discount.RateDiscountPolicy;
import example.springex.member.MemberService;
import example.springex.member.MemberServiceImpl;
import example.springex.member.MemoryMemberRepository;
import example.springex.order.OrderService;
import example.springex.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }


}
