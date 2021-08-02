package example.springex.order;

import example.springex.discount.DiscountPolicy;
import example.springex.discount.FixDiscountPolicy;
import example.springex.discount.RateDiscountPolicy;
import example.springex.member.Member;
import example.springex.member.MemberRepository;
import example.springex.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    //    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    //인터페이스에만 의존하도록 변경
    //그러나 객체를 생성하지않아 NullPointException발생
    private DiscountPolicy discountPolicy;

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
