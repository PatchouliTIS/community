package koumakan.javaweb.community.service;

import koumakan.javaweb.community.dao.DiscussPostMapper;
import koumakan.javaweb.community.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @Package: koumakan.javaweb.community.service
 * @Author: Alice Maetra
 * @Date: 2023/3/28 13:39
 * @Decription:
 *      业务逻辑层 演示类
 */


@Service
public class DemoService {


    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper postMapper;


    public String addPost(int userId, String title, String content) {
        transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_SERIALIZABLE);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                System.out.println(114514);
                return null;
            }
        });

        return null;
    }


}
