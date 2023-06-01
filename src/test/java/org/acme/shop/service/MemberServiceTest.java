package org.acme.shop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.MemberDAO;
import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.data.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@QuarkusTest
public class MemberServiceTest {
    
    @Inject
    private MemberService memberService;

    @InjectMock
    private MemberDAO memberDAO;

    @Test
    @RunOnVertxContext
    public void addMember_test(UniAsserter asserter)  {

        //given 
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                            .id("jjm")
                                            .address("수원")
                                            .phoneNumber("010333322222")
                                            .build();

        
        Member member = Member.builder()
                            .id("jjm")
                            .address("수원")
                            .phoneNumber("010333322222")
                            .build();

        when(memberDAO.save(any(Member.class)))
            .thenReturn(Uni.createFrom().item(member));
            

        //when 
        asserter.assertThat(() -> memberService.addMember(reqMemberDto)
            , m -> {
                //then
                Assertions.assertEquals("jjm", m.getId());
            }) ;
    }

    @Test // 등록하려는 member id 가 이미 등록되어있을 때 
    @RunOnVertxContext
    public void addMemberException_test(UniAsserter asserter) {

        //given 
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                            .id("jjm")
                                            .address("수원")
                                            .phoneNumber("010333322222")
                                            .build();

        
        Member member = Member.builder()
                            .id("jjm")
                            .address("수원")
                            .phoneNumber("010333322222")
                            .build();

        when(memberDAO.findById("jjm"))
            .thenReturn(Uni.createFrom().item(member));
        when(memberDAO.save(any(Member.class)))
            .thenReturn(Uni.createFrom().item(member));

        // when then
        asserter.assertFailedWith(()->memberService.addMember(reqMemberDto)
            , ShopException.class);
    }

    @Test
    @RunOnVertxContext
    public void getAllMember_test(UniAsserter asserter){

        // given
        when(memberDAO.findAll())
            .thenReturn(Uni.createFrom().item(initMemberData()));

        // when 
        asserter.assertThat(() -> memberService.getAllMember()
            , members -> {
                // then
                Assertions.assertEquals(3, members.size());
            });
    }

    @Test
    @RunOnVertxContext
    public void getMemberById_test(UniAsserter asserter) throws ShopException {

        // given
        Member member = initMemberData().get(0);

        when(memberDAO.findById(member.getId()))
            .thenReturn(Uni.createFrom().item(member));

        // when
        asserter.assertThat(() -> memberService.getMemberById(member.getId())
            ,m -> {
                // then
                Assertions.assertEquals(member.getId(), m.getId());
            });
    }

    @Test // id가 없을 때 
    @RunOnVertxContext
    public void getMemberByIdException_test(UniAsserter asserter) throws ShopException {

        // given
        Member member = initMemberData().get(0);

        when(memberDAO.findById(member.getId()))
            .thenReturn(Uni.createFrom().nullItem());


        // when then
        asserter.assertFailedWith(()-> memberService.getMemberById(member.getId())
            , ShopException.class);
    }

    @Test
    @RunOnVertxContext
    public void updateMember_test(UniAsserter asserter) throws ShopException {

        // given
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                            .phoneNumber("01122223333")
                                            .address("제주도")
                                            .build();

        List<Member> members = initMemberData();

        Member member = members.get(0);

        when(memberDAO.findById(member.getId()))
            .thenReturn(Uni.createFrom().item(member));

        // when
        asserter.assertThat(() -> memberService.updateMember(member.getId(), reqMemberDto)
            , m -> {
                // then
                Assertions.assertEquals("01122223333", m.getPhoneNumber());
                Assertions.assertEquals("제주도", m.getAddress());
            });
    }

    @Test // update하려는 member id가 없을 때 
    @RunOnVertxContext
    public void updateMemberException_test(UniAsserter asserter) throws ShopException {

        // given
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                            .phoneNumber("01122223333")
                                            .address("제주도")
                                            .build();

        List<Member> members = initMemberData();

        Member member = members.get(0);

        when(memberDAO.findById(member.getId()))
            .thenReturn(Uni.createFrom().nullItem());

        // when then
        asserter.assertFailedWith(()->memberService.updateMember(member.getId(), reqMemberDto)
            , ShopException.class);
    }

    @Test // 삭제하려는  member id가 없을 때 
    @RunOnVertxContext
    public void removeMemberException_test(UniAsserter asserter) throws ShopException {

        // given
        when(memberDAO.findById("memberA"))
            .thenReturn(Uni.createFrom().nullItem());

        // when then
        asserter.assertFailedWith(()->memberService.removeMember("member")
            , ShopException.class);
    }



    private List<Member> initMemberData() {


        List<Member> members = new ArrayList<Member>();

        Member member1 = Member.builder()
                            .id("member1")
                            .address("수원")
                            .phoneNumber("0101112222")
                            .build();

        Member member2 = Member.builder()
                            .id("member2")
                            .address("서울")
                            .phoneNumber("0101113333")
                            .build();        

        Member member3 = Member.builder()
                            .id("member3")
                            .address("대전")
                            .phoneNumber("0101114444")
                            .build();                                

        members.add(member1);
        members.add(member2);
        members.add(member3);

        return members;
    }

}
