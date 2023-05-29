package org.acme.shop.data.dao.impl;

import java.util.List;

import org.acme.shop.data.dao.MemberDAO;
import org.acme.shop.data.entity.Member;
import org.acme.shop.data.repository.MemberRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberDAOImpl implements MemberDAO {
    
    @Inject
    MemberRepository memberRepository;

    public Uni<Member> save(Member member) {
        return memberRepository.persist(member);
    }

    public Uni<List<Member>> findAll() {
        return memberRepository.findAll().list();
    }

    public Uni<Member> findById(String id){
        return memberRepository.findById(id);
    }

    public Uni<Void> delete(Member member){
        return memberRepository.delete(member);
    }
}
