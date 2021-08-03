package example.springex;

import example.springex.discount.DiscountPolicy;
import example.springex.discount.FixDiscountPolicy;
import example.springex.member.MemberService;
import example.springex.member.MemberServiceImpl;
import example.springex.member.MemoryMemberRepository;
import example.springex.order.OrderService;
import example.springex.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }


}
