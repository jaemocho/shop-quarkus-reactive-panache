package org.acme.shop.application.port.in;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.application.service.dto.ReqMemberDto;
import org.acme.shop.common.exception.ShopException;

import io.smallrye.mutiny.Uni;


public interface MemberUsecase {
    public Uni<MemberEntity> addMember(ReqMemberDto reqMemberDto) throws ShopException;

    public Uni<Void> removeMember(String id) throws ShopException;

    public Uni<List<MemberEntity>> getAllMember();

    public Uni<MemberEntity> getMemberById(String id);

    public Uni<MemberEntity> updateMember(String id, ReqMemberDto reqMemberDto) throws ShopException;

}
