package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wildeslam.
 * @create 2024/6/6 17:04
 */
@Data
public class FSGetPathInfoResponseType {
  private List<FSPathInfoDto> pathInfo;


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FSPathInfoDto {
    private String id;
    private String name;
  }
}
