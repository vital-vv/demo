package com.example.demo.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractAuditable extends AbstractPersistable<Long> {

  @Override
  public void setId(@Nullable Long id) {
    super.setId(id);
  }

  @Version
  @Column(nullable = false)
  protected Integer version;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  protected Instant createdOn;

  @LastModifiedDate
  @Column(nullable = false)
  protected Instant updatedOn;
}
