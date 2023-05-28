package org.acme.shop.application.service;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import org.acme.shop.application.port.in.ItemUsecase;
import org.acme.shop.application.port.out.CategoryPersistencePort;
import org.acme.shop.application.port.out.ItemPersistencePort;
import org.acme.shop.application.service.dto.ReqItemDto;
import org.acme.shop.application.service.dto.RespItemDto;
import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.domain.Category;
import org.acme.shop.domain.Item;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ItemService implements ItemUsecase{
    
    @Inject
    ItemPersistencePort itemPersistencePort;

    @Inject
    CategoryPersistencePort categoryPersistencePort;

    @WithTransaction
    public Uni<ItemEntity> addItem(ReqItemDto reqItemDto) {
        ItemEntity itemEntity = createNewItemEntity(reqItemDto);

        Uni<CategoryEntity> category = categoryPersistencePort.findById(reqItemDto.getCategoryId());
        return category.onItem().ifNotNull()
                .transformToUni( c -> { itemEntity.setCategoryEntity(c);
                                   return itemPersistencePort.save(itemEntity);})
                .onItem().ifNull().continueWith(itemEntity)
                .onItem().transformToUni(i -> itemPersistencePort.save(i));
    }

    @WithTransaction
    public Uni<Void> removeItem(Long id) throws ShopException {
        Uni<ItemEntity> item = getItem(id);
        item = nullCheck(item);
        return item
                .onItem().transformToUni(i -> itemPersistencePort.delete(i));
    }

    private Uni<ItemEntity> nullCheck(Uni<ItemEntity> item) {
        return item.onItem().ifNull()
                .failWith(CommonUtils.throwShopException(ItemEntity.class.getSimpleName()));
    }

    @WithTransaction
    public Uni<List<ItemEntity>> getAllItem() {
        return itemPersistencePort.findAll();
    }

    public Uni<ItemEntity> getItemById(Long id) throws ShopException {
        Uni<ItemEntity> item = getItem(id);
        return nullCheck(item);
    }

    public Uni<List<ItemEntity>> getItemByCategoryId(Long id) {
        return itemPersistencePort.findByCategoryId(id);
    }

    @WithTransaction
    public Uni<ItemEntity> updateItem(Long id, ReqItemDto reqItemDto) throws ShopException{
        Uni<ItemEntity> item = getItemForUpdate(id);
        item = nullCheck(item);
        return item.onItem()
            .invoke(i -> i.updateItem(reqItemDto.getName(), reqItemDto.getPrice(), reqItemDto.getRemainQty()));
    }

    private ItemEntity createNewItemEntity(ReqItemDto reqItemDto) {
        ItemEntity itemEntity = ItemEntity.builder()
                                    .name(reqItemDto.getName())
                                    .price(reqItemDto.getPrice())
                                    .remainQty(reqItemDto.getRemainQty())
                                    .build();
        return itemEntity;
    }

    public Uni<ItemEntity> getItem(Long id) {
        return itemPersistencePort.findById(id);
    }

    public Uni<ItemEntity> getItemForUpdate(Long id) {
        return itemPersistencePort.findByIdForUpdate(id);
    }



}
