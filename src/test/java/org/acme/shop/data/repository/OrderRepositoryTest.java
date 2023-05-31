package org.acme.shop.data.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.acme.shop.common.ShopConstants.OrderState;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.acme.shop.data.entity.Member;
import org.acme.shop.data.entity.Order;
import org.acme.shop.data.entity.OrderItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;

@QuarkusTest
public class OrderRepositoryTest {
    
    @Inject
    private MemberRepository memberRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private OrderItemRepository orderItemRepository;

    @Inject
    private CategoryRepository categoryRepository;

    
    private void insertInitData(UniAsserter asserter) throws Exception {
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");

        // member 초기 data insert
        Member member = Member.builder()
                            .id("JJM")
                            .address("수원")
                            .phoneNumber("01111111111")
                            .build();

        asserter.execute(() -> memberRepository.persistAndFlush(member));
        
        Category category = Category.builder()
                                    .name("MEN")
                                    .build();
        asserter.execute(() -> categoryRepository.persistAndFlush(category));
        
        Item item1 = Item.builder()
                        .name("T-shirt")
                        .price(500)
                        .remainQty(500)
                        .build();

        Item item2 = Item.builder()
                        .name("Y-shirt")
                        .price(300)
                        .remainQty(500)
                        .build();

        asserter.assertThat(() -> categoryRepository.findByName("MEN") , 
            c -> {
                
                item1.setCategory(c.get(0));
                item2.setCategory(c.get(0));
                
            });
        
        // item 초기 data insert
        asserter.execute(() -> itemRepository.persistAndFlush(item1));
        asserter.execute(() -> itemRepository.persistAndFlush(item2));
        

        // order 초기 data insert
        Order order = Order.builder()
                        .orderDate(dtFormat.parse("20230416"))
                        .orderState(OrderState.REQUEST)
                        .build();
        
        order.setMember(member);
        // order 
        // T-shirt 3개 주문 
        // Y-shirt 2개 주문 
        asserter.assertThat(() -> itemRepository.findById(1L) , 
            i -> { 
                OrderItem orderItem = OrderItem.builder()
                .item(i)
                .count(3)
                .build();
                order.addOrderItems(orderItem); 
        });
        asserter.assertThat(() -> itemRepository.findById(2L) , 
        i -> { 
                OrderItem orderItem = OrderItem.builder()
                .item(i)
                .count(3)
                .build();
                order.addOrderItems(orderItem); 
        });                                
        
        asserter.execute(() -> orderRepository.persistAndFlush(order));
    }

    @Test
    @TestReactiveTransaction
    public void order_test(UniAsserter asserter) throws ParseException {
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");

        final Date D20230415 = dtFormat.parse("20230415");
        final Date D20230416 = dtFormat.parse("20230416");
        final Date D20230417 = dtFormat.parse("20230417");
        final Date D20230418 = dtFormat.parse("20230418");
        final Date D20230419 = dtFormat.parse("20230419");

        // order 초기 data insert
        Order order1 = Order.builder()
                        .orderDate(dtFormat.parse("20230416"))
                        .orderState(OrderState.REQUEST)
                        .build();

        Order order2 = Order.builder()
                        .orderDate(dtFormat.parse("20230417"))
                        .orderState(OrderState.REQUEST)
                        .build();

        asserter
        .execute(() -> orderRepository.persistAndFlush(order1))
        .execute(() -> orderRepository.persistAndFlush(order2));

        asserter
        // 전체 조회 테스트
        .assertThat(() -> orderRepository.findAll().list(), 
            orders -> {
                Assertions.assertEquals(2, orders.size());
            })
        // orderDate 로 조회 테스트 LessThan            
        .assertThat(() -> orderRepository.findByOrderDateLessThan(D20230418), 
            orders -> {
                Assertions.assertEquals(2, orders.size());
            })
        // orderDate 로 조회 테스트 LessThan            
        .assertThat(() -> orderRepository.findByOrderDateLessThan(D20230417), 
            orders -> {
                Assertions.assertEquals(1, orders.size());
            })
        // orderDate 로 조회 테스트 GreaterThan            
        .assertThat(() -> orderRepository.findByOrderDateGreaterThan(D20230418), 
            orders -> {
                Assertions.assertEquals(0, orders.size());
            })
        // orderDate 로 조회 테스트 GreaterThan            
        .assertThat(() -> orderRepository.findByOrderDateGreaterThan(D20230415), 
            orders -> {
                Assertions.assertEquals(2, orders.size());
            })
        // orderDate 로 조회 테스트 between       
        .assertThat(() -> orderRepository.findByOrderDateBetween(D20230415, D20230419), 
            orders -> {
                Assertions.assertEquals(2, orders.size());
            })
        // orderDate 로 조회 테스트 between       
        .assertThat(() -> orderRepository.findByOrderDateBetween(D20230415, D20230416), 
            orders -> {
                Assertions.assertEquals(1, orders.size());
            });
    }

    @Test
    @TestReactiveTransaction
    public void findByOrderId_test(UniAsserter asserter) throws Exception{
        insertInitData(asserter);

        asserter
        .assertThat(() -> orderRepository.findOrderInfoByOrderId(1L), 
            order -> {
                Assertions.assertEquals(2, order.getOrderItems().size());
            });
    }
}
