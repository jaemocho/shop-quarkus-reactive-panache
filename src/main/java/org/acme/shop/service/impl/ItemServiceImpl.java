package org.acme.shop.service.impl;

import java.util.List;

import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.CategoryDAO;
import org.acme.shop.data.dao.ItemDAO;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.acme.shop.service.ItemService;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ItemServiceImpl implements ItemService{
    
    @Inject
    ItemDAO itemDAO;

    @Inject
    CategoryDAO categoryDAO;

    @WithTransaction
    public Uni<Item> addItem(ReqItemDto reqItemDto) {
        Item item = createNewItem(reqItemDto);
        Uni<Category> categoryUni = categoryDAO.findById(reqItemDto.getCategoryId());
        return categoryUni.onItem()
                .transformToUni( c -> { item.setCategory(c);
                                   return itemDAO.save(item);
                                });
    }

    private Item createNewItem(ReqItemDto reqItemDto) {
        Item item = Item.builder()
                                    .name(reqItemDto.getName())
                                    .price(reqItemDto.getPrice())
                                    .remainQty(reqItemDto.getRemainQty())
                                    .build();
        return item;
    }

    @WithTransaction
    public Uni<Void> removeItem(Long id) throws ShopException {
        Uni<Item> itemUni = getItem(id);
        itemUni = nullCheck(itemUni);
        return itemUni.onItem().transformToUni(i -> itemDAO.delete(i));
    }

    public Uni<Item> getItem(Long id) {
        return itemDAO.findById(id);
    }

    private Uni<Item> nullCheck(Uni<Item> itemUni) {
        return itemUni.onItem().ifNull()
                .failWith(CommonUtils.throwShopException(Item.class.getSimpleName()));
    }

    @WithTransaction
    public Uni<List<Item>> getAllItem() {
        return itemDAO.findAll();
    }

    public Uni<Item> getItemById(Long id) throws ShopException {
        Uni<Item> itemUni = getItem(id);
        return nullCheck(itemUni);
    }

    public Uni<List<Item>> getItemByCategoryId(Long id) {
        return itemDAO.findByCategoryId(id);
    }

    @WithTransaction
    public Uni<Item> updateItem(Long id, ReqItemDto reqItemDto) throws ShopException{
        Uni<Item> itemUni = getItemForUpdate(id);
        return itemUni.onItem()
            .invoke(i -> i.updateItem(reqItemDto.getName(), reqItemDto.getPrice(), reqItemDto.getRemainQty()));
    }

    public Uni<Item> getItemForUpdate(Long id) throws ShopException{
        return nullCheck(itemDAO.findByIdForUpdate(id));
    }



}
