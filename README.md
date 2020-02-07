# Forge Install Bootstrapper

Forge的安装器在发布时删除了--installClient的命令行选项，所以我写了这个小程序实现forge从命令行自动安装

# 使用
`java -cp "forge-install-bootstrapper.jar:forge-xxx-installer.jar" com.bangbang93.ForgeInstaller "PathToDotMinecraft"`

-cp参数同时指定本程序和forge的安装程序，最后一个参数是安装位置，请确保目录中有有效的`launcher_profile.json`文件

# 下载
<https://github.com/bangbang93/forge-install-bootstrapper/releases>

# 输出
程序输出内容和Forge安装器原生输出内容一致，未作任何更改

# 如何使用BMCLAPI加速安装
参考<https://github.com/bangbang93/BMCL/blob/master/BMCLV2/Forge/ForgeInstaller.cs>

1. 从BMCLAPI下载原版客户端，并且放置到合适位置
2. 从forge的安装程序中提取version.json，使用BMCLAPI安装所有libraries
3. 从forge的安装程序中提取和install_profile.json，使用BMCLAPI安装所有libraries
4. 使用本程序安装forge，forge会检查本地文件，由于前三步会把所有文件都下载完毕，所以会很快跳过下载，进入安装过程

# 和邻居区别
Referer： <https://github.com/xfl03/ForgeInstallerHeadless>

ForgeInstallerHeadless把forge的原始命令行类拿回来了，恢复了--installClient这个参数，而本程序是直接调用安装方法，所以生成的程序体积更小（大约1.3KB），更适合bundle进启动器内部。
不过ForgeInstallerHeadless优化了输出，请大家自行选择合适的程序。
