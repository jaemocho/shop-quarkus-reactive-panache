package org.acme.shop.adapter.out.persistence.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acme.shop.common.ShopConstants.OrderState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_shop_order")
public class OrderEntity {
    
    @Id //PK
    @Column(name = "ORDER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY) //해당 데이터베이스 번호증가 전략을 따라가겠다. 
	private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity memberEntity;

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    @Builder.Default
    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.PERSIST)
    private List<OrderItemEntity> orderItemEntities = new ArrayList<OrderItemEntity>();
    
    
    public void addOrderItemEntities(OrderItemEntity orderItemEntity) {
        this.orderItemEntities.add(orderItemEntity);

        if (orderItemEntity.getOrderEntity() != this) {
            orderItemEntity.setOrderEntity(this);
        }
        
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "order_date", unique= false, nullable = false)
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", unique= false, nullable = false)
    private OrderState orderState;

    public void updateOrderStatus(OrderState orderState) {
        this.orderState = orderState;
    }

}
