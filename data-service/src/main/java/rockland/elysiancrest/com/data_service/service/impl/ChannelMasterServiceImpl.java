package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Channel;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.ChanelMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.ChannelMaster;
import rockland.elysiancrest.com.data_service.repo.ChannelMasterRepo;
import rockland.elysiancrest.com.data_service.service.ChannelMasterService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChannelMasterServiceImpl extends CrudServiceImpl<ChannelMaster,Long, ChannelMasterRepo, Channel> implements ChannelMasterService {

    public ChannelMasterServiceImpl(ChannelMasterRepo repository, ModelMapper modelMapper) {
        super(repository,modelMapper);
    }

}
