package koumakan.javaweb.community;

import koumakan.javaweb.community.dao.IUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext _appContext;

	@Test
	public void textApplicationContext() {
		System.out.println(_appContext);
		IUserDao usrDao = _appContext.getBean(IUserDao.class);
		System.out.println(usrDao.select());
		IUserDao alphaUserDao = _appContext.getBean("alphaUserDao", IUserDao.class);
		System.out.println(alphaUserDao.select());
	}


	/**
	 * 手动获取 Bean 后注入
	 */
	@Test
	public void applicationConfigTest() {
		SimpleDateFormat sdf = _appContext.getBean(SimpleDateFormat.class);
		// do something...
	}


	/**
	 * 自动获取 Bean 后注入
	 */
	@Autowired
	private SimpleDateFormat _sdf;

	@Autowired
	@Qualifier("alphaUserDao")
	private IUserDao _alphaUserDao;

	@Test
	public void autowiredTest() {
		System.out.println(this._alphaUserDao);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this._appContext = applicationContext;
	}
}
