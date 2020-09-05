package com.crm.common;

public class Constant {
  public static final String BASE = "";

  public static final String BIDDING_NOTIFICATION = "/queue/bidding-notification";

  public static final String DRIVER_NOTIFICATION = "/queue/shipping-notification";

  public static final String SHIPPING_LINE_NOTIFICATION = "/queue/combined-notification";

  public static final String REPORT_NOTIFICATION = "/queue/report-notification";

  public static final String EVIDENCE_NOTIFICATION = "/queue/evidence-notification";

  public static final String SEARCH_REGEX = "(\\w+?)(:|!|<|>|~)(\\w+?),";

  // BID_VALIDITY_PERIOD as bid.bidValidityPeriod
  public static final long FREEZE_TIME = 5;

  public static final String EMPTY_STRING = "";

  public static final long REWIND_MONTH = 1;

  public static final long QR_CODE_EXPIRED_MINUTES = 5;

  public static final String CONTRACT_PAID_PERCENTAGE_FORMAT = "#0.00";

}
