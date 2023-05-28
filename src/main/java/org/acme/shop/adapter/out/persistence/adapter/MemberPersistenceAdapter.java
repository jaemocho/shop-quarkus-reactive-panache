package org.acme.shop.adapter.out.persistence.adapter;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.adapter.out.persistence.mapper.DomainEntityMapper;
import org.acme.shop.adapter.out.persistence.repository.MemberRepository;
import org.acme.shop.application.port.out.MemberPersistencePort;
import org.acme.shop.domain.Member;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberPersistenceAdapter implements MemberPersistencePort {
    
    @Inject
    MemberRepository memberRepository;

    @Inject
    DomainEntityMapper<Member, MemberEntity> memberMapper;

    public Uni<MemberEntity> save(MemberEntity memberEntity) {
        return memberRepository.persist(memberEntity);
    }

    public Uni<List<MemberEntity>> findAll() {
        return memberRepository.findAll().list();
    }

    public Uni<MemberEntity> findById(String id){
        return memberRepository.findById(id);
    }

    public Uni<Void> delete(MemberEntity memberEntity){
        return memberRepository.delete(memberEntity);
    }
}
