package com.crm.services.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Icd;
import com.crm.models.ShippingLine;
import com.crm.payload.request.IcdRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.IcdRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.IcdService;

@Service
public class IcdServiceImpl implements IcdService{
	
	@Autowired
	private IcdRepository icdRepository;
	
	@Autowired
    private ShippingLineRepository shippingLineRepository;

	@Override
	public void saveIcd(IcdRequest request) {
		Icd icd = new Icd();
		icd.setFullname(request.getFullname());
		String nameCode = request.getNameCode();
		if(icdRepository.existsByNameCode(nameCode)) {
			throw new DuplicateRecordException("ICD name code already existed.");
		}
		icd.setNameCode(nameCode);
		icd.setAddress(request.getAddress());
		
		Collection<String> shippingLinesString = request.getShippingLines();
		Collection<ShippingLine> listShippingLines = new ArrayList<ShippingLine>();
		shippingLinesString.forEach(shippingLines -> {
		  ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLines)
	          .orElseThrow(() -> new NotFoundException("Error: ShippingLine is not found"));
		  listShippingLines.add(shippingLine);
	    });
		icd.setShippingLines(listShippingLines);
	    
		icdRepository.save(icd);
	}

  @Override
  public void editIcd(IcdRequest request) {
//    Icd icd = icdRepository.findById(id)
    
  }

  @Override
  public void deleteIcd(Long id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Page<Icd> getIcds(PaginationRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Icd getIcdById(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<Icd> getIcdsByMerchant(Long id, PaginationRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
