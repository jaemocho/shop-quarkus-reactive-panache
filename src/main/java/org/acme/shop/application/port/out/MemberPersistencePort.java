package org.acme.shop.application.port.out;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;

import io.smallrye.mutiny.Uni;

public interface MemberPersistencePort {
    public Uni<MemberEntity> save(MemberEntity memberEntity);
        
    public Uni<List<MemberEntity>> findAll();

    public Uni<MemberEntity> findById(String id);

    public Uni<Void> delete(MemberEntity member);
}
