package org.acme.shop.adapter.out.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import org.acme.shop.domain.Category;
import org.acme.shop.domain.Item;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ItemMapper implements DomainEntityMapper<Item,ItemEntity>{

    @Inject
    DomainEntityMapper<Category, CategoryEntity> categoryMapper;

    public List<ItemEntity> domainToEntity(List<Item> items) {
        List<ItemEntity> itemEntities = new ArrayList<ItemEntity>();
        for(Item i : items) {
            itemEntities.add(domainToEntity(i));
        }
        return itemEntities;   
    }

    public ItemEntity domainToEntity(Item item) {
        if ( item == null ) {
            return null;
        }
        return ItemEntity.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .price(item.getPrice())
                            .remainQty(item.getRemainQty())
                            .categoryEntity(categoryMapper.domainToEntity(item.getCategory()))
                            .build();
    }

    public List<Item> entityToDomain(List<ItemEntity> itemEntities)  {
        List<Item> items = new ArrayList<Item>();
        for(ItemEntity i : itemEntities) {
            items.add(entityToDomain(i));
        }
        return items;
    }

    public Item entityToDomain(ItemEntity itemEntity) {
        if( itemEntity == null ) {
            return null;
        }
        return Item.builder()
                    .id(itemEntity.getId())
                    .name(itemEntity.getName())
                    .price(itemEntity.getPrice())
                    .remainQty(itemEntity.getRemainQty())
                    .category(categoryMapper.entityToDomain(itemEntity.getCategoryEntity()))
                    .build();
    }
}
