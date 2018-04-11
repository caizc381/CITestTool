package com.mytijian.admin.shop.validator;


import com.mytijian.account.model.User;
import com.mytijian.account.service.UserService;
import com.mytijian.admin.BaseTest;
import com.mytijian.admin.shop.model.HospitalDataValidateResult;
import com.mytijian.admin.shop.model.HospitalImportData;
import com.mytijian.admin.shop.resolver.HospitalFileResolver;
import com.mytijian.resource.model.Address;
import com.mytijian.resource.service.AddressService;
import com.mytijian.resource.service.HospitalService;
import com.mytijian.site.service.SiteService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class HospitalFileDataValidatorTest  extends BaseTest {
    @InjectMocks
    @Autowired
    private HospitalImportDataValidator hospitalImportDataValidator;

    @Autowired
    private HospitalFileResolver hospitalFileResolver;



    @Mock
    private UserService userService;


    @Mock
    private SiteService siteService;


    @Mock
    private AddressService addressService;

    @Mock
    private HospitalService hospitalService;


    @Test
    public void hotest(){
        URL url = HospitalFileDataValidatorTest.class.getClassLoader().getResource("./hospital_import.xlsx");
        List<HospitalImportData> hspitalImportDatas = hospitalFileResolver.hospitalFileResolve(new File(url.getFile()));

        when(hospitalService.listHospitalIdsByBrandId(anyInt())).thenReturn(new ArrayList<>());
        when(hospitalService.getHospitalsByIds(anyList())).thenReturn(new ArrayList());
        when(userService.getUserBySystemType(anyString(),anyInt())).thenReturn(new User());
        when(siteService.checkSiteUrl(anyInt(),anyString())).thenReturn(true);
        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setProvince("安徽省");
        addresses.add(address);

        Address address1 = new Address();
        address1.setProvince("浙江省");
        addresses.add(address1);
        when(addressService.getProvince()).thenReturn(addresses);


        HospitalDataValidateResult hospitalDataValidateResult = hospitalImportDataValidator.validateHospitalFileData(hspitalImportDatas,1);


        System.out.println(hospitalDataValidateResult.isHaveError());

    }
	
}
