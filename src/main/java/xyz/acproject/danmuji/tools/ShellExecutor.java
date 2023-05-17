package xyz.acproject.danmuji.tools;

/**
 * @Author: zhou
 * @Description:
 * @Data: 2023-05-10
 */
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import xyz.acproject.danmuji.client.Websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: zhou
 * @Description: 以上代码定义了一个`ShellExecutor`类，使用`@Component`注解将其标记为Spring Bean组件。该类中包含一个`shellExecutor`方法，
 * 用于执行指定路径下的shell脚本。其中，`command`变量使用了`@Value`注解，从配置文件中读取了shell脚本的路径。在`shellExecutor`方法中，我们
 * 使用`Runtime.getRuntime().exec(command)`方法执行了shell脚本，并使用`BufferedReader`读取脚本执行的结果。在`while`循环中，我们将脚本执行
 * 结果输出到控制台，并使用`Log4j2`输出到日志中。最后，我们使用`process.waitFor()`方法等待脚本执行完毕。需要注意的是，在执行shell脚本时，可能
 * 需要设置相应的环境变量或使用`sudo`命令执行脚本，具体实现方式需要根据具体情况而定。
 * @param
 * @Data: 2023-05-10
 * @return:
 */

//@Component
//public class ShellExecutor {
//
//    private static Logger LOGGER = LogManager.getLogger(ShellExecutor.class);
//    @Value("${danmuji.url}")
//    private String command;// shell脚本的路径
//
//    public  void shellExecutor() {
//        try {
//            LOGGER.info("shell脚本的路径:" + command);
//            Process process = Runtime.getRuntime().exec(command);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                LOGGER.info("调用脚本，脚本执行结果:" + line);
//            }
//            process.waitFor();
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}

public class ShellExecutor {

    private static final Logger LOGGER = LogManager.getLogger(ShellExecutor.class);
    public static String command = "/home/zbo/kplayerStart.sh";

    private ShellExecutor() {
        // 防止被实例化
    }




    public static void shellExecutor() {
        try {
            LOGGER.info("shell脚本的路径:" + command);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("调用脚本，脚本执行结果:" + line);
            }
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            LOGGER.error("执行shell脚本失败", e);
        }
    }
}