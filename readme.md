## 说明
1. 使用Java Californium 框架
2. 使用MAVEN构建

## 操作步骤
### 新建工程
![](http://ovqdgsm5c.bkt.clouddn.com/17-11-4/21770248.jpg)

### 修改POM文件
1. 增加mainclass定义
``` xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <!--定义一个mainclass变量-->
    <assembly.mainClass>org.iotwuxi.App</assembly.mainClass>
</properties>
```
2. 增加依赖项
```
<dependencies>
    ...
    <dependency>
        <groupId>org.eclipse.californium</groupId>
        <artifactId>californium-core</artifactId>
        <version>2.0.0-M4</version>
    </dependency>
    <dependency>
        <groupId>org.eclipse.californium</groupId>
        <artifactId>element-connector</artifactId>
        <version>2.0.0-M4</version>
    </dependency>
</dependencies>
```
3. 增加maven-assembly-plugin
```
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <appendAssemblyId>false</appendAssemblyId>
                <archive>
                    <manifest>
                        <addClasspath>true</addClasspath>
                        <mainClass>${assembly.mainClass}</mainClass>
                        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 运行
选择正确的main class文件  
![](http://ovqdgsm5c.bkt.clouddn.com/17-11-4/89726294.jpg)

### 生成JAR
在target文件中生成jar可执行文件，点击运行  
![](http://ovqdgsm5c.bkt.clouddn.com/17-11-4/90923166.jpg)