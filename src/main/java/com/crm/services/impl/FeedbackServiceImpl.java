package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumReportStatus;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Feedback;
import com.crm.models.Report;
import com.crm.models.User;
import com.crm.payload.request.FeedbackRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.FeedbackRepository;
import com.crm.repository.ReportRepository;
import com.crm.repository.UserRepository;
import com.crm.services.FeedbackService;
import com.crm.specification.builder.FeedbackSpecificationsBuilder;

@Service
public class FeedbackServiceImpl implements FeedbackService {

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReportRepository reportRepository;

  @Override
  public Feedback createFeedback(Long id, Long userId, FeedbackRequest request) {
    Feedback feedback = new Feedback();

    Report report = reportRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.REPORT_NOT_FOUND));

    if (report.getStatus().equals(EnumReportStatus.RESOLVED.name())
        || report.getStatus().equals(EnumReportStatus.REJECTED.name())
        || report.getStatus().equals(EnumReportStatus.CLOSED.name())) {
      throw new InternalException(ErrorConstant.FEEDBACK_INVALID_TIME);
    }

    report.setStatus(EnumReportStatus.UPDATED.name());
    feedback.setReport(report);
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    String role = sender.getRoles().iterator().next().getName();
    if (role.equals("ROLE_MODERATOR") || userId.equals(report.getSender().getId())) {
      feedback.setSender(sender);
    } else {
      throw new NotFoundException("Access denied.");
    }

    String message = request.getMessage();
    feedback.setMessage(message);

    Integer satisfactionPoints = request.getSatisfactionPoints();
    if (satisfactionPoints < 0 || satisfactionPoints > 5) {
      throw new InternalException(ErrorConstant.FEEDBACK_INVALID_SATISFACTION_POINTS);
    }
    feedback.setSatisfactionPoints(satisfactionPoints);
    feedback.setSendDate(LocalDateTime.now());

    feedbackRepository.save(feedback);
    return feedback;
  }

  @Override
  public Page<Feedback> getFeedbacksByReport(Long reportId, Long userId, PaginationRequest request) {
    Page<Feedback> feedbacks = null;

    if (!reportRepository.existsById(reportId)) {
      throw new NotFoundException(ErrorConstant.REPORT_NOT_FOUND);
    }

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Direction.ASC, "createdAt"));
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    String role = sender.getRoles().iterator().next().getName();
    if (role.equals("ROLE_FORWARDER")) {
      feedbacks = feedbackRepository.findByReport(reportId, userId, pageRequest);
    } else if (role.equals("ROLE_MODERATOR")) {
      feedbacks = feedbackRepository.findByReport(reportId, pageRequest);
    }
    return feedbacks;
  }

  @Override
  public Page<Feedback> getFeedbacksByUser(String username, PaginationRequest request) {
    Page<Feedback> feedbacks = null;
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Direction.DESC, "createdAt"));
    User sender = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    String role = sender.getRoles().iterator().next().getName();
    if (role.equals("ROLE_MODERATOR") || role.equals("ROLE_FORWARDER")) {
      feedbacks = feedbackRepository.findBySender(sender, pageRequest);
    }
    return feedbacks;
  }

  @Override
  public Page<Feedback> searchFeedbacks(PaginationRequest request, String search) {
    // Extract data from search string
    FeedbackSpecificationsBuilder builder = new FeedbackSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Feedback> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Feedback> pages = feedbackRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Feedback editFeedback(Long id, Long userId, Map<String, Object> updates) {
    Feedback feedback = feedbackRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FEEDBACK_NOT_FOUND));
    if (feedback.getSender().getId() != userId) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String message = String.valueOf(updates.get("message"));
    if (updates.get("message") != null && !Tool.isEqual(feedback.getMessage(), message)) {
      feedback.setMessage(message);
    }

    String satisfactionPoints = String.valueOf(updates.get("satisfactionPoints"));
    if (updates.get("satisfactionPoints") != null
        && !Tool.isEqual(feedback.getSatisfactionPoints(), satisfactionPoints)) {
      feedback.setSatisfactionPoints(Integer.valueOf(satisfactionPoints));
    }

    feedbackRepository.save(feedback);
    return feedback;
  }

  @Override
  public void removeFeedback(Long id, Long userId) {
    Feedback feedback = feedbackRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FEEDBACK_NOT_FOUND));
    if (feedback.getSender().getId().equals(userId)) {
      feedbackRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
  }

}
