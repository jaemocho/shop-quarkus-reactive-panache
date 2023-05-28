package org.acme.shop.adapter.out.persistence.repository;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class MemberRepository implements PanacheRepositoryBase<MemberEntity, String>{
    
}
