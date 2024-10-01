package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.InventoryDetail;
import com.bot.coreservice.entity.User;
import com.bot.coreservice.model.FilterModel;
import com.bot.coreservice.model.UserDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {

    String addUserExcelService(MultipartFile userExcel) throws Exception;

    List<User> filterUserService(FilterModel filterModel);

    UserDetail getUserByIdService(long userId) throws Exception;

    UserDetail addNewUserService(String user, MultipartFile profile) throws Exception;

    UserDetail updateUserService(String userData, MultipartFile profileImage) throws Exception;
}
