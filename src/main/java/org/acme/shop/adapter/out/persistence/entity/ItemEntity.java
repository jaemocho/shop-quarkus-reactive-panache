package org.acme.shop.adapter.out.persistence.entity;


import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.exception.ShopException;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_shop_item")
public class ItemEntity{
    
    @Id //PK
    @Column(name = "ITEM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY) //해당 데이터베이스 번호증가 전략을 따라가겠다. 
	private Long id;

    @Column(unique= false, nullable = false)
    private String name;
    
    @Column(unique= false, nullable = false)

    private int price;
    @Column(name = "remain_qty",  unique= false, nullable = false)
    private int remainQty;

    @ManyToOne //(fetch = FetchType.LAZY) 
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity categoryEntity;

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public void updateItem(String name, int price, int remainQty){
        this.name = name;
        this.price = price;
        this.remainQty = remainQty;
    }

    public void addRemainQty(int requestQty) {
        this.remainQty += requestQty;
    }

    public void removeRemainQty(int requestQty) throws ShopException {
        if ( requestQty > this.remainQty ) {
            throw new ShopException(ExceptionClass.SHOP, HttpResponseStatus.BAD_REQUEST, "not enough remainQty");
        }
        this.remainQty -= requestQty;
    }

}
