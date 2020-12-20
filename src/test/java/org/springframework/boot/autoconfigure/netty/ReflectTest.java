package org.springframework.boot.autoconfigure.netty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ReflectTest {

	private static Map<Person,Integer> test2(){
		return null;
	}
	public static void main(String[] args) throws Exception {
		/**
		 * 方法中
		 * 获取形参列表
		 * 及泛型中的形参列表
		 */
		//1.先获取clazz对象
//		Class.forName("crazyjava.reflect.ReflectTest");
		Class clazz = ReflectTest.class;

		//2.先获取方法
		Method method = clazz.getDeclaredMethod("test", Person.class, List.class, int.class);//传递三个Class的对象 getDeclaredMethod里面是Class<?>... parameterTypes

		Parameter[] parameters = method.getParameters();
		System.out.println("====================================");
		for (Parameter p : parameters) {
			if (List.class.isAssignableFrom(p.getType())) {
				//判断当前参数类型为对象类型 or List(泛型)类型
				if (p.getParameterizedType() instanceof ParameterizedType) {
					Type actualTypeArgument = ((ParameterizedType) p.getParameterizedType()).getActualTypeArguments()[0];

					System.out.println(p.getParameterizedType());
				}
			}
		}
			System.out.println("====================================");

			//3.获取形参类型
			Type[] types = method.getGenericParameterTypes();
			System.out.println("test方法中形参列表的所有形参类型：");
			for (Type type : types) {
				System.out.println(type);
			}

			System.out.println("--------------");
			for (Type type : types) {
				System.out.println("形参类型为：" + type);
				if (type instanceof ParameterizedType) {
					System.out.println("泛型中的参数列表为：");
					Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
					for (Type actualType : actualTypeArguments) {
						System.out.print(actualType.getTypeName() + " , ");
					}
					System.out.println();
				}
			}

			/**
			 * 获取返回值中
			 * 获取参数
			 * 及泛型中的形参列表
			 */
		/*System.out.println("----------测试返回值的");
	    Method method2=clazz.getDeclaredMethod("test2");
		Type returnType = method2.getGenericReturnType();
		System.out.println("返回值的参数类型："+returnType);
		if(returnType instanceof ParameterizedType)
		{
			Type[] actualTypeArguments = ((ParameterizedType)returnType).getActualTypeArguments();
			for(Type actualType:actualTypeArguments) {
				System.out.print(actualType+",");
			}
			
		}*/
		}

		/**
		 * Author: huoxingzhi
		 * Date: 2020/12/16
		 * Email: hxz_798561819@163.com
		 */
	public static class Person {
	}
}