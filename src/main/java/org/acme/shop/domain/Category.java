package org.acme.shop.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	private Long id;

	private String name;
    
    @Builder.Default
    private List<Item> items = new ArrayList<Item>();

    public void addItem(Item item){
        this.items.add(item);

        if(item.getCategory() != this) {
            item.setCategory(this);
        }
    }
}

