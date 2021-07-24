package com.bangbang93;

import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.ClientInstall;
import net.minecraftforge.installer.actions.ProgressCallback;
import net.minecraftforge.installer.json.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class ForgeInstaller {
  public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
    InvocationTargetException, InstantiationException, IllegalAccessException {
    SimpleInstaller.headless = true;
    Class<?> installerClass;
    try {
     installerClass = Class.forName("net.minecraftforge.installer.json.InstallV1");
    } catch (ClassNotFoundException e) {
        installerClass = Class.forName("net.minecraftforge.installer.json.Install");
    }
    Object install = Util.class.getDeclaredMethod("loadInstallProfile").invoke(Util.class);
    ProgressCallback monitor = ProgressCallback.withOutputs(System.out);
    String path;
    if (args.length == 0) {
      path = ".";
    } else {
      path = args[0];
    }
    ClientInstall action = (ClientInstall) Class.forName("net.minecraftforge.installer.actions.ClientInstall")
      .getConstructor(installerClass, ProgressCallback.class).newInstance(install, monitor);
    Method[] methods = ClientInstall.class.getMethods();
    Predicate<String> optionals = (a) -> true;
    Object result = null;
    for (Method method : methods) {
      if (method.getName().equals("run")) {
        if (method.getParameters().length == 2) {
          result = method.invoke(action, new File(path), optionals);
        } else {
          String p = SimpleInstaller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
          result = method.invoke(action, new File(path), optionals, new File(p));
        }
      }
    }
    monitor.message(result != null ? result.toString() : null);
  }
}
