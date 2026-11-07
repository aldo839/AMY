package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CollectorResponseDTO;
import com.techdrawer.amy.model.Collector;
import org.springframework.stereotype.Service;

@Service
public class CollectorMapperService {

    public CollectorResponseDTO mapToResponseDTO(Collector collector) {

        return new CollectorResponseDTO(

                collector.getUser().getName(),
                collector.getCollectorType(),
                collector.getInterest(),
                collector.getPreferredStyle(),
                collector.getPreferredArtists(),
                collector.getShippingAddresses(),
                collector.getProfilePictureUrl()
        );
    }
}
