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
import com.crm.enums.EnumReportNotificationType;
import com.crm.enums.EnumReportStatus;
import com.crm.enums.EnumShippingLineNotificationType;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.DriverNotification;
import com.crm.models.Feedback;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Report;
import com.crm.models.ReportNotification;
import com.crm.models.ShippingLineNotification;
import com.crm.models.User;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.DriverNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportNotificationRequest;
import com.crm.payload.request.ShippingLineNotificationRequest;
import com.crm.services.BiddingNotificationService;
import com.crm.services.DriverNotificationService;
import com.crm.services.ForwarderService;
import com.crm.services.ReportNotificationService;
import com.crm.services.ShippingLineNotificationService;
import com.crm.services.UserService;
import com.crm.websocket.service.BiddingWebSocketService;
import com.crm.websocket.service.DriverWebSocketService;
import com.crm.websocket.service.ReportWebSocketService;
import com.crm.websocket.service.ShippingLineWebSocketService;

@Component
public class NotificationBroadcast {

  private static final Logger logger = LoggerFactory.getLogger(BiddingDocumentController.class);

  private static BiddingNotificationService biddingNotificationService;

  private static BiddingWebSocketService biddingWebSocketService;

  private static ForwarderService forwarderService;

  private static DriverNotificationService driverNotificationService;

  private static ReportNotificationService reportNotificationService;

  private static UserService userService;

  private static ReportWebSocketService reportWebSocketService;

  private static ShippingLineNotificationService shippingLineNotificationService;

  private static ShippingLineWebSocketService shippingLineWebSocketService;

  private static DriverWebSocketService driverWebSocketService;

  @Autowired
  public NotificationBroadcast(BiddingNotificationService biddingNotificationService,
      BiddingWebSocketService biddingWebSocketService, ForwarderService forwarderService,
      DriverNotificationService driverNotificationService, ReportNotificationService reportNotificationService,
      UserService userService, ReportWebSocketService reportWebSocketService,
      ShippingLineNotificationService shippingLineNotificationService,
      ShippingLineWebSocketService shippingLineWebSocketService, DriverWebSocketService driverWebSocketService) {
    NotificationBroadcast.biddingNotificationService = biddingNotificationService;
    NotificationBroadcast.biddingWebSocketService = biddingWebSocketService;
    NotificationBroadcast.forwarderService = forwarderService;
    NotificationBroadcast.driverNotificationService = driverNotificationService;
    NotificationBroadcast.reportNotificationService = reportNotificationService;
    NotificationBroadcast.userService = userService;
    NotificationBroadcast.reportWebSocketService = reportWebSocketService;
    NotificationBroadcast.shippingLineNotificationService = shippingLineNotificationService;
    NotificationBroadcast.shippingLineWebSocketService = shippingLineWebSocketService;
    NotificationBroadcast.driverWebSocketService = driverWebSocketService;
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

  public static void broadcastCreateCombinedToDriver(Combined combined) {
    Bid bidNew = combined.getBid();
    BiddingNotification notification = new BiddingNotification();
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    Merchant offeree = bidNew.getBiddingDocument().getOfferee();

    notifyRequest.setRecipient(bidNew.getBidder().getUsername());
    notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
    notifyRequest.setMessage(String.format("Your Bid have ACCEPTED from %s", offeree.getUsername()));
    notifyRequest.setType(EnumBiddingNotificationType.ACCEPTED.name());
    notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to forwarders

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.sendBiddingNotifyToUser(notification);

    ShippingLineNotification shippingLineNotification = new ShippingLineNotification();
    ShippingLineNotificationRequest shippingLineNotificationRequest = new ShippingLineNotificationRequest();
    String numberOfContainer = String.valueOf(bidNew.getContainers().size());
    String shippingLine = bidNew.getBiddingDocument().getOutbound().getShippingLine().getUsername();
    shippingLineNotificationRequest.setRecipient(shippingLine);
    shippingLineNotificationRequest.setRelatedResource(combined.getId());
    shippingLineNotificationRequest.setMessage(String.format("%s and %s want to borrow %s container from you",
        offeree.getUsername(), bidNew.getBidder().getUsername(), numberOfContainer));
    shippingLineNotificationRequest.setType(EnumShippingLineNotificationType.REQUEST.name());
    shippingLineNotification = shippingLineNotificationService
        .createShippingLineNotification(shippingLineNotificationRequest);

    // Asynchronous send notification to ShippingLine

    shippingLineWebSocketService.sendCombinedNotifyToShippingLine(shippingLineNotification);

    Collection<Container> collectionContainers = bidNew.getContainers();
    List<Container> containers = new ArrayList<Container>(collectionContainers);
    if (containers != null) {
      containers.forEach(container -> {
        DriverNotification driverNotification = new DriverNotification();
        DriverNotificationRequest driverNotifyRequest = new DriverNotificationRequest();
        String driverUserName = container.getDriver().getUsername();
        driverNotifyRequest.setRecipient(driverUserName);
        driverNotifyRequest.setRelatedResource(bidNew.getBiddingDocument().getOutbound().getId());
        driverNotifyRequest.setMessage(String.format("%s and %s want you driver container %s", offeree.getUsername(),
            bidNew.getBidder().getUsername(), container.getContainerNumber()));
        driverNotifyRequest.setType(EnumDriverNotificationType.TASK.name());

        driverNotification = driverNotificationService.createDriverNotification(driverNotifyRequest);

        // Asynchronous send notification to Driver

        driverWebSocketService.sendBiddingNotifyToDriver(driverNotification);
      });
    }
  }

  public static void broadcastCreateReportToModerator(Report report) {

    String roleName = "ROLE_MODERATOR";

    List<User> moderators = userService.getUsersByRole(roleName);
    moderators.forEach(moderator -> {
      ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

      // Create new message notifications and save to Database

      notifyRequest.setRecipient(moderator.getUsername());
      notifyRequest.setTitle(report.getTitle());
      notifyRequest.setRelatedResource(report.getId());
      notifyRequest.setMessage(String.format("You got a new Report from %s", report.getSender().getUsername()));
      notifyRequest.setType(EnumReportNotificationType.NEW.name());
      ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

      // Asynchronous send notification to moderator

      logger.info("notification : {}", notification.getId());
      reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
    });

  }

  public static void broadcastUpdateReportToModerator(String status, Report report) {

    String roleName = "ROLE_MODERATOR";

    if (status.equals(report.getStatus()) || report.getStatus().equals(EnumReportStatus.RESOLVED.name())) {

      List<User> moderators = userService.getUsersByRole(roleName);
      moderators.forEach(moderator -> {
        ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

        // Create new message notifications and save to Database

        notifyRequest.setRecipient(moderator.getUsername());
        notifyRequest.setTitle(report.getTitle());
        notifyRequest.setRelatedResource(report.getId());
        if (report.getStatus().equals(EnumReportStatus.RESOLVED.name())) {
          notifyRequest.setMessage(
              String.format("Report %s has been RESOLVED by %s", report.getId(), report.getSender().getUsername()));
          notifyRequest.setType(EnumReportNotificationType.RESOLVED.name());
        } else {
          notifyRequest.setMessage(
              String.format("Report %s has been Updated by %s", report.getId(), report.getSender().getUsername()));
          notifyRequest.setType(EnumReportNotificationType.UPDATE.name());
        }
        ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

        // Asynchronous send notification to moderator

        logger.info("notification : {}", notification.getId());
        reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
      });

    } else {

      ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

      // Create new message notifications and save to Database

      notifyRequest.setRecipient(report.getSender().getUsername());
      notifyRequest.setTitle(report.getTitle());
      notifyRequest.setRelatedResource(report.getId());
      notifyRequest.setMessage(String.format("Report %s has been %s", report.getId(), report.getStatus()));
      notifyRequest.setType(report.getStatus());
      ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

      // Asynchronous send notification to forwarder

      logger.info("notification : {}", notification.getId());
      reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);

    }

  }

  public static void broadcastCreateFeedbackToUser(Feedback feedback) {

    ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

    // Create new message notifications and save to Database

    notifyRequest.setRecipient(feedback.getReport().getSender().getUsername());
    notifyRequest.setTitle(feedback.getReport().getTitle());
    notifyRequest.setRelatedResource(feedback.getReport().getId());
    notifyRequest.setMessage(String.format("You got a new Feedback from %s", feedback.getSender().getUsername()));
    notifyRequest.setType(EnumReportNotificationType.FEEDBACK.name());
    ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

    // Asynchronous send notification to forwarder

    logger.info("notification : {}", notification.getId());
    reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);

  }

  public static void broadcastCreateFeedbackToModerator(String name, Feedback feedback) {

    ReportNotificationRequest notifyRequest = new ReportNotificationRequest();
    // Create new message notifications and save to Database

    notifyRequest.setRecipient(name);
    notifyRequest.setTitle(feedback.getReport().getTitle());
    notifyRequest.setRelatedResource(feedback.getReport().getId());
    notifyRequest.setMessage(String.format("You got a new Feedback from %s", feedback.getSender().getUsername()));
    notifyRequest.setType(EnumReportNotificationType.FEEDBACK.name());
    ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

    // Asynchronous send notification to Moderator

    logger.info("notification : {}", notification.getId());
    reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);

  }
}
