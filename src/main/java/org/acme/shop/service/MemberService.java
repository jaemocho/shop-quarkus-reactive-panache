package org.acme.shop.service;

import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.data.entity.Member;

import io.smallrye.mutiny.Uni;


public interface MemberService {
    public Uni<Member> addMember(ReqMemberDto reqMemberDto) throws ShopException;

    public Uni<Void> removeMember(String id) throws ShopException;

    public Uni<List<Member>> getAllMember();

    public Uni<Member> getMemberById(String id);

    public Uni<Member> updateMember(String id, ReqMemberDto reqMemberDto) throws ShopException;

}
