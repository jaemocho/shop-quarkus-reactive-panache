package org.acme.shop.application.service;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.application.port.in.MemberUsecase;
import org.acme.shop.application.port.out.MemberPersistencePort;
import org.acme.shop.application.service.dto.ReqMemberDto;
import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.exception.ShopException;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberService implements MemberUsecase{
    
    @Inject
    MemberPersistencePort memberPersistencePort;

    @WithTransaction
    public Uni<MemberEntity> addMember(ReqMemberDto reqMemberDto) throws ShopException {
        MemberEntity newMemberEntity = createNewMember(reqMemberDto);
        Uni<MemberEntity> member = getMember(reqMemberDto.getId());
        member = vaildateDuplicateMember(member);
        return member.onItem()
                    .ifNull().continueWith(newMemberEntity)
                    .onItem().transformToUni(m -> memberPersistencePort.save(m));
        // transformToUni 추가되는 i/o 작업이 있을 때 
    }

    private Uni<MemberEntity> vaildateDuplicateMember(Uni<MemberEntity> member) {
        return member.onItem()
                    .ifNotNull().failWith(
                        new ShopException(ExceptionClass.SHOP
                    , HttpResponseStatus.BAD_REQUEST, "already exist memeber"));
    }

    @WithTransaction
    public Uni<Void> removeMember(String id) throws ShopException{
        Uni<MemberEntity> member = getMember(id);
        member = nullCheck(member);
        return member.onItem()
                .ifNotNull().transformToUni( 
                    m -> memberPersistencePort.delete(m));
    }

    @WithTransaction
    public Uni<List<MemberEntity>> getAllMember() {
        return memberPersistencePort.findAll();
    }

    public Uni<MemberEntity> getMemberById(String id){
        Uni<MemberEntity> member = getMember(id);
        return nullCheck(member);
    }

    @WithTransaction
    public Uni<MemberEntity> updateMember(String id, ReqMemberDto reqMemberDto) throws ShopException {
        Uni<MemberEntity> member = getMember(id);
        member = nullCheck(member);
        return member.onItem()
                .ifNotNull().transformToUni(  // update만 해도 변경됨 
                            (m) -> {m.updateMember(reqMemberDto.getAddress(), reqMemberDto.getPhoneNumber());
                                    return memberPersistencePort.save(m); });
    }   

    private Uni<MemberEntity> getMember(String memberId) {
        return memberPersistencePort.findById(memberId);
    }

    private Uni<MemberEntity> nullCheck(Uni<MemberEntity> member) {
        return member.onItem().ifNull()
                .failWith(CommonUtils.throwShopException(MemberEntity.class.getSimpleName()));
    }

    private MemberEntity createNewMember(ReqMemberDto reqMemberDto) {
        MemberEntity newMembereEntity = MemberEntity.builder()
                            .id(reqMemberDto.getId()) 
                            .address(reqMemberDto.getAddress())
                            .phoneNumber(reqMemberDto.getPhoneNumber())
                            .build();
        return newMembereEntity;
    }

}
