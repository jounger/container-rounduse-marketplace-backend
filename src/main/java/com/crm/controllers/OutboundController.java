package com.crm.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/consignment")
public class OutboundController {
/*
  private static final Logger logger = LoggerFactory.getLogger(OutboundController.class);

  @Autowired
  private OutboundService outboundService;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT')")
  public ResponseEntity<?> getConsignment(@PathVariable Long id) {
    Outbound outbound = outboundService.getConsignmentById(id);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toConsignmentDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @GetMapping("/merchant/{id}")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> getConsignmentsByMerchant(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Outbound> pages = outboundService.getConsignmentsByMerchant(id, request);
    PaginationResponse<OutboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Outbound> outbounds = pages.getContent();
    List<OutboundDto> consignmentsDto = new ArrayList<>();
    outbounds.forEach(consignment -> consignmentsDto.add(OutboundMapper.toConsignmentDto(consignment)));
    response.setContents(consignmentsDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getConsignments(@Valid PaginationRequest request) {

    Page<Outbound> pages = outboundService.getConsignments(request);
    PaginationResponse<OutboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Outbound> outbounds = pages.getContent();
    List<OutboundDto> consignmentsDto = new ArrayList<>();
    outbounds.forEach(consignment -> consignmentsDto.add(OutboundMapper.toConsignmentDto(consignment)));
    response.setContents(consignmentsDto);

    return ResponseEntity.ok(response);

  }

  @PostMapping("")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> createConsignment(@Valid @RequestBody ConsignmentRequest request) {
    logger.error("Runtime error: {}", request);
    outboundService.createConsignment(request);
    return ResponseEntity.ok(new MessageResponse("Consignment created successfully"));
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> updateConsignment(@Valid @RequestBody ConsignmentRequest request) {
    Outbound outbound = outboundService.updateConsignment(request);
    OutboundDto outboundDto = OutboundMapper.toConsignmentDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> removeConsignment(@PathVariable Long id) {
    outboundService.removeConsignment(id);
    return ResponseEntity.ok(new MessageResponse("Consignment has remove successfully"));
  }
*/
}
