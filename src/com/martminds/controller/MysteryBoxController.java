package com.martminds.controller;

import java.util.List;

import com.martminds.exception.InvalidOrderException;
import com.martminds.exception.OutOfStockException;
import com.martminds.model.common.Address;
import com.martminds.model.order.MysteryBoxOrder;
import com.martminds.model.product.MysteryBox;
import com.martminds.model.product.Product;
import com.martminds.model.user.User;
import com.martminds.service.MysteryBoxService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;

public class MysteryBoxController {

    public List<MysteryBox> getAllMysteryBoxes() {
        return MysteryBoxService.getInstance().getAllMysteryBoxes();
    }

    public MysteryBox getMysteryBoxById(String boxId) {
        if (!ValidationUtil.isNotEmpty(boxId)) {
            throw new IllegalArgumentException("Mystery box ID cannot be empty");
        }

        MysteryBox box = MysteryBoxService.getInstance().getMysteryBoxById(boxId);
        if (box == null) {
            throw new IllegalArgumentException("Mystery box not found: " + boxId);
        }

        return box;
    }

    public MysteryBoxOrder purchaseMysteryBox(String boxId, Address deliveryAddress)
            throws InvalidOrderException, OutOfStockException {
        Session.getInstance().requireCustomer();

        if (!ValidationUtil.isNotEmpty(boxId)) {
            throw new IllegalArgumentException("Mystery box ID cannot be empty");
        }

        if (deliveryAddress == null || !deliveryAddress.isComplete()) {
            throw new IllegalArgumentException("Delivery address is incomplete");
        }

        User currentUser = Session.getInstance().getCurrentUser();

        return MysteryBoxService.getInstance().createMysteryBoxOrder(
                currentUser.getUserId(), boxId, deliveryAddress);
    }

    public List<MysteryBoxOrder> getMyMysteryBoxOrders() {
        Session.getInstance().requireCustomer();

        User currentUser = Session.getInstance().getCurrentUser();
        return MysteryBoxService.getInstance().getMysteryBoxOrdersByCustomer(currentUser.getUserId());
    }

    public List<Product> revealMysteryBoxContents(String orderId) throws InvalidOrderException {
        Session.getInstance().requireCustomer();

        if (!ValidationUtil.isNotEmpty(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        MysteryBoxOrder order = MysteryBoxService.getInstance().findMysteryBoxOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Mystery box order not found: " + orderId);
        }

        User currentUser = Session.getInstance().getCurrentUser();
        if (!currentUser.getUserId().equals(order.getCustomerId())) {
            throw new IllegalArgumentException("You can only reveal your own mystery box orders");
        }

        return order.revealContents();
    }
}
