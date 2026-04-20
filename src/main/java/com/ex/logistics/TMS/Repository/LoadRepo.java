package com.ex.logistics.TMS.Repository;

import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.enums.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface LoadRepo extends JpaRepository<Load, UUID> {
    Page<Load> findByShipperId(String ShipperId, Pageable pageable);

    Page<Load> findByStatus(LoadStatus status, Pageable pageable);

    Page<Load> findByShipperIdAndStatus(String ShipperId,
                                        LoadStatus status, Pageable pageable);
}
