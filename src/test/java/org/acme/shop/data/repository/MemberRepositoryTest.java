package org.acme.shop.data.repository;

import org.acme.shop.data.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;

@QuarkusTest
public class MemberRepositoryTest {
    
    @Inject
    private MemberRepository memberRepository;

    @Test
    @TestReactiveTransaction
    public void member_test(UniAsserter asserter) {
        
        // 초기 member date insert 
        Member member1 = Member.builder()
                            .id("memberA")
                            .address("서울 여기 저기")
                            .phoneNumber("010 1234 5678")
                            .build();

        Member member2 = Member.builder()
                            .id("memberB")
                            .address("수원 여기 저기")
                            .phoneNumber("010 1234 7890")
                            .build();
        
        asserter
        .execute(() -> memberRepository.persistAndFlush(member1))
        .execute(() -> memberRepository.persistAndFlush(member2));

        asserter
        // 전체 member 조회 테스트
        .assertThat(() -> memberRepository.findAll().list(), 
            members -> {
                Assertions.assertEquals(2, members.size());
            })
        // memberA 조회 테스트
        .assertThat(() -> memberRepository.findById("memberA") ,
            member -> {
                Assertions.assertNotNull(member);
                Assertions.assertEquals("서울 여기 저기", member.getAddress());
                
                // update 테스트 
                member.updateMember("부산 여기 저기", member.getPhoneNumber());
                asserter.assertThat(() -> memberRepository.findById("memberA"), 
                 updatedMember -> {
                    Assertions.assertEquals("부산 여기 저기", updatedMember.getAddress());
                 });

            });

        // 삭제 테스트
        asserter
        .execute(() -> memberRepository.delete(member1))
        
        // 전체 member 조회 
        .assertThat(() -> memberRepository.findAll().list(), 
            members -> {
                Assertions.assertEquals(1, members.size());
        });
    }
}
