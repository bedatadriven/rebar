package com.bedatadriven.rebar.appcache.test;

import com.bedatadriven.rebar.appcache.test.client.AppVersion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TargetDir {


  public static Path get() {
    String relPath = TargetDir.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    File targetDir = new File(relPath + "../../target");
    if(!targetDir.exists()) {
      boolean created = targetDir.mkdirs();
      assert created;
    }
    return targetDir.toPath().normalize();
  }

  public static Path getWarDir(AppVersion appVersion) {
    return get().resolve(appVersion.getWarDir());
  }
  
  public static Path failSafeReportsDir() throws IOException {
    Path dir = get().resolve("failsafe-reports");
    if(!Files.exists(dir)) {
      Files.createDirectories(dir);
    }
    return dir;
  }
}
