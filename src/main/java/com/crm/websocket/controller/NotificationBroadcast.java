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
import com.crm.enums.EnumCombinedNotification;
import com.crm.enums.EnumNotificationType;
import com.crm.enums.EnumShippingNotification;
import com.crm.enums.EnumSupplyStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.Combined;
import com.crm.models.CombinedNotification;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.ReportNotification;
import com.crm.models.ShippingInfo;
import com.crm.models.ShippingNotification;
import com.crm.models.User;
import com.crm.models.dto.BiddingNotificationDto;
import com.crm.models.dto.CombinedNotificationDto;
import com.crm.models.dto.ReportNotificationDto;
import com.crm.models.dto.ShippingNotificationDto;
import com.crm.models.mapper.BiddingNotificationMapper;
import com.crm.models.mapper.CombinedNotificationMapper;
import com.crm.models.mapper.ReportNotificationMapper;
import com.crm.models.mapper.ShippingNotificationMapper;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.CombinedNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportNotificationRequest;
import com.crm.payload.request.ShippingNotificationRequest;
import com.crm.services.BiddingNotificationService;
import com.crm.services.CombinedNotificationService;
import com.crm.services.ForwarderService;
import com.crm.services.ReportNotificationService;
import com.crm.services.ShippingNotificationService;
import com.crm.services.UserService;
import com.crm.websocket.service.BiddingWebSocketService;
import com.crm.websocket.service.CombinedWebSocketService;
import com.crm.websocket.service.ContractDocumentWebSocketService;
import com.crm.websocket.service.ReportWebSocketService;
import com.crm.websocket.service.ShippingWebSocketService;

@Component
public class NotificationBroadcast {

  private final Logger logger = LoggerFactory.getLogger(NotificationBroadcast.class);

  @Autowired
  @Qualifier("fixedThreadPool")
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
  private ShippingNotificationService shippingNotificationService;

  @Autowired
  private ReportNotificationService reportNotificationService;

  @Autowired
  private ReportWebSocketService reportWebSocketService;

  @Autowired
  private CombinedNotificationService combinedNotificationService;

  @Autowired
  private CombinedWebSocketService combinedWebSocketService;

  @Autowired
  private ShippingWebSocketService shippingWebSocketService;

  public void broadcastEditBidToMerchantOrForwarder(String status, Bid bidNew) {
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    Merchant offeree = bidNew.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database
    if (bidNew.getStatus().equals(status)) {
      notifyRequest.setRecipient(offeree.getUsername());
      notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
      notifyRequest.setMessage(String.format(NotificationMessage.SEND_BID_MODIFIED_NOTIFICATION_TO_MERCHANT,
          bidNew.getBidder().getCompanyName()));
      notifyRequest.setAction(EnumBiddingNotification.BID_EDITED.name());
    } else if (bidNew.getStatus().equals(EnumBidStatus.REJECTED.name())) {
      notifyRequest.setRecipient(bidNew.getBidder().getUsername());
      notifyRequest.setRelatedResource(bidNew.getBiddingDocument().getId());
      notifyRequest.setMessage(
          String.format(NotificationMessage.SEND_BID_REJECT_NOTIFICATION_TO_FORWARDER, offeree.getCompanyName()));
      notifyRequest.setAction(EnumBiddingNotification.BID_REJECTED.name());
    }

    notifyRequest.setType(EnumNotificationType.BIDDING.name());
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);
    BiddingNotificationDto notificationDto = BiddingNotificationMapper.toBiddingNotificationDto(notification);

    // Send notification to merchant
    logger.info("notification : {}", notification.getId());
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        biddingWebSocketService.sendBiddingNotifyToUser(notificationDto);
      }
    });
  }

  public void broadcastCreateBiddingDocumentToForwarder(BiddingDocument biddingDocument) {
    System.out.println(String.format("Task thread %s", Thread.currentThread().getName()));
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    PaginationRequest paging = new PaginationRequest();
    paging.setPage(0);
    paging.setLimit(100);
    Page<Forwarder> forwardersPage = forwarderService.findForwardersByOutbound(biddingDocument.getOutbound().getId(),
        paging);
    // TODO: deal with duplicate forwarder
    List<Forwarder> forwarders = forwardersPage.getContent();
    List<BiddingNotificationDto> notificationsDto = new ArrayList<>();

    // Create new message notifications and save to Database
    for (Forwarder f : forwarders) {
      notifyRequest.setRecipient(f.getUsername());
      notifyRequest.setRelatedResource(biddingDocument.getId());
      notifyRequest.setMessage(String.format(NotificationMessage.SEND_BIDDING_DOCUMENT_TO_FORWARDER,
          biddingDocument.getOfferee().getCompanyName()));
      notifyRequest.setAction(EnumBiddingNotification.BIDDING_INVITED.name());
      notifyRequest.setType(EnumNotificationType.BIDDING.name());
      BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);
      BiddingNotificationDto notificationDto = BiddingNotificationMapper.toBiddingNotificationDto(notification);
      notificationsDto.add(notificationDto);
    }
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        // Asynchronous send notification to forwarders
        notificationsDto.parallelStream().forEach(notificationDto -> {
          logger.info("notification : {}", notificationDto.getId());
          biddingWebSocketService.sendBiddingNotifyToUser(notificationDto);
        });
      }
    });
  }

  public void broadcastCreateContractToDriver(Contract contract) {
    Combined combined = contract.getCombined();
    Bid bidNew = combined.getBid();
    Merchant offeree = bidNew.getBiddingDocument().getOfferee();
//        List<Container> listContainerBid = containerService.getContainersByBidAndStatus(bidNew.getId(),
//            EnumSupplyStatus.COMBINED.name());
    Collection<Container> listContainerBid = bidNew.getContainers().stream()
        .filter(ex -> ex.getStatus().equals(EnumSupplyStatus.COMBINED.name())).collect(Collectors.toList());

    Collection<ShippingInfo> shippingInfos = contract.getShippingInfos();

    if (listContainerBid.size() > 0) {
      List<ShippingNotificationDto> shippingNotifications = new ArrayList<>();
      shippingInfos.forEach(shippingInfo -> {
        ShippingNotification shippingNotification = new ShippingNotification();
        ShippingNotificationRequest shippingNotifyRequest = new ShippingNotificationRequest();
        String shippingUserName = shippingInfo.getContainer().getDriver().getUsername();
        shippingNotifyRequest.setRecipient(shippingUserName);
        shippingNotifyRequest.setRelatedResource(shippingInfo.getId());
        shippingNotifyRequest.setMessage(String.format(NotificationMessage.SEND_TASK_NOTIFICATION_TO_DRIVER,
            offeree.getCompanyName(), bidNew.getBidder().getCompanyName(), shippingInfo.getContainer().getNumber()));
        shippingNotifyRequest.setType(EnumNotificationType.SHIPPING.name());
        shippingNotifyRequest.setAction(EnumShippingNotification.TASK.name());
        shippingNotification = shippingNotificationService.createShippingNotification(shippingNotifyRequest);
        ShippingNotificationDto driverNotificationDto = ShippingNotificationMapper
            .toShippingNotificationDto(shippingNotification);
        shippingNotifications.add(driverNotificationDto);
      });

      // Asynchronous send notification to Driver
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          shippingNotifications.parallelStream().forEach(shippingNotification -> {
            shippingWebSocketService.sendBiddingNotifyToDriver(shippingNotification);
          });
        }
      });
    }
  }

  public void broadcastCreateContractToShippingLine(Contract contract) {
    Combined combined = contract.getCombined();
    Bid bidNew = combined.getBid();
    Merchant offeree = bidNew.getBiddingDocument().getOfferee();

    Collection<Container> listContainerBid = bidNew.getContainers().stream()
        .filter(ex -> ex.getStatus().equals(EnumSupplyStatus.COMBINED.name())).collect(Collectors.toList());

    CombinedNotificationRequest combinedNotificationRequest = new CombinedNotificationRequest();
    String numberOfContainer = String.valueOf(listContainerBid.size());
    String shippingLine = bidNew.getBiddingDocument().getOutbound().getShippingLine().getUsername();
    combinedNotificationRequest.setRecipient(shippingLine);
    combinedNotificationRequest.setRelatedResource(combined.getId());
    combinedNotificationRequest
        .setMessage(String.format(NotificationMessage.SEND_REQUEST_BORROW_NOTIFICATION_TO_SHIPPING_LINE,
            offeree.getCompanyName(), bidNew.getBidder().getCompanyName(), numberOfContainer));
    combinedNotificationRequest.setAction(EnumCombinedNotification.REQUEST.name());
    combinedNotificationRequest.setType(EnumNotificationType.COMBINED.name());
    CombinedNotification combinedNotification = combinedNotificationService
        .createCombinedNotification(combinedNotificationRequest);
    CombinedNotificationDto combinedNotificationDto = CombinedNotificationMapper
        .toCombinedNotificationDto(combinedNotification);

    // Send notification to ShippingLine
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        combinedWebSocketService.sendCombinedNotifyToShippingLine(combinedNotificationDto);
      }
    });
  }

  public void broadcastSendBiddingNotificationToUser(BiddingNotificationRequest notifyRequest) {

    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);
    BiddingNotificationDto notificationDto = BiddingNotificationMapper.toBiddingNotificationDto(notification);

    // Send notification to Forwarder
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        logger.info("notification : {}", notificationDto.getId());
        biddingWebSocketService.sendBiddingNotifyToUser(notificationDto);
      }
    });
  }

  public void broadcastSendContractNotificationToUser(CombinedNotificationRequest notifyRequest) {

    CombinedNotification notification = combinedNotificationService.createCombinedNotification(notifyRequest);
    CombinedNotificationDto notificationDto = CombinedNotificationMapper.toCombinedNotificationDto(notification);
    // Send notification to Forwarder
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        logger.info("notification : {}", notification.getId());
        contractDocumentWebSocketService.sendContractDocumentNotifyToUser(notificationDto);
      }
    });
  }

  public void broadcastSendReportNotificationToModerator(ReportNotificationRequest notifyRequest) {
    String roleName = "ROLE_MODERATOR";

    List<User> moderators = userService.getUsersByRole(roleName);
    if (moderators.size() > 0) {
      List<ReportNotificationDto> notifications = new ArrayList<>();
      moderators.forEach(moderator -> {
        notifyRequest.setRecipient(moderator.getUsername());
        ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);
        ReportNotificationDto notificationDto = ReportNotificationMapper.toReportNotificationDto(notification);
        notifications.add(notificationDto);
      });

      // Send notification to moderator
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          notifications.parallelStream().forEach(notification -> {
            logger.info("notification : {}", notification.getId());
            reportWebSocketService.sendReportNotifyToModeratorOrUser(notification);
          });
        }
      });
    }
  }

  public void broadcastCreateReportNotificationToUser(ReportNotificationRequest notifyRequest) {

    ReportNotification notification = reportNotificationService.createReportNotification(notifyRequest);
    ReportNotificationDto notificationDto = ReportNotificationMapper.toReportNotificationDto(notification);

    // Send notification to Moderator
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        logger.info("notification : {}", notification.getId());
        reportWebSocketService.sendReportNotifyToModeratorOrUser(notificationDto);
      }
    });
  }

  public void broadcastSendCombinedNotificationToUser(CombinedNotificationRequest notifyRequest) {

    CombinedNotification notification = combinedNotificationService.createCombinedNotification(notifyRequest);
    CombinedNotificationDto notificationDto = CombinedNotificationMapper.toCombinedNotificationDto(notification);

    // Send notification to User
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        logger.info("notification : {}", notification.getId());
        combinedWebSocketService.sendCombinedNotifyToShippingLine(notificationDto);
      }
    });
  }

  public void broadcastSendCombinedNotificationToDriver(Contract contract, CombinedNotificationRequest notifyRequest) {
    Collection<ShippingInfo> shippingInfos = contract.getShippingInfos();

    if (shippingInfos.size() > 0) {
      List<CombinedNotificationDto> combinedNotificationDtos = new ArrayList<>();
      shippingInfos.forEach(shippingInfo -> {
        CombinedNotification notification = new CombinedNotification();
        String shippingUserName = shippingInfo.getContainer().getDriver().getUsername();
        notifyRequest.setRecipient(shippingUserName);
        notification = combinedNotificationService.createCombinedNotification(notifyRequest);
        CombinedNotificationDto notificationDto = CombinedNotificationMapper.toCombinedNotificationDto(notification);
        combinedNotificationDtos.add(notificationDto);
      });

      // Asynchronous send notification to Driver
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          combinedNotificationDtos.parallelStream().forEach(combinedNotificationDto -> {
            combinedWebSocketService.sendCombinedNotifyToShippingLine(combinedNotificationDto);
          });
        }
      });
    }
  }

}
