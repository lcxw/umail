package org.edu.mail.usage;

import org.edu.mail.usage.util.UMailUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 邮件配置类，包含邮件本地存储、索引、附件等路径
 */

public abstract class UMailConfigure {
    private static final String WINDOW_CONF_FILE_NAME = "d:\\umail\\conf\\window.conf.properties";
    private static final String UNIX_CONF_FILE_NAME = "/usr/myprogram/umail/conf/unix.conf.properties";
    // 配置
    protected Properties conf = new Properties();
    // 路径分隔符
    protected final String separator = File.separator;

    protected UMailConfigure() {
        final Logger log = Logger.getLogger(UMailConfigure.class.getName());
        try {
            InputStream is = null;
            // 首先判断时window还是unix系统，加载不同的配置文件
            log.info("当前操作环境:" + System.getProperty("os.name"));
            log.info("预加载配置文件：conf/conf.properties");
            // 使用绝对路径解决ClassLoader.getSystemResourceAsStream($fileName)在WEB容器中就报NULL错误
            is = ClassLoader.getSystemResourceAsStream("conf" + separator + "conf.properties");
            if (is == null) {
                is = new FileInputStream(new File(UMailUtils.getServerConfig().toString()));
                log.info("加载配置文件：" + new File(UMailUtils.getServerConfig().toString()).getAbsolutePath());
            }
            conf.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
