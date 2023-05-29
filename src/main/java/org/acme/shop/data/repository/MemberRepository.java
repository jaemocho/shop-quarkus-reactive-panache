package org.acme.shop.data.repository;


import org.acme.shop.data.entity.Member;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class MemberRepository implements PanacheRepositoryBase<Member, String>{
    
}
