package org.acme.shop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acme.shop.common.ShopConstants.OrderState;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.ItemDAO;
import org.acme.shop.data.dao.OrderDAO;
import org.acme.shop.data.dto.ReqOrderDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.acme.shop.data.entity.Member;
import org.acme.shop.data.entity.Order;
import org.acme.shop.data.entity.OrderItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@QuarkusTest
public class OrderServiceTest {
    
    @InjectMock
    OrderDAO orderDAO;
    
    @InjectMock
    ItemDAO itemDAO;

    @InjectMock
    MemberService memberService;

    @Inject
    ItemService itemService;

    @Inject
    private OrderService orderService;

    @Test
    @RunOnVertxContext
    public void createOrder_test(UniAsserter asserter) throws Exception{

        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        order.setMember(members.get(0));
        
        List<ReqOrderDto.RequestItem> requestItems = new ArrayList<ReqOrderDto.RequestItem>();

        requestItems.add(ReqOrderDto.RequestItem.builder()
                                        .itemId(items.get(0).getId())
                                        .requestQty(30)
                                        .build());

        requestItems.add(ReqOrderDto.RequestItem.builder()
                                        .itemId(items.get(1).getId())
                                        .requestQty(15)
                                        .build());                                      

        
        ReqOrderDto reqOrderDto = ReqOrderDto.builder()
                                        .memberId(members.get(0).getId())
                                        .requestItem(requestItems)
                                        .build();
        
        when(memberService.getMemberById(reqOrderDto.getMemberId()))
            .thenReturn(Uni.createFrom().item(members.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().item(items.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(1).getId()))
            .thenReturn(Uni.createFrom().item(items.get(1)));

        when(orderDAO.save(any(Order.class)))
            .thenReturn(Uni.createFrom().item(order));
        // when(orderItemDAO.save(any(OrderItem.class)))
        //     .thenReturn(Uni.createFrom().nullItem());
        

        // when
        asserter.assertThat(() -> orderService.createOrder(reqOrderDto)
            , o -> {
                // then 
                Assertions.assertEquals(70, items.get(0).getRemainQty());
                Assertions.assertEquals(85, items.get(1).getRemainQty());
            });
    }

    @Test   // reamin qty 보다 많이 요청했을 경우 exception 테스트
    @RunOnVertxContext
    public void createOrderNotEnough_test(UniAsserter asserter) throws Exception{

        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        order.setMember(members.get(0));
        
        List<ReqOrderDto.RequestItem> requestItems = new ArrayList<ReqOrderDto.RequestItem>();

        requestItems.add(ReqOrderDto.RequestItem.builder()
                                        .itemId(items.get(0).getId())
                                        .requestQty(101)
                                        .build());
        
        ReqOrderDto reqOrderDto = ReqOrderDto.builder()
                                        .memberId(members.get(0).getId())
                                        .requestItem(requestItems)
                                        .build();
        
        when(memberService.getMemberById(reqOrderDto.getMemberId()))
            .thenReturn(Uni.createFrom().item(members.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().item(items.get(0)));

        when(orderDAO.save(any(Order.class)))
            .thenReturn(Uni.createFrom().item(order));
        //when(orderItemDAO.save(any(OrderItem.class))).thenReturn(null);
        
        // when then
        asserter.assertFailedWith(()->orderService.createOrder(reqOrderDto), 
            ShopException.class);
    }

    @Test   // member 정보가 잘못된 경우 exception 발생 테스트
    @RunOnVertxContext
    public void createOrderNotFoundMember_test(UniAsserter asserter) throws Exception{

        //given
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        
        List<ReqOrderDto.RequestItem> requestItems = new ArrayList<ReqOrderDto.RequestItem>();

        requestItems.add(ReqOrderDto.RequestItem.builder()
                                        .itemId(items.get(0).getId())
                                        .requestQty(101)
                                        .build());
        
        ReqOrderDto reqOrderDto = ReqOrderDto.builder()
                                        .requestItem(requestItems)
                                        .build();
        
        when(memberService.getMemberById(reqOrderDto.getMemberId()))
            .thenReturn(Uni.createFrom().nullItem());        
        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().item(items.get(0)));
    
        when(orderDAO.save(any(Order.class)))
            .thenReturn(Uni.createFrom().item(order));
        //when(orderItemDAO.save(any(OrderItem.class))).thenReturn(null);

        // when then
        asserter.assertFailedWith(()->orderService.createOrder(reqOrderDto), 
            ShopException.class);
    }

    @Test   // item 정보가 없는 경우 테스트 
    @RunOnVertxContext
    public void createOrderNotFoundItem_test(UniAsserter asserter) throws Exception{

        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        order.setMember(members.get(0));
        
        List<ReqOrderDto.RequestItem> requestItems = new ArrayList<ReqOrderDto.RequestItem>();

        requestItems.add(ReqOrderDto.RequestItem.builder()
                                        .itemId(items.get(0).getId())
                                        .requestQty(101)
                                        .build());
        
        ReqOrderDto reqOrderDto = ReqOrderDto.builder()
                                        .memberId(members.get(0).getId())
                                        .requestItem(requestItems)
                                        .build();
        
        when(memberService.getMemberById(reqOrderDto.getMemberId()))
            .thenReturn(Uni.createFrom().item(members.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().nullItem());
        // doThrow(ShopException.class).when(commonUtils).nullCheckAndThrowException(null, Item.class.getName());
        
        when(orderDAO.save(any(Order.class)))
            .thenReturn(Uni.createFrom().item(order));
       //when(orderItemDAO.save(any(OrderItem.class))).thenReturn(null);
        
        // when then
        asserter.assertFailedWith(()->orderService.createOrder(reqOrderDto), 
            ShopException.class);
    }

    @Test
    @RunOnVertxContext
    public void getOrderInfoByOrderId_test(UniAsserter asserter) throws Exception {
        
        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        List<Category> categorys = initCategoryData();

        // order 정보 생성
        Order order = Order.builder()
                        .id(0L)
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        // order에 member mapping
        order.setMember(members.get(0));

        // item1, itme2 를 category1 에 mapping
        categorys.get(0).addItem(items.get(0));
        categorys.get(0).addItem(items.get(1));

        // orderitems 생성 및 order에 mapping item1, item2 주문 
        OrderItem orderItem1 = OrderItem.builder()
                                .item(items.get(0))
                                .count(30)
                                .build();

        OrderItem orderItem2 = OrderItem.builder()
                                .item(items.get(1))
                                .count(20)
                                .build();

        order.addOrderItems(orderItem1);
        order.addOrderItems(orderItem2);
        
        // order 정보 조회 시 order 반환
        when(orderDAO.findOrderInfoByOrderId(0L))
            .thenReturn(Uni.createFrom().item(order));
        
        // when
        asserter.assertThat(() -> orderService.getOrderInfoByOrderId(0L)
            , o -> {
                // then 
                Assertions.assertEquals(members.get(0).getId(), o.getMember().getId());
                Assertions.assertEquals(OrderState.REQUEST, o.getOrderState());
                
                // 요청 item 개수 확인
                Assertions.assertEquals(2, o.getOrderItems().size());
                // 요청 item id 및 요청 개수 확인
                Assertions.assertEquals(items.get(0).getId(), o.getOrderItems().get(0).getItem().getId());
                Assertions.assertEquals(30, o.getOrderItems().get(0).getCount());

                // 요청 item id 및 요청 개수 확인
                Assertions.assertEquals(items.get(1).getId(), o.getOrderItems().get(1).getItem().getId());
                Assertions.assertEquals(20, o.getOrderItems().get(1).getCount());
            });
    }

    @Test
    @RunOnVertxContext
    public void cancelOrder_test(UniAsserter asserter) throws ShopException {
        
        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.REQUEST)
                        .build();
        order.setMember(members.get(0));
        
        // orderitems 생성 및 order에 mapping item1, item2 주문 
        OrderItem orderItem1 = OrderItem.builder()
                                .item(items.get(0))
                                .count(30)
                                .build();

        OrderItem orderItem2 = OrderItem.builder()
                                .item(items.get(1))
                                .count(20)
                                .build();

        order.addOrderItems(orderItem1);
        order.addOrderItems(orderItem2);

        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().item(items.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(1).getId()))
            .thenReturn(Uni.createFrom().item(items.get(1)));

        when(orderDAO.findOrderInfoByOrderId(0L))
            .thenReturn(Uni.createFrom().item(order));
        
        // when
        asserter.assertThat(() -> orderService.cancelOrder(0L)
            ,o -> {
                // 취소 확인
                Assertions.assertEquals(OrderState.CANCEL, order.getOrderState());
                
                // then 취소 수량 update 확인
                Assertions.assertEquals(130, items.get(0).getRemainQty());
                Assertions.assertEquals(120, items.get(1).getRemainQty());
            });
    }

    @Test
    @RunOnVertxContext
    public void cancelOrderException_test(UniAsserter asserter) throws ShopException {
        
        //given
        List<Member> members = initMemberData();
        List<Item> items = initItemData();
        // /List<Category> categorys = initCategoryData();

        Order order = Order.builder()
                        .orderDate(new Date())
                        .orderState(OrderState.COMPLETE)
                        .build();
        order.setMember(members.get(0));
        
        // orderitems 생성 및 order에 mapping item1, item2 주문 
        OrderItem orderItem1 = OrderItem.builder()
                                .item(items.get(0))
                                .count(30)
                                .build();

        OrderItem orderItem2 = OrderItem.builder()
                                .item(items.get(1))
                                .count(20)
                                .build();

        order.addOrderItems(orderItem1);
        order.addOrderItems(orderItem2);

        when(itemDAO.findByIdForUpdate(items.get(0).getId()))
            .thenReturn(Uni.createFrom().item(items.get(0)));
        when(itemDAO.findByIdForUpdate(items.get(1).getId()))
        .thenReturn(Uni.createFrom().item(items.get(1)));

        when(orderDAO.findById(0L))
            .thenReturn(Uni.createFrom().item(order));
        
        // when then OrderState가 REQUEST가 아닐 경우 exception 발생 테스트 
        asserter.assertFailedWith(()->orderService.cancelOrder(0L)
            , ShopException.class);
    }    


    private List<Category> initCategoryData() {

        List<Category> categorys = new ArrayList<Category>();
        Category category1 = Category.builder().id(1L).name("WOMEN").build();
        Category category2 = Category.builder().id(2L).name("MEN").build();
        Category category3 = Category.builder().id(3L).name("KIDS").build();
        categorys.add(category1);
        categorys.add(category2);
        categorys.add(category3);

        return categorys;
    }

    private List<Member> initMemberData() {

        List<Member> members = new ArrayList<Member>();
        Member member1 = Member.builder().id("member1").address("수원").phoneNumber("0101112222").build();
        Member member2 = Member.builder().id("member2").address("서울").phoneNumber("0101113333").build();        
        Member member3 = Member.builder().id("member3").address("대전").phoneNumber("0101114444").build();                                
        members.add(member1);
        members.add(member2);
        members.add(member3);
        return members;
    }


    private List<Item> initItemData() {

        List<Item> items = new ArrayList<Item>();
        Item item1 = Item.builder().id(1L).name("T-shirt").price(5000).remainQty(100).build();
        Item item2 = Item.builder().id(2L).name("T-shirt").price(4500).remainQty(100).build();
        Item item3 = Item.builder().id(3L).name("T-shirt").price(50000).remainQty(100).build();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        return items;
    }
}
