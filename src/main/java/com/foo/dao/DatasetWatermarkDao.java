package com.foo.dao;

import java.util.Date;

public interface DatasetWatermarkDao {

  void updateWatermark(String jobName, String dataset, Date watermark, Long jobExecutionId);

  Date getWatermark(String jobName, String dataset);
}
