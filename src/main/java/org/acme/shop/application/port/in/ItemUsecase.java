package org.acme.shop.application.port.in;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import org.acme.shop.application.service.dto.ReqItemDto;
import org.acme.shop.common.exception.ShopException;

import io.smallrye.mutiny.Uni;

public interface ItemUsecase {
    public Uni<ItemEntity> addItem(ReqItemDto reqItemDto);

    public Uni<Void> removeItem(Long id) throws ShopException;
    
    public Uni<List<ItemEntity>> getAllItem();

    public Uni<List<ItemEntity>> getItemByCategoryId(Long id);

    public Uni<ItemEntity> getItemById(Long id) throws ShopException;

    public Uni<ItemEntity> updateItem(Long id, ReqItemDto reqItemDto) throws ShopException;

    public Uni<ItemEntity> getItem(Long id);

    public Uni<ItemEntity> getItemForUpdate(Long id);

}
