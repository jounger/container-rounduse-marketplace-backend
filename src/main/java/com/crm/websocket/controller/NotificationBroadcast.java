package com.crm.websocket.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.crm.common.NotificationMessage;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingNotification;
import com.crm.enums.EnumDriverNotification;
import com.crm.enums.EnumContractDocumentStatus;
import com.crm.enums.EnumNotificationType;
import com.crm.enums.EnumReportNotification;
import com.crm.enums.EnumReportStatus;
import com.crm.enums.EnumShippingLineNotification;
import com.crm.enums.EnumSupplyStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.DriverNotification;
import com.crm.models.Feedback;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Report;
import com.crm.models.ReportNotification;
import com.crm.models.ShippingInfo;
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
import com.crm.websocket.service.ContractDocumentWebSocketService;
import com.crm.websocket.service.ReportWebSocketService;
import com.crm.websocket.service.ShippingLineWebSocketService;

@Component
public class NotificationBroadcast {

  private final Logger logger = LoggerFactory.getLogger(NotificationBroadcast.class);

  @Autowired
  @Qualifier("cachedThreadPool")
  private ExecutorService executorService;

  @Autowired
  private UserService userService;

  @Autowired
  private BiddingNotificationService biddingNotificationService;

  @Autowired
  private BiddingWebSocketService biddingWebSocketService;

  @Autowired
  private ContractDocumentWebSocketService contractDocumentWebSocketService;

  @Autowired
  private ForwarderService forwarderService;

  // Notification Service

  @Autowired
  private DriverNotificationService driverNotificationService;

  @Autowired
  private ReportNotificationService reportNotificationService;

  @Autowired
  private ReportWebSocketService reportWebSocketService;

  @Autowired
  private ShippingLineNotificationService shippingLineNotificationService;

  @Autowired
  private ShippingLineWebSocketService shippingLineWebSocketService;

  @Autowired
  private DriverWebSocketService driverWebSocketService;

  public void broadcastCreateBidToMerchant(Bid bid) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bid.getBiddingDocument().getOfferee();

        // Create new message notifications and save to Database
        notifyRequest.setRecipient(offeree.getUsername());
        notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
        notifyRequest
            .setMessage(String.format(NotificationMessage.SEND_BID_TO_MERCHANT, bid.getBidder().getCompanyName()));
        notifyRequest.setAction(EnumBiddingNotification.ADDED.name());
        notifyRequest.setType(EnumNotificationType.BIDDING.name());
        BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to merchant
        logger.info("notification : {}", notification.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notification);
      }
    });
  }

  public void broadcastEditBidToMerchantOrForwarder(String status, Bid bidNew) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        BiddingNotification notification = new BiddingNotification();
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();

        // Create new message notifications and save to Database
        if (bidNew.getStatus().equals(status)) {
          notifyRequest.setRecipient(offeree.getUsername());
          notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
          notifyRequest.setMessage(String.format(NotificationMessage.SEND_BID_MODIFIED_NOTIFICATION_TO_MERCHANT,
              bidNew.getBidder().getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.MODIFIED.name());
          notifyRequest.setType(EnumNotificationType.BIDDING.name());
          notification = biddingNotificationService.createBiddingNotification(notifyRequest);

          // Send notification to merchant
          logger.info("notification : {}", notification.getId());
          biddingWebSocketService.sendBiddingNotifyToUser(notification);
        } else if (bidNew.getStatus().equals(EnumBidStatus.REJECTED.name())) {
          notifyRequest.setRecipient(bidNew.getBidder().getUsername());
          notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
          notifyRequest.setMessage(
              String.format(NotificationMessage.SEND_BID_REJECT_NOTIFICATION_TO_FORWARDER, offeree.getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.REJECTED.name());
          notifyRequest.setType(EnumNotificationType.BIDDING.name());
          notification = biddingNotificationService.createBiddingNotification(notifyRequest);

          // Send notification to forwarders
          logger.info("notification : {}", notification.getId());
          biddingWebSocketService.sendBiddingNotifyToUser(notification);
        }
      }
    });
  }

  public void broadcastRemoveBidToMerchant(Bid bid) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bid.getBiddingDocument().getOfferee();

        // Create new message notifications and save to Database
        notifyRequest.setRecipient(offeree.getUsername());
        notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format(NotificationMessage.SEND_BID_REMOVE_NOTIFICATION_TO_MERCHANT,
            bid.getBidder().getCompanyName()));
        notifyRequest.setAction(EnumBiddingNotification.REMOVED.name());
        notifyRequest.setType(EnumNotificationType.BIDDING.name());
        BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to merchant
        logger.info("notification : {}", notification.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notification);
      }
    });
  }

  public void broadcastCreateBiddingDocumentToForwarder(BiddingDocument biddingDocument) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        System.out.println(String.format("Task thread %s", Thread.currentThread().getName()));
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        PaginationRequest paging = new PaginationRequest();
        paging.setPage(0);
        paging.setLimit(100);
        Page<Forwarder> forwardersPage = forwarderService
            .findForwardersByOutbound(biddingDocument.getOutbound().getId(), paging);
        // TODO: deal with duplicate forwarder
        List<Forwarder> forwarders = forwardersPage.getContent();
        List<BiddingNotification> notifications = new ArrayList<>();

        // Create new message notifications and save to Database
        for (Forwarder f : forwarders) {
          notifyRequest.setRecipient(f.getUsername());
          notifyRequest.setRelatedResource(biddingDocument.getId());
          notifyRequest.setMessage(String.format(NotificationMessage.SEND_BIDDING_DOCUMENT_TO_FORWARDER,
              biddingDocument.getOfferee().getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.ADDED.name());
          notifyRequest.setType(EnumNotificationType.BIDDING.name());
          BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);
          notifications.add(notification);
        }
        // Asynchronous send notification to forwarders
        notifications.parallelStream().forEach(notification -> {
          logger.info("notification : {}", notification.getId());
          biddingWebSocketService.sendBiddingNotifyToUser(notification);
        });
      }
    });
  }

  public void broadcastCreateContractToDriver(Contract contract) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());
        Collection<Container> listContainerBid = bidNew.getContainers().stream()
            .filter(ex -> ex.getStatus().equals(EnumSupplyStatus.COMBINED.name())).collect(Collectors.toList());

        Collection<ShippingInfo> shippingInfos = contract.getShippingInfos();

        if (listContainerBid.size() > 0) {
          List<DriverNotification> driverNotifications = new ArrayList<>();
          shippingInfos.forEach(shippingInfo -> {
            DriverNotification driverNotification = new DriverNotification();
            DriverNotificationRequest driverNotifyRequest = new DriverNotificationRequest();
            String driverUserName = shippingInfo.getContainer().getDriver().getUsername();
            driverNotifyRequest.setRecipient(driverUserName);
            driverNotifyRequest.setRelatedResource(shippingInfo.getId());
            driverNotifyRequest.setMessage(
                String.format(NotificationMessage.SEND_TASK_NOTIFICATION_TO_DRIVER, offeree.getCompanyName(),
                    bidNew.getBidder().getCompanyName(), shippingInfo.getContainer().getNumber()));
            driverNotifyRequest.setType(EnumNotificationType.DRIVER.name());
            driverNotifyRequest.setAction(EnumDriverNotification.TASK.name());
            driverNotification = driverNotificationService.createDriverNotification(driverNotifyRequest);
            driverNotifications.add(driverNotification);
          });

          // Asynchronous send notification to Driver
          driverNotifications.parallelStream().forEach(driverNotification -> {
            driverWebSocketService.sendBiddingNotifyToDriver(driverNotification);
          });
        }
      }
    });
  }

  public void broadcastCreateContractToForwarder(Contract contract) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        BiddingNotification notification = new BiddingNotification();
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());

        notifyRequest.setRecipient(bidNew.getBidder().getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        if (contract.getRequired()) {
          notifyRequest.setMessage(
              String.format(NotificationMessage.SEND_CONTRACT_REQUIREMENT_NOTIFICATION, offeree.getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.ACCEPTED.name());
          notifyRequest.setType(EnumNotificationType.CONTRACT.name());
        } else {
          notifyRequest.setMessage(
              String.format(NotificationMessage.SEND_BID_ACCEPT_NOTIFICATION_TO_FORWARDER, offeree.getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.ACCEPTED.name());
          notifyRequest.setType(EnumNotificationType.BIDDING.name());
        }
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to Forwarder
        logger.info("notification : {}", notification.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notification);
      }
    });
  }

  public void broadcastCreateContractToShippingLine(Contract contract) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());
        Collection<Container> listContainerBid = bidNew.getContainers().stream()
            .filter(ex -> ex.getStatus().equals(EnumSupplyStatus.COMBINED.name())).collect(Collectors.toList());

        ShippingLineNotification shippingLineNotification = new ShippingLineNotification();
        ShippingLineNotificationRequest shippingLineNotificationRequest = new ShippingLineNotificationRequest();
        String numberOfContainer = String.valueOf(listContainerBid.size());
        String shippingLine = bidNew.getBiddingDocument().getOutbound().getShippingLine().getUsername();
        shippingLineNotificationRequest.setRecipient(shippingLine);
        shippingLineNotificationRequest.setRelatedResource(combined.getId());
        shippingLineNotificationRequest
            .setMessage(String.format(NotificationMessage.SEND_REQUEST_BORROW_NOTIFICATION_TO_SHIPPING_LINE,
                offeree.getCompanyName(), bidNew.getBidder().getCompanyName(), numberOfContainer));
        shippingLineNotificationRequest.setAction(EnumShippingLineNotification.REQUEST.name());
        shippingLineNotificationRequest.setType(EnumNotificationType.SHPIPPING_LINE.name());
        shippingLineNotification = shippingLineNotificationService
            .createShippingLineNotification(shippingLineNotificationRequest);

        // Send notification to ShippingLine
        shippingLineWebSocketService.sendCombinedNotifyToShippingLine(shippingLineNotification);
      }
    });
  }

  public void broadcastCreateContractDocumentToMerchant(Contract contract) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        BiddingNotification notification = new BiddingNotification();
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());

        notifyRequest.setRecipient(offeree.getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_CREATE_EVIDENCE_NOTIFICATION, bidNew.getBidder().getCompanyName()));
        notifyRequest.setAction(EnumBiddingNotification.ADDED.name());
        notifyRequest.setType(EnumNotificationType.CONTRACT.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to Merchant
        logger.info("notification : {}", notification.getId());
        contractDocumentWebSocketService.sendContractDocumentNotifyToUser(notification);
      }
    });
  }

  public void broadcastAcceptOrRejectContractDocumentToForwarder(Contract contract, String isValid) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        BiddingNotification notification = new BiddingNotification();
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());

        notifyRequest.setRecipient(bidNew.getBidder().getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        if (isValid.equals(EnumContractDocumentStatus.ACCEPTED.name())) {
          notifyRequest.setMessage(
              String.format(NotificationMessage.SEND_ACCEPT_EVIDENCE_NOTIFICATION, offeree.getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.ACCEPTED.name());
        } else if (isValid.equals(EnumContractDocumentStatus.REJECTED.name())) {
          notifyRequest.setMessage(
              String.format(NotificationMessage.SEND_REJECT_EVIDENCE_NOTIFICATION, offeree.getCompanyName()));
          notifyRequest.setAction(EnumBiddingNotification.REJECTED.name());
        }
        notifyRequest.setType(EnumNotificationType.CONTRACT.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to Forwarder
        logger.info("notification : {}", notification.getId());
        contractDocumentWebSocketService.sendContractDocumentNotifyToUser(notification);
      }
    });
  }

  public void broadcastEditContractToForwarder(Contract contract) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        Combined combined = contract.getCombined();
        Bid bidNew = combined.getBid();
        BiddingNotification notification = new BiddingNotification();
        BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
        Merchant offeree = bidNew.getBiddingDocument().getOfferee();

        notifyRequest.setRecipient(offeree.getUsername());
        notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_EDIT_CONTRACT_NOTIFICATION, bidNew.getBidder().getCompanyName()));
        notifyRequest.setAction(EnumBiddingNotification.MODIFIED.name());
        notifyRequest.setType(EnumNotificationType.CONTRACT.name());
        notification = biddingNotificationService.createBiddingNotification(notifyRequest);

        // Send notification to Forwarder
        logger.info("notification : {}", notification.getId());
        contractDocumentWebSocketService.sendContractDocumentNotifyToUser(notification);
      }
    });
  }

  public void broadcastCreateReportToModerator(Report report) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        String roleName = "ROLE_MODERATOR";

        List<User> moderators = userService.getUsersByRole(roleName);
        moderators.forEach(moderator -> {
          ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

          // Create new message notifications and save to Database
          notifyRequest.setRecipient(moderator.getUsername());
          notifyRequest.setTitle(report.getTitle());
          notifyRequest.setRelatedResource(report.getId());
          notifyRequest.setMessage(String.format(NotificationMessage.SEND_REPORT_NOTIFICATION_TO_MODERATOR,
              report.getSender().getCompanyName()));
          notifyRequest.setAction(EnumReportNotification.NEW.name());
          notifyRequest.setType(EnumNotificationType.REPORT.name());
          ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

          // Send notification to moderator
          logger.info("notification : {}", notification.getId());
          reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
        });
      }
    });
  }

  public void broadcastUpdateReportToModerator(String status, Report report) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
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
              notifyRequest.setMessage(String.format(NotificationMessage.SEND_REPORT_RESOLVED_NOTIFICATION,
                  report.getId(), report.getSender().getCompanyName()));
              notifyRequest.setAction(EnumReportNotification.RESOLVED.name());
            } else {
              notifyRequest.setMessage(String.format(NotificationMessage.SEND_REPORT_UPDATE_STRING_NOTIFICATION,
                  report.getId(), report.getSender().getCompanyName()));
              notifyRequest.setAction(EnumReportNotification.UPDATE.name());
            }
            notifyRequest.setType(EnumNotificationType.REPORT.name());
            ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

            // Send notification to moderator
            logger.info("notification : {}", notification.getId());
            reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
          });

        } else {
          ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

          // Create new message notifications and save to Database
          notifyRequest.setRecipient(report.getSender().getUsername());
          notifyRequest.setTitle(report.getTitle());
          notifyRequest.setRelatedResource(report.getId());
          notifyRequest.setMessage(String.format(NotificationMessage.SEND_REPORT_NOTIFICATION_TO_FORWARDER,
              report.getId(), report.getStatus()));
          notifyRequest.setAction(report.getStatus());
          notifyRequest.setType(EnumNotificationType.REPORT.name());
          ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

          // Send notification to forwarder
          logger.info("notification : {}", notification.getId());
          reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
        }
      }
    });
  }

  public void broadcastCreateFeedbackToUser(Feedback feedback) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

        // Create new message notifications and save to Database
        notifyRequest.setRecipient(feedback.getReport().getSender().getUsername());
        notifyRequest.setTitle(feedback.getReport().getTitle());
        notifyRequest.setRelatedResource(feedback.getReport().getId());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_FEEDBACK_NOTIFICATION, feedback.getSender().getUsername()));
        notifyRequest.setAction(EnumReportNotification.FEEDBACK.name());
        notifyRequest.setType(EnumNotificationType.REPORT.name());
        ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

        // Send notification to forwarder
        logger.info("notification : {}", notification.getId());
        reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
      }
    });
  }

  public void broadcastCreateFeedbackToModerator(String name, Feedback feedback) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        ReportNotificationRequest notifyRequest = new ReportNotificationRequest();

        // Create new message notifications and save to Database
        notifyRequest.setRecipient(name);
        notifyRequest.setTitle(feedback.getReport().getTitle());
        notifyRequest.setRelatedResource(feedback.getReport().getId());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_FEEDBACK_NOTIFICATION, feedback.getSender().getUsername()));
        notifyRequest.setAction(EnumReportNotification.FEEDBACK.name());
        notifyRequest.setType(EnumNotificationType.REPORT.name());
        ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);

        // Send notification to Moderator
        logger.info("notification : {}", notification.getId());
        reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
      }
    });
  }
}
