package example.springex;

import example.springex.discount.FixDiscountPolicy;
import example.springex.member.MemberService;
import example.springex.member.MemberServiceImpl;
import example.springex.member.MemoryMemberRepository;
import example.springex.order.OrderService;
import example.springex.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }
}
