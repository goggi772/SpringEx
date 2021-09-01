package example.springex.order;

import example.springex.discount.DiscountPolicy;
import example.springex.discount.FixDiscountPolicy;
import example.springex.discount.RateDiscountPolicy;
import example.springex.member.Member;
import example.springex.member.MemberRepository;
import example.springex.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor //final이 붙은 값에 대해 생성자를 만들어줌
public class OrderServiceImpl implements OrderService{

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
    //    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    //final이 있으면 무조건 생성자를 통해 할당이 되어야 한다.
    private final MemberRepository memberRepository;

    //인터페이스에만 의존하도록 변경
    //그러나 객체를 생성하지않아 NullPointException발생
    private final DiscountPolicy discountPolicy;

    /*@Autowired //@RequiredArgsConstructor에 의해 생성자가 자동으로 만들어짐
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }*/

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    //테스트용
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
