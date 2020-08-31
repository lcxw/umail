package org.edu.mail.usage.search;

import org.edu.mail.usage.UMailConfigure;
import org.edu.mail.usage.util.UMailUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Created by ibu on 2019/2/28.
 */
public abstract class LuceneService extends UMailConfigure {
    protected Logger logger = Logger.getLogger(LuceneService.class.getName());
    // 用户名
    protected String account;
    // 邮件存储路径
    Path storagePath;
    // 邮件索引路径
    Path indexPath;

    LuceneService(String uid, String account) {
        this.account = account;
        storagePath = Paths.get(UMailUtils.getConfigBase().toString(), conf.getProperty("storage.directory"), uid, account);
        indexPath = Paths.get(UMailUtils.getConfigBase().toString(), conf.getProperty("index.directory"), uid, account);
        try {
            if (!Files.exists(indexPath)) {
                indexPath = Files.createDirectories(indexPath);
            }
            if (!Files.exists(storagePath)) {
                storagePath = Files.createDirectories(storagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
