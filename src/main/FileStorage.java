package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

class StoragePaths {
    static final String STORAGE_DIRECTORY = "account_info";
    static final String DEFAULT_ACCOUNTS_FILE = STORAGE_DIRECTORY + File.separator + "accounts.json";
    static final String DEFAULT_TIME_FILE = STORAGE_DIRECTORY + File.separator + "system_time.json";

    static String normalizeDataFilePath(String filePath) {
        File file = new File(filePath);
        if (file.isAbsolute() || file.getParent() != null) {
            return filePath;
        }
        return STORAGE_DIRECTORY + File.separator + file.getName();
    }

    static String buildAdminFilePath(String accountFilePath) {
        File accountFile = new File(accountFilePath);
        String accountFileName = accountFile.getName();
        File parentDirectory = accountFile.getAbsoluteFile().getParentFile();
        if (parentDirectory == null) {
            return "admin_" + accountFileName;
        }
        return new File(parentDirectory, "admin_" + accountFileName).getPath();
    }

    static String buildTimeFilePath(String accountFilePath) {
        File accountFile = new File(accountFilePath);
        String accountFileName = accountFile.getName();
        String timeFileName = "system_time_" + accountFileName;
        File parentDirectory = accountFile.getAbsoluteFile().getParentFile();
        if (parentDirectory == null) {
            return timeFileName;
        }
        return new File(parentDirectory, timeFileName).getPath();
    }

    static void ensureParentDirectoryExists(String filePath) {
        File parentDirectory = new File(filePath).getAbsoluteFile().getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
    }

    static void migrateLegacyStorageIfNeeded() {
        migrateLegacyFileIfNeeded("accounts.json", DEFAULT_ACCOUNTS_FILE);
        migrateLegacyFileIfNeeded("admin_accounts.json", buildAdminFilePath(DEFAULT_ACCOUNTS_FILE));
        migrateLegacyFileIfNeeded("system_time.json", DEFAULT_TIME_FILE);
    }

    private static void migrateLegacyFileIfNeeded(String legacyFilePath, String newFilePath) {
        Path legacyPath = Paths.get(legacyFilePath);
        Path newPath = Paths.get(newFilePath);
        if (!Files.exists(legacyPath) || Files.exists(newPath)) {
            return;
        }
        try {
            Path parent = newPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.move(legacyPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error organizing account storage files.");
        }
    }
}

class AccountStorage {
    private final String accountsFile;

    AccountStorage(String accountsFile) {
        this.accountsFile = accountsFile;
    }

    List<BankAccount> readAccounts() {
        StoragePaths.ensureParentDirectoryExists(accountsFile);
        File file = new File(accountsFile);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<BankAccount>>(){}.getType();
        try (FileReader reader = new FileReader(file)) {
            List<BankAccount> result = new Gson().fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error reading accounts file.");
            return new ArrayList<>();
        }
    }

    void writeAccounts(List<BankAccount> list) {
        StoragePaths.ensureParentDirectoryExists(accountsFile);
        try (FileWriter writer = new FileWriter(accountsFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(list, writer);
        } catch (IOException e) {
            System.out.println("Error writing accounts file.");
        }
    }
}

class AdminStorage {
    private final String adminFile;

    AdminStorage(String adminFile) {
        this.adminFile = adminFile;
    }

    AdminLoginInfo readAdminLoginInfo() {
        StoragePaths.ensureParentDirectoryExists(adminFile);
        File file = new File(adminFile);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            return new Gson().fromJson(reader, AdminLoginInfo.class);
        } catch (IOException e) {
            System.out.println("Error reading admin account file.");
            return null;
        }
    }

    void writeAdminLoginInfo(AdminLoginInfo adminInfo) {
        StoragePaths.ensureParentDirectoryExists(adminFile);
        try (FileWriter writer = new FileWriter(adminFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(adminInfo, writer);
        } catch (IOException e) {
            System.out.println("Error writing admin file.");
        }
    }
}

class TimeStorage {
    private final String timeFile;

    TimeStorage(String timeFile) {
        this.timeFile = timeFile;
    }

    SystemTime readOrDefault() {
        StoragePaths.ensureParentDirectoryExists(timeFile);
        File file = new File(timeFile);
        if (!file.exists() || file.length() == 0) {
            return new SystemTime(1);
        }
        try (FileReader reader = new FileReader(file)) {
            SystemTime result = new Gson().fromJson(reader, SystemTime.class);
            if (result == null || result.currentDay < 1) {
                return new SystemTime(1);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error reading time file.");
            return new SystemTime(1);
        }
    }

    void write(SystemTime time) {
        StoragePaths.ensureParentDirectoryExists(timeFile);
        try (FileWriter writer = new FileWriter(timeFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(time, writer);
        } catch (IOException e) {
            System.out.println("Error writing time file.");
        }
    }
}

class SystemTime {
    int currentDay;

    SystemTime(int currentDay) {
        this.currentDay = Math.max(1, currentDay);
    }

    int getCurrentDay() {
        return currentDay;
    }

    void resetToDay1() {
        currentDay = 1;
    }

    void advanceDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive.");
        }
        if (currentDay > Integer.MAX_VALUE - days) {
            throw new IllegalArgumentException("Day counter overflow.");
        }
        currentDay += days;
    }
}

class AdminLoginInfo {
    String adminPassword;
    String favoriteColorAnswer;
    String favoriteAnimalAnswer;

    AdminLoginInfo(String adminPassword, String favoriteColorAnswer, String favoriteAnimalAnswer) {
        this.adminPassword = adminPassword;
        this.favoriteColorAnswer = favoriteColorAnswer;
        this.favoriteAnimalAnswer = favoriteAnimalAnswer;
    }
}

