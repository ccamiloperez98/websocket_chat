package com.example.mychat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Table(name = "TBL_MESSAGE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "FK_USER_FROM", referencedColumnName = "id", updatable = false)
    private UserEntity userFrom;

    @ManyToOne
    @JoinColumn(name = "FK_USER_TO", referencedColumnName = "id", updatable = false)
    private UserEntity userTo;

    @Column(name = "MESSAGE", nullable = false)
    private String email;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
