package org.acme.shop.adapter.out.persistence.mapper;

import java.util.List;

public interface DomainEntityMapper<Domain,Entity> {

    List<Entity> domainToEntity(List<Domain> domains);

    Entity domainToEntity(Domain domain);

    List<Domain> entityToDomain(List<Entity> entities);

    Domain entityToDomain(Entity entity);
}
