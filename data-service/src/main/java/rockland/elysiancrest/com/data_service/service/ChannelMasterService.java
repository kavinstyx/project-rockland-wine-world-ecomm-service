package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rockland.elysiancrest.com.data_service.dto.ChanelMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.ChannelMaster;

import java.util.List;

public interface ChannelMasterService extends CrudService<Channel, Long>{
//    ChanelMasterDTO convertToDto(ChannelMaster channelMaster);
//    ChannelMaster convertToEntity(ChanelMasterDTO dto);
   // Page<ChanelMasterDTO> findAllChannelsWithCities(Pageable pageable);
}
