package com.ex.logistics.TMS.dto.Mapper;

import com.ex.logistics.TMS.dto.TransporterReq;
import com.ex.logistics.TMS.dto.TransporterRes;
import com.ex.logistics.TMS.entity.Transporter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransportMapper {

    public Transporter toEntity(TransporterReq req) {
        Transporter t = new Transporter();
        t.setCompanyName(req.getCompanyName());
        t.setRating(req.getRating());
        t.setAvailableTrucks(new HashMap<>(req.getAvailableTrucks()));
        return t;
    }

    public TransporterRes toResponse(Transporter t) {
        return TransporterRes.builder()
                .transporterId(t.getTransporterId())
                .companyName(t.getCompanyName())
                .rating(t.getRating())
                .availableTrucks(t.getAvailableTrucks())
                .build();
    }
}
