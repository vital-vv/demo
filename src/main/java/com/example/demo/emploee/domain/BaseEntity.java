package com.example.demo.emploee.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  @Id
  @GeneratedValue
  protected Long id;

  @Version
  protected Integer version;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  protected Instant createdOn;

  @LastModifiedDate
  protected Instant updatedOn;
}
