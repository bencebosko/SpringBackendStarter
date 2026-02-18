package com.bbsoft.spring_backend_starter.repository;

import com.bbsoft.spring_backend_starter.repository.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {

    @Modifying
    @Query(value = "DELETE FROM mail WHERE mail_type = :mailType RETURNING *", nativeQuery = true)
    List<Mail> removeAllByMailType(String mailType);
}
