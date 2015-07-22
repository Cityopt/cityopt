package eu.cityopt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.MaintenanceService;

@Service("MaintenanceService")
public class MaintenanceServiceImp implements MaintenanceService {

	@Autowired
	ExtParamValSetService extParamValSetService;
	
	@Override
	@Transactional
	public void cleanupEntities() {
		extParamValSetService.cleanupExtParamValSets();		
	}
	
}
