package com.bangbang93;

import com.google.gson.Gson;
import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.ClientInstall;
import net.minecraftforge.installer.actions.ProgressCallback;
import net.minecraftforge.installer.json.Install;
import net.minecraftforge.installer.json.Mirror;
import net.minecraftforge.installer.json.Util;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ForgeInstaller {
  public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
    InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    SimpleInstaller.headless = true;
    Class<?> installerClass;
    try {
     installerClass = Class.forName("net.minecraftforge.installer.json.InstallV1");
    } catch (ClassNotFoundException e) {
        installerClass = Class.forName("net.minecraftforge.installer.json.Install");
    }
    Object install = Util.class.getDeclaredMethod("loadInstallProfile").invoke(Util.class);

    Field processorField = Class.forName("net.minecraftforge.installer.json.Install").getDeclaredField("processors");
    processorField.setAccessible(true);
    List<Install.Processor> processors = (List<Install.Processor>) processorField.get(install);
    List<Install.Processor> target = processors.stream().filter((processor -> {
      String[] processorArgs = processor.getArgs();
      return Arrays.asList(processorArgs).contains("DOWNLOAD_MOJMAPS");
    }))
      .collect(Collectors.toList());
    processors.removeAll(target);

    Mirror mirror = new Gson().fromJson(new StringReader(
      "{\n" +
        "    \"name\": \"bmclapi\",\n" +
        "    \"url\": \"http://bmclapi.bangbang93.com/maven/\"\n" +
        "}"
    ), Mirror.class);
    Field mirrorField = Class.forName("net.minecraftforge.installer.json.Install").getDeclaredField("mirror");
    mirrorField.setAccessible(true);
    mirrorField.set(install, mirror);

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
    String installerPath = SimpleInstaller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    Predicate<String> optionals = (a) -> true;
    Object result = null;
    for (Method method : methods) {
      if (method.getName().equals("run")) {
        if (method.getParameters().length == 2) {
          if (method.getParameters()[1].getType().equals(Predicate.class)) {
            result = method.invoke(action, new File(path), optionals);
          } else {
            result = method.invoke(action, new File(path), new File(installerPath));
          }
        } else {
          result = method.invoke(action, new File(path), optionals, new File(installerPath));
        }
      }
    }
    monitor.message(result != null ? result.toString() : null);
  }
}
