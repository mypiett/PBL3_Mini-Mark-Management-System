package com.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBookEntryDTO {

  private Long entryId;
  private String sourceId;
  private String type;
  private String direction;

  public Long getEntryId() {
    return entryId;
  }

  public void setEntryId(Long entryId) {
    this.entryId = entryId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }
}
