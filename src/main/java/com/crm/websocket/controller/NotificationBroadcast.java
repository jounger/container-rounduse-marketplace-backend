package com.crm.websocket.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.crm.controllers.BiddingDocumentController;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingNotificationType;
import com.crm.enums.EnumDriverNotificationType;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.Container;
import com.crm.models.DriverNotification;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.DriverNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.BiddingNotificationService;
import com.crm.services.DriverNotificationService;
import com.crm.services.ForwarderService;
import com.crm.websocket.service.BiddingWebSocketService;

@Component
public class NotificationBroadcast {

  private static final Logger logger = LoggerFactory.getLogger(BiddingDocumentController.class);

  private static BiddingNotificationService biddingNotificationService;

  private static BiddingWebSocketService biddingWebSocketService;

  private static ForwarderService forwarderService;

  private static DriverNotificationService driverNotificationService;

  @Autowired
  public NotificationBroadcast(BiddingNotificationService biddingNotificationService,
      BiddingWebSocketService biddingWebSocketService, ForwarderService forwarderService,
      DriverNotificationService driverNotificationService) {
    NotificationBroadcast.biddingNotificationService = biddingNotificationService;
    NotificationBroadcast.biddingWebSocketService = biddingWebSocketService;
    NotificationBroadcast.forwarderService = forwarderService;
    NotificationBroadcast.driverNotificationService = driverNotificationService;
  }

  public static void broadcastCreateBidToMerchant(Bid bid) {

    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    Merchant offeree = bid.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database

    notifyRequest.setRecipient(offeree.getUsername());
    notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
    notifyRequest.setMessage(String.format("You got a new Bid from %s", bid.getBidder().getUsername()));
    notifyRequest.setType(EnumBiddingNotificationType.ADDED.name());
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to merchant

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.sendBiddingNotifyToUser(notification);
  }

  public static void broadcastEditBidToMerchantOrForwarder(String status, Bid bidNew) {

    BiddingNotification notification = new BiddingNotification();
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    Merchant offeree = bidNew.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database
    if (bidNew.getStatus().equals(status)) {

      notifyRequest.setRecipient(offeree.getUsername());
      notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
      notifyRequest.setMessage(String.format("Bid have been MODIFIED by %s", bidNew.getBidder().getUsername()));
      notifyRequest.setType(EnumBiddingNotificationType.MODIFIED.name());
      notification = biddingNotificationService.createBiddingNotification(notifyRequest);

      // Asynchronous send notification to merchant

      logger.info("notification : {}", notification.getId());
      biddingWebSocketService.sendBiddingNotifyToUser(notification);

    } else {
      if (bidNew.getStatus().equals(EnumBidStatus.ACCEPTED.name())) {

        notifyRequest.setRecipient(bidNew.getBidder().getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format("Your Bid have ACCEPTED from %s", offeree.getUsername()));
        notifyRequest.setType(EnumBiddingNotificationType.ACCEPTED.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Asynchronous send notification to forwarders

        logger.info("notification : {}", notification.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notification);

        String numberOfContainer = String.valueOf(bidNew.getContainers().size());
        String shippingLine = bidNew.getBiddingDocument().getOutbound().getShippingLine().getUsername();
        notifyRequest.setRecipient(shippingLine);
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format("%s and %s want to borrow %s container from you", offeree.getUsername(),
            bidNew.getBidder().getUsername(), numberOfContainer));
        notifyRequest.setType(EnumBiddingNotificationType.ACCEPTED.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Asynchronous send notification to ShippingLine

        biddingWebSocketService.sendBiddingNotifyToShippingLine(notification, bidNew);

        Collection<Container> collectionContainers = bidNew.getContainers();
        List<Container> containers = new ArrayList<Container>(collectionContainers);
        if (containers != null) {
          containers.forEach(container -> {
            DriverNotification driverNotification = new DriverNotification();
            DriverNotificationRequest driverNotifyRequest = new DriverNotificationRequest();
            String driverUserName = container.getDriver().getUsername();
            driverNotifyRequest.setRecipient(driverUserName);
            driverNotifyRequest.setRelatedResource(bidNew.getBiddingDocument().getOutbound().getId());
            driverNotifyRequest.setMessage(String.format("%s and %s want you driver container %s",
                offeree.getUsername(), bidNew.getBidder().getUsername(), container.getContainerNumber()));
            driverNotifyRequest.setType(EnumDriverNotificationType.TASK.name());

            driverNotification = driverNotificationService.createDriverNotification(driverNotifyRequest);

            // Asynchronous send notification to Driver

            biddingWebSocketService.sendBiddingNotifyToDriver(driverNotification);
          });
        }

      } else if (bidNew.getStatus().equals(EnumBidStatus.REJECTED.name())) {

        notifyRequest.setRecipient(bidNew.getBidder().getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format("Your Bid have REJECTED from %s", offeree.getUsername()));
        notifyRequest.setType(EnumBiddingNotificationType.REJECTED.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Asynchronous send notification to forwarders

        logger.info("notification : {}", notification.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notification);
      }
    }
  }

  public static void broadcastRemoveBidToMerchant(Bid bid) {

    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    Merchant offeree = bid.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database

    notifyRequest.setRecipient(offeree.getUsername());
    notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
    notifyRequest.setMessage(String.format("Bid have been REMOVED by %s", bid.getBidder().getUsername()));
    notifyRequest.setType(EnumBiddingNotificationType.REMOVED.name());
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to merchant

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.sendBiddingNotifyToUser(notification);
  }

  public static void broadcastCreateBiddingDocumentToForwarder(BiddingDocument biddingDocument) {

    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    PaginationRequest paging = new PaginationRequest();
    paging.setPage(0);
    paging.setLimit(100);
    Page<Forwarder> forwardersPage = forwarderService.findForwardersByOutbound(biddingDocument.getOutbound().getId(),
        paging);
    // TODO: deal with duplicate forwarder
    List<Forwarder> forwarders = forwardersPage.getContent();
    List<BiddingNotification> notifications = new ArrayList<>();

    // Create new message notifications and save to Database
    for (Forwarder f : forwarders) {
      notifyRequest.setRecipient(f.getUsername());
      notifyRequest.setRelatedResource(biddingDocument.getId());
      notifyRequest.setMessage(
          String.format("You got a new Bidding Document from %s", biddingDocument.getOfferee().getUsername()));
      notifyRequest.setType(EnumBiddingNotificationType.ADDED.name());
      BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);
      notifications.add(notification);
    }
    // Asynchronous send notification to forwarders
    notifications.parallelStream().forEach(notification -> {
      logger.info("notification : {}", notification.getId());
      biddingWebSocketService.sendBiddingNotifyToUser(notification);
    });
  }

}
