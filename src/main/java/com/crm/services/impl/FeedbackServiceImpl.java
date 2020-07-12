package com.crm.services.impl;

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
  public Feedback createFeedback(Long id, String username, FeedbackRequest request) {
    Feedback feedback = new Feedback();

    Report report = reportRepository.findById(id).orElseThrow(() -> new NotFoundException("Report is not found."));
    feedback.setReport(report);
    User sender = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User is not found."));
    String role = sender.getRoles().iterator().next().getName();
    if (role.equals("ROLE_MODERATOR") || username.equals(report.getSender().getUsername())) {
      feedback.setSender(sender);
    } else {
      throw new NotFoundException("Access denied.");
    }

    String message = request.getMessage();
    feedback.setMessage(message);

    Integer satisfactionPoints = request.getSatisfactionPoints();
    if (satisfactionPoints < 0 || satisfactionPoints > 5) {
      throw new InternalException("Satisfaction Points must be greater than zero and less than five.");
    }
    feedback.setSatisfactionPoints(satisfactionPoints);

    feedbackRepository.save(feedback);
    return feedback;
  }

  @Override
  public Page<Feedback> getFeedbacksByReport(Long reportId, String username, PaginationRequest request) {
    Page<Feedback> feedbacks = null;
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new NotFoundException("Report is not found."));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Direction.DESC, "createdAt"));
    User sender = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User is not found."));
    String role = sender.getRoles().iterator().next().getName();
    if (role.equals("ROLE_MODERATOR") || role.equals("ROLE_FORWARDER")) {
      feedbacks = feedbackRepository.findByReport(report.getId(), username, pageRequest);
    }
    return feedbacks;
  }

  @Override
  public Page<Feedback> getFeedbacksByUser(String username, PaginationRequest request) {
    Page<Feedback> feedbacks = null;
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Direction.DESC, "createdAt"));
    User sender = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User is not found."));
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
  public Feedback editFeedback(Long id, String username, Map<String, Object> updates) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeFeedback(Long id, String username) {
    Feedback feedback = feedbackRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Feedback is not found."));
    if (feedback.getSender().getUsername().equals(username)) {
      feedbackRepository.deleteById(id);
    } else {
      throw new NotFoundException("Access denied.");
    }
  }

}