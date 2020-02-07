package com.bangbang93;

import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.ClientInstall;
import net.minecraftforge.installer.actions.ProgressCallback;
import net.minecraftforge.installer.json.Install;
import net.minecraftforge.installer.json.Util;

import java.io.File;

public class ForgeInstaller {
  public static void main(String[] args) {
    SimpleInstaller.headless = true;
    Install install = Util.loadInstallProfile();
    ProgressCallback monitor = ProgressCallback.withOutputs(System.out);
    String path;
    if (args.length == 0) {
      path = ".";
    } else {
      path = args[0];
    }
    ClientInstall action = new ClientInstall(install, monitor);
    try {
      boolean result = action.run(new File(path), (a) -> true);
      monitor.message(Boolean.toString(result));
    } catch (ActionCanceledException e) {
      e.printStackTrace();
    }
  }
}
