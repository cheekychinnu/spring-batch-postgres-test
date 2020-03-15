package com.foo.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DatasetWatermarkDaoImpl implements DatasetWatermarkDao {

  private final SqlSession sqlSession;

  public DatasetWatermarkDaoImpl(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public void updateWatermark(String jobName, String dataset, Date watermark, Long jobExecutionId) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("jobExecutionId", jobExecutionId);
    paramMap.put("jobName", jobName);
    paramMap.put("dataset", dataset);
    paramMap.put("watermark", watermark);
    sqlSession.update("updateWatermark", paramMap);
  }

  @Override
  public Date getWatermark(String jobName, String dataset) {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("dataset", dataset);
    paramMap.put("jobName", jobName);
    return sqlSession.selectOne("getWatermark", paramMap);
  }
}
