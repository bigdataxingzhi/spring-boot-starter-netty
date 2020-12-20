
**Aop 原理**

`AnnotationAwareAspectJAutoProxyCreator
    --extends--> AspectJAwareAdvisorAutoProxyCreator
        --extends--> AbstractAdvisorAutoProxyCreator
            --extends--> AbstractAutoProxyCreator
                         		implements 
                         		SmartInstantiationAwareBeanPostProcessor,
                         		BeanFactoryAware`
                         		
重点分析

    /*
    一般情况下，此方法始终返回null,幸亏此方法返回null，否则bean就会直接返回而不会经过IOC的创建过程。
    this.advisedBeans 缓存bean是否经过aop代理。
    */
    @Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
		Object cacheKey = getCacheKey(beanClass, beanName);

		if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
			if (this.advisedBeans.containsKey(cacheKey)) {
				return null;
			}
			if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
				this.advisedBeans.put(cacheKey, Boolean.FALSE);
				return null;
			}
		}

		return null;
	}
	
	
	该方法判断bean是否集成了一些切面方法
	protected boolean isInfrastructureClass(Class<?> beanClass) {
		boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
				Pointcut.class.isAssignableFrom(beanClass) ||
				Advisor.class.isAssignableFrom(beanClass) ||
				AopInfrastructureBean.class.isAssignableFrom(beanClass);
		if (retVal && logger.isTraceEnabled()) {
			logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
		}
		return retVal;
	}
	
	// 后置处理器的的初始化结束后的方法，该方法会利用切面，优先使用CGLIB创建代理
	@Override
	public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
		if (bean != null) {
			Object cacheKey = getCacheKey(bean.getClass(), beanName);
			if (this.earlyProxyReferences.remove(cacheKey) != bean) {
				return wrapIfNecessary(bean, beanName, cacheKey);
			}
		}
		return bean;
	}
