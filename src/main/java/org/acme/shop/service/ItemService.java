package org.acme.shop.service;

import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.data.entity.Item;

import io.smallrye.mutiny.Uni;

public interface ItemService {
    public Uni<Item> addItem(ReqItemDto reqItemDto);

    public Uni<Void> removeItem(Long id) throws ShopException;
    
    public Uni<List<Item>> getAllItem();

    public Uni<List<Item>> getItemByCategoryId(Long id);

    public Uni<Item> getItemById(Long id) throws ShopException;

    public Uni<Item> updateItem(Long id, ReqItemDto reqItemDto) throws ShopException;

    public Uni<Item> getItem(Long id);

    public Uni<Item> getItemForUpdate(Long id);

}
