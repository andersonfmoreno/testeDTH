package test.br.com.ericsson.teltools.dth.baseTest;
import java.lang.reflect.Method;


public class BaseTest {
	
	private Class clazz;
	
	public BaseTest(Class clazz) {
		this.clazz = clazz;
	}

	public Method getMethodByNameAndParams(String name,Class ...paramTypes) throws SecurityException, NoSuchMethodException{
		Method method = this.clazz.getDeclaredMethod(name, paramTypes);
		method.setAccessible(true);
		return method;
	}

}
