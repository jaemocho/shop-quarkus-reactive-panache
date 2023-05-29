package org.acme.shop.data.dao;

import java.util.List;

import org.acme.shop.data.entity.Member;

import io.smallrye.mutiny.Uni;

public interface MemberDAO{
    
    public Uni<Member> save(Member member);
        
    public Uni<List<Member>> findAll();

    public Uni<Member> findById(String id);

    public Uni<Void> delete(Member member);
}
