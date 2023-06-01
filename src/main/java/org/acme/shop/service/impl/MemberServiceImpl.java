package org.acme.shop.service.impl;

import java.util.List;

import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.MemberDAO;
import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.data.entity.Member;
import org.acme.shop.service.MemberService;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberServiceImpl implements MemberService{
    
    @Inject
    MemberDAO memberDAO;

    @WithTransaction
    public Uni<Member> addMember(ReqMemberDto reqMemberDto) throws ShopException {
        Member newMember = createNewMember(reqMemberDto);
        Uni<Member> memberUni = getMember(reqMemberDto.getId());
        memberUni = vaildateDuplicateMember(memberUni);
        return memberUni.onItem()
                    .ifNull().continueWith(newMember)
                    .flatMap(m -> memberDAO.save(m));
    }

    private Member createNewMember(ReqMemberDto reqMemberDto) {
        Member newMember = Member.builder()
                            .id(reqMemberDto.getId()) 
                            .address(reqMemberDto.getAddress())
                            .phoneNumber(reqMemberDto.getPhoneNumber())
                            .build();
        return newMember;
    }

    private Uni<Member> getMember(String memberId) {
        return memberDAO.findById(memberId);
    }

    private Uni<Member> vaildateDuplicateMember(Uni<Member> memberUni) {
        return memberUni.onItem()
                    .ifNotNull().failWith(
                        new ShopException(ExceptionClass.SHOP
                    , HttpResponseStatus.BAD_REQUEST, "already exist memeber"));
    }

    @WithTransaction
    public Uni<Void> removeMember(String id) throws ShopException{
        Uni<Member> memberUni = getMember(id);
        memberUni = nullCheck(memberUni);
        return memberUni.onItem()
                        .ifNotNull().transformToUni( m -> memberDAO.delete(m));
    }
    
    private Uni<Member> nullCheck(Uni<Member> memberUni) {
        return memberUni.onItem().ifNull()
                .failWith(CommonUtils.throwShopException(Member.class.getSimpleName()));
    }

    @WithTransaction
    public Uni<List<Member>> getAllMember() {
        return memberDAO.findAll();
    }

    public Uni<Member> getMemberById(String id){
        Uni<Member> memberUni = getMember(id);
        return nullCheck(memberUni);
    }

    @WithTransaction
    public Uni<Member> updateMember(String id, ReqMemberDto reqMemberDto) throws ShopException {
        Uni<Member> memberUni = getMember(id);
        memberUni = nullCheck(memberUni);
        return memberUni.onItem()
                        .ifNotNull().invoke( 
                            m -> { 
                                m.updateMember(reqMemberDto.getAddress(), reqMemberDto.getPhoneNumber());});
    }   
}
