package example.springex;

import example.springex.member.Grade;
import example.springex.member.Member;
import example.springex.member.MemberService;
import example.springex.member.MemberServiceImpl;
import example.springex.order.Order;
import example.springex.order.OrderService;
import example.springex.order.OrderServiceImpl;

public class OrderApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        OrderService orderService = new OrderServiceImpl();

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        System.out.println("order = " + order.toString());
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
