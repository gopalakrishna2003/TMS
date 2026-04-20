package com.ex.logistics.TMS.dto.Mapper;

import com.ex.logistics.TMS.dto.LoadRequest;
import com.ex.logistics.TMS.dto.LoadResponse;
import com.ex.logistics.TMS.entity.Load;
import com.ex.logistics.TMS.enums.LoadStatus;
import com.ex.logistics.TMS.enums.WeightUnit;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;

@Component
public class LoadMapper {

    public Load toEntity(LoadRequest req) {
        Load load = new Load();
        load.setShipperId(req.getShipperId());
        load.setLoadingCity(req.getLoadingCity());
        load.setUnloadingCity(req.getUnloadingCity());
        load.setLoadingDate(req.getLoadingDate());
        load.setProductType(req.getProductType());
        load.setWeight(req.getWeight());
        load.setWeightUnit(req.getWeightUnit());
        load.setTruckType(req.getTruckType());
        load.setNoOfTrucks(req.getNoOfTrucks());
        load.setStatus(LoadStatus.POSTED);
        load.setDatePosted(new Timestamp(System.currentTimeMillis()));
        return load;
    }

    public LoadResponse toResponse(Load load) {
        return LoadResponse.builder()
                .LoadId(load.getLoadId())
                .ShipperId(load.getShipperId())
                .LoadingCity(load.getLoadingCity())
                .UnloadingCity(load.getUnloadingCity())
                .LoadingDate(load.getLoadingDate())
                .ProductType(load.getProductType())
                .weight(load.getWeight())
                .weightUnit(WeightUnit.valueOf(load.getWeightUnit().name()))
                .TruckType(load.getTruckType())
                .noOfTrucks(load.getNoOfTrucks())
                .status(load.getStatus())
                .DatePosted(load.getDatePosted())
                .build();
    }
}