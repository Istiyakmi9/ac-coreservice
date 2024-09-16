package com.bot.coreservice.services;

import com.bot.coreservice.Repository.UserAccountRepository;
import com.bot.coreservice.Repository.UserFileRepository;
import com.bot.coreservice.Repository.UserRepository;
import com.bot.coreservice.contracts.IUserService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.User;
import com.bot.coreservice.entity.UserFile;
import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.FilterModel;
import com.bot.coreservice.model.UserDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    FileManager fileManager;
    @Autowired
    UserFileRepository userFileRepository;

    public String addUserExcelService(MultipartFile userExcel) throws Exception {
        try {
            List<User> users = readAndMapUserExcel(userExcel);
            if (!users.isEmpty()) {
                userRepository.saveAll(users);
            }

            return "user inserted successfully";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public List<User> filterUserService(FilterModel filterModel) {
        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_searchString", filterModel.getSearchString(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageIndex", filterModel.getPageIndex(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_pageSize", filterModel.getPageSize(), Types.VARCHAR));
        dbParameters.add(new DbParameters("_sortBy", filterModel.getSortBy(), Types.VARCHAR));

        var dataSet = lowLevelExecution.executeProcedure("sp_user_by_filter", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<User>>() {
        });
    }

    public UserDetail getUserByIdService(long userId) throws Exception {
        if (userId == 0)
            throw new Exception("Invalid user Id");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_userId", userId, Types.BIGINT));

        var dataSet = lowLevelExecution.executeProcedure("sp_user_get_by_id", dbParameters);
        var result = objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<UserDetail>>() {
        });

        if (result == null || result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    public String addNewUserService(String userData, MultipartFile profileImage) throws Exception {
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        User user = objectMapper.readValue(userData, User.class);
        user.setAccountId("6789123542");
        user.setCreatedBy(1L);
        user.setUpdatedBy(1L);
        user.setCreatedOn(currentDate);
        user.setUpdatedOn(currentDate);

        var newUser = userRepository.save(user);

        saveUserProfile(profileImage, newUser);

        return  "User added successfully";
    }

    public String updateUserService(String userData, MultipartFile profileImage) throws Exception {
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        User user = objectMapper.readValue(userData, User.class);
        if (user.getUserId() == 0 || user.getUserId() == null)
            throw new Exception("Invalid user");

        var existingUserData = userRepository.findById(user.getUserId());

        var existingUser = getUpdateUserDetail(existingUserData, user, currentDate);

        userRepository.save(existingUser);

        saveUserProfile(profileImage, existingUser);

        return  "User updated successfully";
    }

    private void saveUserProfile(MultipartFile profileImage, User user) throws Exception {
        if (profileImage != null) {
            UserFile userFile = new UserFile();

            userFile.setFilePath(UploadNewFile(profileImage, user.getUserId(), "profile_", "profile"));
            userFile.setUserId(user.getUserId());

            userFileRepository.save(userFile);
        }
    }

    @NotNull
    private static User getUpdateUserDetail(Optional<User> existingUserData, User user, Timestamp currentDate) throws Exception {
        if (existingUserData.isEmpty())
            throw new Exception("User detail not found");

        var existingUser = existingUserData.get();

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMobileNumber(user.getMobileNumber());
        existingUser.setAlternateNumber(user.getAlternateNumber());
        existingUser.setEmailId(user.getEmailId());
        existingUser.setDob(user.getDob());
        existingUser.setAddress(user.getAddress());
        existingUser.setUpdatedBy(1L);
        existingUser.setUpdatedOn(currentDate);
        return existingUser;
    }

    private String UploadNewFile(MultipartFile newFile, long userId, String folderName, String fileType) throws Exception {
        if (newFile == null) return "";
        String filepath = "";
        try {
            filepath = fileManager.uploadFile(newFile, userId, folderName + new Date().getTime(), fileType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return filepath;
    }

    private List<User> readAndMapUserExcel(MultipartFile userExcel) throws Exception {
        List<User> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(userExcel.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnMap = new HashMap<>();

            extractedHeader(sheet, columnMap);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                users.add(mappedUserData(columnMap, row));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return users;
    }

    private User mappedUserData(Map<String, Integer> columnMap, Row row) {
        User user = new User();
        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        if (columnMap.containsKey("firstname")) {
            user.setFirstName(row.getCell(columnMap.get("firstname")).getStringCellValue());
        }

        if (columnMap.containsKey("lastname")) {
            user.setLastName(row.getCell(columnMap.get("lastname")).getStringCellValue());
        }

        if (columnMap.containsKey("address")) {
            user.setAddress(row.getCell(columnMap.get("address")).getStringCellValue());
        }

        if (columnMap.containsKey("emailid")) {
            user.setEmailId(row.getCell(columnMap.get("emailid")).getStringCellValue());
        }

        if (columnMap.containsKey("referenceid")) {
            user.setReferenceId((long) row.getCell(columnMap.get("referenceid")).getNumericCellValue());
        }
        if (columnMap.containsKey("dob")) {
            user.setDob(row.getCell(columnMap.get("dob")).getDateCellValue());
        }

        if (columnMap.containsKey("mobilenumber")) {
            Cell cell = row.getCell(columnMap.get("mobilenumber"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String mobileNumber = String.valueOf(cell.getNumericCellValue());

                mobileNumber = String.format("%.0f", cell.getNumericCellValue());
                user.setMobileNumber(mobileNumber);
            } else  {
                user.setMobileNumber(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("alternatenumber")) {
            Cell cell = row.getCell(columnMap.get("alternatenumber"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String alternateNumber = String.valueOf(cell.getNumericCellValue());

                alternateNumber = String.format("%.0f", cell.getNumericCellValue());
                user.setAlternateNumber(alternateNumber);
            } else  {
                user.setAlternateNumber(cell.getStringCellValue());
            }
        }

        if (columnMap.containsKey("accountid")) {
            Cell cell = row.getCell(columnMap.get("accountid"));
            if (cell.getCellType() == CellType.NUMERIC) {
                String accountId = String.valueOf(cell.getNumericCellValue());

                accountId = String.format("%.0f", cell.getNumericCellValue());
                user.setAccountId(accountId);
            } else if (cell.getCellType() == CellType.STRING) {
                user.setAccountId(cell.getStringCellValue());
            }
        }

        user.setCreatedBy(1L);
        user.setUpdatedBy(1L);
        user.setCreatedOn(currentDate);
        user.setUpdatedOn(currentDate);

        return user;
    }

    private void extractedHeader(Sheet sheet, Map<String, Integer> columnMap) {
        Row headerRow = sheet.getRow(0);
        for (int cellIndex = 0; cellIndex < headerRow.getPhysicalNumberOfCells(); cellIndex++) {
            String header = headerRow.getCell(cellIndex).getStringCellValue().toLowerCase();
            columnMap.put(header, cellIndex);
        }
    }
}