package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

@Data
public class ItemBody {

  private String mode;
  private String raw;
  private BodyOptions options;

  @Data
  public static class BodyOptions {

    private OptionsRaw raw;
  }

  @Data
  public static class OptionsRaw {

    private String language;
  }
}
