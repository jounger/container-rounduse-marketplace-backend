package com.crm.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumReportStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.NotFoundException;
import com.crm.payload.request.OverviewRequest;
import com.crm.payload.response.ForwarderOverviewResponse;
import com.crm.payload.response.MerchantOverviewResponse;
import com.crm.payload.response.OperatorOverviewResponse;
import com.crm.payload.response.ShippingLineOverviewResponse;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OperatorRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.ReportRepository;
import com.crm.repository.UserRepository;
import com.crm.services.OverviewService;

@Service
public class OverviewServiceImpl implements OverviewService {

  @Autowired
  InboundRepository inboundRepository;

  @Autowired
  OutboundRepository outboundRepository;

  @Autowired
  ContractRepository contractRepository;

  @Autowired
  ContainerRepository containerRepository;

  @Autowired
  BidRepository bidRepository;

  @Autowired
  BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  ReportRepository reportRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  MerchantRepository merchantRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Autowired
  OperatorRepository operatorRepository;

  @Override
  public MerchantOverviewResponse getOverviewByMerchant(String username, OverviewRequest request) {
    if (!merchantRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    MerchantOverviewResponse response = new MerchantOverviewResponse();
    LocalDateTime startDateRequest = Tool.convertToLocalDateTime(request.getStartDate());
    Date startDate = Date.from(startDateRequest.atZone(ZoneId.systemDefault()).toInstant());
    LocalDateTime endDateRequest = Tool.convertToLocalDateTime(request.getEndDate());
    Date endDate = Date.from(endDateRequest.atZone(ZoneId.systemDefault()).toInstant());

    int outboundQty = outboundRepository.countOutbounds(username, startDate, endDate);
    response.setOutboundQty(outboundQty);

    List<String> statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name());
    int biddingOutboundQty = outboundRepository.countOutbounds(username, statusList, startDate, endDate);
    response.setBiddingOutboundQty(biddingOutboundQty);

    statusList = Arrays.asList(EnumSupplyStatus.COMBINED.name());
    int combinedOutboundQty = outboundRepository.countOutbounds(username, statusList, startDate, endDate);
    response.setCombinedOutboundQty(combinedOutboundQty);

    statusList = Arrays.asList(EnumSupplyStatus.DELIVERED.name());
    int deliveredOutbountQty = outboundRepository.countOutbounds(username, statusList, startDate, endDate);
    response.setDeliveredOutbountQty(deliveredOutbountQty);

    int biddingDocumentQty = biddingDocumentRepository.countBiddingDocuments(username, startDate, endDate);
    response.setBiddingDocumentQty(biddingDocumentQty);

    statusList = Arrays.asList(EnumBiddingStatus.COMBINED.name());
    int successfulBiddingDocumentQty = biddingDocumentRepository.countBiddingDocuments(username, statusList, startDate,
        endDate);
    response.setSuccessfulBiddingDocumentQty(successfulBiddingDocumentQty);

    statusList = Arrays.asList(EnumBiddingStatus.CANCELED.name(), EnumBiddingStatus.EXPIRED.name());
    int failBiddingDocumentQty = biddingDocumentRepository.countBiddingDocuments(username, statusList, startDate,
        endDate);
    response.setFailBiddingDocumentQty(failBiddingDocumentQty);

    int contractQty = contractRepository.countContracts(username, startDate, endDate);
    response.setContractQty(contractQty);

    int paidContractQty = contractRepository.countPaidContracts(username, startDate, endDate);
    response.setPaidContractQty(paidContractQty);

    int unpaidContractQty = contractRepository.countUnpaidContracts(username, startDate, endDate);

    response.setUnpaidContractQty(unpaidContractQty);

    return response;
  }

  @Override
  public ForwarderOverviewResponse getOverviewByForwarder(String username, OverviewRequest request) {
    if (!forwarderRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    ForwarderOverviewResponse response = new ForwarderOverviewResponse();
    LocalDateTime startDateRequest = Tool.convertToLocalDateTime(request.getStartDate());
    Date startDate = Date.from(startDateRequest.atZone(ZoneId.systemDefault()).toInstant());
    LocalDateTime endDateRequest = Tool.convertToLocalDateTime(request.getEndDate());
    Date endDate = Date.from(endDateRequest.atZone(ZoneId.systemDefault()).toInstant());

    int inboundQty = inboundRepository.countInbounds(username, startDate, endDate);
    response.setInboundQty(inboundQty);

    int containerQty = containerRepository.countContainers(username, startDate, endDate);
    response.setContainerQty(containerQty);

    List<String> statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name());
    int biddingContainerQty = containerRepository.countContainers(username, statusList, startDate, endDate);
    response.setBiddingContainerQty(biddingContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.COMBINED.name());
    int combinedContainerQty = containerRepository.countContainers(username, statusList, startDate, endDate);
    response.setCombinedContainerQty(combinedContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.DELIVERED.name());
    int deliveredContainerQty = containerRepository.countContainers(username, statusList, startDate, endDate);
    response.setDeliveredContainerQty(deliveredContainerQty);

    int bidQty = bidRepository.countBids(username, startDate, endDate);
    response.setBidQty(bidQty);

    statusList = Arrays.asList(EnumBidStatus.ACCEPTED.name());
    int successfulBidQty = bidRepository.countBids(username, statusList, startDate, endDate);
    response.setSuccessfulBidQty(successfulBidQty);

    statusList = Arrays.asList(EnumBidStatus.CANCELED.name(), EnumBidStatus.EXPIRED.name(),
        EnumBidStatus.REJECTED.name());
    int failBidQty = bidRepository.countBids(username, statusList, startDate, endDate);
    response.setFailBidQty(failBidQty);

    int receivedContractQty = contractRepository.countContracts(username, startDate, endDate);
    response.setReceivedContractQty(receivedContractQty);

    int getPaidContractQty = contractRepository.countPaidContracts(username, startDate, endDate);
    response.setGetPaidContractQty(getPaidContractQty);

    int unpaidContractQty = contractRepository.countUnpaidContracts(username, startDate, endDate);

    response.setUnpaidContractQty(unpaidContractQty);

    return response;
  }

  @Override
  public OperatorOverviewResponse getOverviewByOperator(OverviewRequest request) {
    OperatorOverviewResponse response = new OperatorOverviewResponse();
    LocalDateTime startDateRequest = Tool.convertToLocalDateTime(request.getStartDate());
    Date startDate = Date.from(startDateRequest.atZone(ZoneId.systemDefault()).toInstant());
    LocalDateTime endDateRequest = Tool.convertToLocalDateTime(request.getEndDate());
    Date endDate = Date.from(endDateRequest.atZone(ZoneId.systemDefault()).toInstant());

    int outboundQty = outboundRepository.countOutbounds(startDate, endDate);
    response.setOutboundQty(outboundQty);

    List<String> statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name());
    int biddingOutboundQty = outboundRepository.countOutbounds(startDate, endDate);
    response.setBiddingOutboundQty(biddingOutboundQty);

    statusList = Arrays.asList(EnumSupplyStatus.COMBINED.name());
    int combinedOutboundQty = outboundRepository.countOutbounds(startDate, endDate);
    response.setCombinedOutboundQty(combinedOutboundQty);

    statusList = Arrays.asList(EnumSupplyStatus.DELIVERED.name());
    int deliveredOutboundQty = outboundRepository.countOutbounds(startDate, endDate);
    response.setDeliveredOutboundQty(deliveredOutboundQty);

    int inboundQty = inboundRepository.countInbounds(startDate, endDate);
    response.setInboundQty(inboundQty);

    int biddingDocumentQty = biddingDocumentRepository.countBiddingDocumentsByOperator(startDate, endDate);
    response.setBiddingDocumentQty(biddingDocumentQty);

    statusList = Arrays.asList(EnumBiddingStatus.BIDDING.name());
    int biddingBiddingDocumentQty = biddingDocumentRepository.countBiddingDocumentsByOperator(statusList, startDate,
        endDate);
    response.setBiddingBiddingDocumentQty(biddingBiddingDocumentQty);

    statusList = Arrays.asList(EnumBiddingStatus.COMBINED.name());
    int combinedBiddingDocumentQty = biddingDocumentRepository.countBiddingDocumentsByOperator(statusList, startDate,
        endDate);
    response.setCombinedBiddingDocumentQty(combinedBiddingDocumentQty);

    int bidQty = bidRepository.countBidsByOperator(startDate, endDate);
    response.setBidQty(bidQty);

    statusList = Arrays.asList(EnumBidStatus.PENDING.name());
    int pendingBidQty = bidRepository.countBidsByOperator(statusList, startDate, endDate);
    response.setPendingBidQty(pendingBidQty);

    statusList = Arrays.asList(EnumBidStatus.ACCEPTED.name());
    int acceptedBidQty = bidRepository.countBidsByOperator(statusList, startDate, endDate);
    response.setAcceptedBidQty(acceptedBidQty);

    int containerQty = containerRepository.countContainersByOperator(startDate, endDate);
    response.setContainerQty(containerQty);

    statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name(), EnumSupplyStatus.COMBINED.name(),
        EnumSupplyStatus.DELIVERED.name());
    int containerBidQty = containerRepository.countContainersByOperator(statusList, startDate, endDate);
    response.setContainerBidQty(containerBidQty);

    statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name());
    int biddingContainerQty = containerRepository.countContainersByOperator(statusList, startDate, endDate);
    response.setBiddingContainerQty(biddingContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.COMBINED.name());
    int combinedContainerQty = containerRepository.countContainersByOperator(statusList, startDate, endDate);
    response.setCombinedContainerQty(combinedContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.DELIVERED.name());
    int deliveredContainerQty = containerRepository.countContainersByOperator(statusList, startDate, endDate);
    response.setDeliveredContainerQty(deliveredContainerQty);

    int contractQty = contractRepository.countContractsByOperator(startDate, endDate);
    response.setContractQty(contractQty);

    int paidContractQty = contractRepository.countPaidContractsByOperator(startDate, endDate);
    response.setPaidContractQty(paidContractQty);

    int unpaidContractQty = contractRepository.countUnpaidContractsByOperator(startDate, endDate);
    response.setUnpaidContractQty(unpaidContractQty);

    int reportQty = (int) reportRepository.countReportByOperator(startDate, endDate);
    response.setReportQty(reportQty);

    statusList = Arrays.asList(EnumReportStatus.PENDING.name(), EnumReportStatus.UPDATED.name());
    int pendingReportQty = reportRepository.countReportByOperator(startDate, endDate, statusList);
    response.setPendingReportQty(pendingReportQty);

    statusList = Arrays.asList(EnumReportStatus.RESOLVED.name());
    int resolvedReportQty = reportRepository.countReportByOperator(startDate, endDate, statusList);
    response.setResolvedReportQty(resolvedReportQty);

    int newMemberQty = userRepository.countUserByOperator(startDate, endDate);
    response.setNewMemberQty(newMemberQty);

    int totalMember = (int) userRepository.count();
    response.setTotalMember(totalMember);

    statusList = Arrays.asList(EnumUserStatus.PENDING.name());
    int unapprovedRegistration = userRepository.countUserByOperator(startDate, endDate, statusList);
    response.setUnapprovedRegistration(unapprovedRegistration);

    return response;
  }

  @Override
  public ShippingLineOverviewResponse getOverviewByShippingLine(String username, OverviewRequest request) {
    ShippingLineOverviewResponse response = new ShippingLineOverviewResponse();
    LocalDateTime startDateRequest = Tool.convertToLocalDateTime(request.getStartDate());
    Date startDate = Date.from(startDateRequest.atZone(ZoneId.systemDefault()).toInstant());
    LocalDateTime endDateRequest = Tool.convertToLocalDateTime(request.getEndDate());
    Date endDate = Date.from(endDateRequest.atZone(ZoneId.systemDefault()).toInstant());

    int containerQty = containerRepository.countContainersByShippingLine(username, startDate, endDate);
    response.setContainerQty(containerQty);

    List<String> statusList = Arrays.asList(EnumSupplyStatus.BIDDING.name());
    int biddingContainerQty = containerRepository.countContainersByShippingLine(username, statusList, startDate,
        endDate);
    response.setBiddingContainerQty(biddingContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.COMBINED.name());
    int combinedContainerQty = containerRepository.countContainersByShippingLine(username, statusList, startDate,
        endDate);
    response.setCombinedContainerQty(combinedContainerQty);

    statusList = Arrays.asList(EnumSupplyStatus.DELIVERED.name());
    int deliveredContainerQty = containerRepository.countContainersByShippingLine(username, statusList, startDate,
        endDate);
    response.setDeliveredContainerQty(deliveredContainerQty);

    return response;
  }

}
