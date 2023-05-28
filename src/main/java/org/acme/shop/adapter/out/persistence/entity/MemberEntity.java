package org.acme.shop.adapter.out.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "tb_shop_member")
public class MemberEntity {
    
    @Id //PK
    @Column(name = "MEMBER_ID")
	private String id;

    @Column(unique = false, nullable = false)
    private String address;

    @Column(name = "phone_number", unique = false, nullable = false)
    private String phoneNumber;

    @Builder.Default
    @OneToMany(mappedBy = "memberEntity")
    private List<OrderEntity> orderEntities = new ArrayList<OrderEntity>();

    public void addOrderEntities(OrderEntity orderEntity) {
        this.orderEntities.add(orderEntity);

        if(orderEntity.getMemberEntity() != this) {
            orderEntity.setMemberEntity(null);
        }
    }

    public void updateMember(String address, String phoneNumber){
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

}
