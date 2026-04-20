package com.ex.logistics.TMS.Repository;

import com.ex.logistics.TMS.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransporterRepo extends JpaRepository<Transporter, UUID> {
}
